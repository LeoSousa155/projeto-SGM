import greenfoot.*;
import java.util.*;

public class BiologistMinigameController
{
    // ===== REOPEN COOLDOWN (optional like others) =====
    private static int reopenCooldown = 0;
    public static boolean canReopen() { return reopenCooldown == 0; }
    public static void tickReopenTimer() { if (reopenCooldown > 0) reopenCooldown--; }

    private final World world;
    private PanelBoard board;
    private boolean finished = false;

    // ===== Persistent state across close/reopen (until recompile) =====
    private static enum Phase { FIGURE_OUT, DECIDE_FATE }
    private static Phase savedPhase = Phase.FIGURE_OUT;
    private static FishermanFishData.FishAttempt savedCurrent = null;
    private static int savedSetSeed = 0;

    // ===== Runtime state =====
    private Phase phase;
    private FishermanFishData.FishAttempt current;
    private Random rng;

    // Runs after the current message finishes displaying
    private Runnable pendingAfterMessage = null;

    // If true, update() will re-render AFTER the message ends
    private boolean pendingRender = false;

    // UI refs
    private Text headerText;
    private Text messageText;
    private int messageTimer = 0;
    private Text instructionText;

    private BiologistDraggableFish fishActor;
    private BiologistDropZone[] zones = new BiologistDropZone[3];

    // descriptions data
    private FishSpecies correctSpecies;
    private FishSpecies[] optionSpecies = new FishSpecies[3];

    // Fate buttons
    private Button keepButton;
    private Button releaseButton;

    public interface ResultListener {
        void onBiologistDone();
    }
    private final ResultListener listener;

    public BiologistMinigameController(World world, PanelBoard board, ResultListener listener)
    {
        this.world = world;
        this.board = board;
        this.listener = listener;

        board.attachControllerAdapter(new PanelBoard.ControllerAdapter() {
            public void update() { BiologistMinigameController.this.update(); }
        });

        if (!MinigameLock.tryLock())
        {
            board.destroy();
            return;
        }

        MinigameLock.registerForceClose(() -> {
            finished = true;
            cleanup();
        });
        
        setupOrRestoreState();

        if (this.current == null)
            return;

        renderCurrentPhase();
    }

    private void setupOrRestoreState()
    {
        this.phase = savedPhase;

        if (savedCurrent != null)
            this.current = savedCurrent;
        else
        {
            this.current = FishermanFishData.peekNextPending();
            savedCurrent = this.current;
        }

        if (this.current == null)
        {
            finished = true;
            phase = Phase.FIGURE_OUT;
            savedPhase = phase;
            savedSetSeed = 0;

            renderEmptyPanelMessage();
            return;
        }

        if (savedSetSeed == 0)
            savedSetSeed = (int)(System.currentTimeMillis() & 0x7fffffff);

        this.rng = new Random(savedSetSeed);
    }

    private void renderEmptyPanelMessage()
    {
        if (board == null) return;

        board.clearContents();
        board.addContent(new EscListener(), 0, 0);

        Button closeButton = new Button("X", "button1.png", 40, 40, () -> closeFromPlayer());
        int offsetX = board.getHalfWidth() - closeButton.getImage().getWidth() / 2 - 5;
        int offsetY = -board.getHalfHeight() + closeButton.getImage().getHeight() / 2 + 5;
        board.addContent(closeButton, offsetX, offsetY);

        Text t = new Text("No new fish to examine, go fish some!", 30, Color.WHITE, true);
        board.addContent(t, 0, 0);

        // also clear any message state
        clearMessageState();
    }

    private void renderCurrentPhase()
    {
        if (board == null) return;

        if (current == null)
        {
            renderEmptyPanelMessage();
            return;
        }

        board.clearContents();
        board.addContent(new EscListener(), 0, 0);

        Button closeButton = new Button("X", "button1.png", 40, 40, () -> closeFromPlayer());
        int offsetX = board.getHalfWidth() - closeButton.getImage().getWidth() / 2 - 5;
        int offsetY = -board.getHalfHeight() + closeButton.getImage().getHeight() / 2 + 5;
        board.addContent(closeButton, offsetX, offsetY);

        if (phase == Phase.FIGURE_OUT) renderFigureOut();
        else renderDecideFate();
    }

    // ===========================
    // Phase 1: Figure out the fish
    // ===========================
    private void renderFigureOut()
    {
        fishActor = null;
        Arrays.fill(zones, null);

        headerText = new Text("Identify the fish", 30, Color.WHITE, true);
        board.addContent(headerText, 0, -board.getHalfHeight() + 30);

        instructionText = new Text("Drag the fish onto the matching description.", 26, Color.CYAN, true);
        board.addContent(instructionText, 0, -board.getHalfHeight() + 70);

        correctSpecies = current.fish;

        List<FishSpecies> decoys = pickDecoys(correctSpecies, 2, rng);
        List<FishSpecies> all = new ArrayList<>();
        all.add(correctSpecies);
        all.addAll(decoys);
        Collections.shuffle(all, rng);
        optionSpecies = all.toArray(new FishSpecies[0]);

        fishActor = new BiologistDraggableFish(correctSpecies.imageFile, this);
        board.addContent(fishActor, -board.getHalfWidth()/2 + 20, -40);

        Text leftStats = new Text(buildFullStatsText(current), 24, Color.WHITE, true);
        board.addContent(leftStats, -board.getHalfWidth()/2 + 20, 100);

        int rightX = board.getHalfWidth()/2 - 20;
        int baseY = -90;
        int gapY = 120;

        for (int i = 0; i < 3; i++)
        {
            String partial = buildPartialStatsText(optionSpecies[i], rng);
            zones[i] = new BiologistDropZone(i, partial);
            board.addContent(zones[i], rightX, baseY + i * gapY);
        }
    }

    private List<FishSpecies> pickDecoys(FishSpecies correct, int count, Random rng)
    {
        List<FishSpecies> pool = new ArrayList<>(FishermanFishData.getAllSpecies());
        pool.remove(correct);
        Collections.shuffle(pool, rng);

        if (pool.size() < count) return pool;
        return pool.subList(0, count);
    }

    public void onFishDropped(int worldX, int worldY)
    {
        if (finished) return;
        if (board == null) return;
        if (board.getWorld() == null) return;
        if (optionSpecies == null) return;
        if (zones == null) return;
        if (phase != Phase.FIGURE_OUT) return;
        if (messageTimer > 0) return; // optional: prevents spamming while message is showing

        for (int i = 0; i < zones.length; i++)
        {
            BiologistDropZone z = zones[i];
            if (z != null && z.containsPoint(worldX, worldY))
            {
                FishSpecies chosen = optionSpecies[z.getIndex()];

                if (chosen == correctSpecies)
                {
                    int reward = current.value / 2;
                    MoneyDisplay.addMoney(reward);
                
                    if (finished || board == null || board.getWorld() == null) return;
                
                    phase = Phase.DECIDE_FATE;
                    savedPhase = phase;
                
                    showMessage(
                        "Correct! (+" + reward + "$)",
                        Color.GREEN,
                        60,
                        -board.getHalfWidth()/2 + 20,
                        -80
                    );
                
                    pendingAfterMessage = null;
                    pendingRender = true;
                }
                else
                {
                    savedSetSeed = (int)(System.currentTimeMillis() & 0x7fffffff);
                    rng = new Random(savedSetSeed);

                    showMessage("Wrong. Try again.", Color.RED, 90, -board.getHalfWidth()/2 + 20, -80);

                    pendingAfterMessage = null;
                    pendingRender = true;
                }

                return;
            }
        }

        if (fishActor != null) fishActor.snapHome();
    }

    // ===========================
    // Phase 2: Decide fish fate
    // ===========================
    private void renderDecideFate()
    {
        headerText = new Text("Decide the fish's fate", 30, Color.WHITE, true);
        board.addContent(headerText, 0, -board.getHalfHeight() + 30);

        fishActor = new BiologistDraggableFish(current.fish.imageFile, this);
        fishActor.setDraggable(false);
        board.addContent(fishActor, 0, -100);

        Text stats = new Text(buildFullStatsText(current), 24, Color.WHITE, true);
        board.addContent(stats, 0, 50);

        releaseButton = new Button("Release", "button1.png", 200, 60, () -> onRelease());
        keepButton    = new Button("Keep", "button1.png", 200, 60, () -> onKeep());

        board.addContent(releaseButton, -200, board.getHalfHeight() - 60);
        board.addContent(keepButton,     200, board.getHalfHeight() - 60);

        instructionText = new Text("Release if endangered OR too small.", 26, Color.RED, true);
        board.addContent(instructionText, 0, -board.getHalfHeight() + 70);
    }

    private boolean isEndangered(FishSpecies fish)
    {
        return fish.threatStatus != null && fish.threatStatus.toLowerCase().contains("endangered");
    }

    private boolean isTooSmall(FishermanFishData.FishAttempt a)
    {
        return a.currentSize != null && a.currentSize.equalsIgnoreCase("too small");
    }

    private void onRelease()
    {
        if (finished) return;
        if (phase != Phase.DECIDE_FATE) return;
        if (messageTimer > 0) return;

        FishermanFishData.logBiologistRelease(); 
        
        int value = current.value;
        MoneyDisplay.addMoney(-value);

        boolean bad = isEndangered(current.fish) || isTooSmall(current);

        if (bad) showMessage("Released an illegal fish. (-" + value + "$)", Color.GREEN, 120, 0, -100);
        else     showMessage("Released a legal fish. (-" + value + "$)", Color.RED, 120, 0, -100);

        pendingAfterMessage = () -> consumeCurrentAndContinue();
        pendingRender = true;
    }

    private void onKeep()
    {
        if (finished) return;
        if (phase != Phase.DECIDE_FATE) return;
        if (messageTimer > 0) return;

        int value = current.value;
        boolean bad = isEndangered(current.fish) || isTooSmall(current);

        if (bad)
        {
            MoneyDisplay.addMoney(-(value * 2));
            showMessage("That's illegal! (-" + (value * 2) + "$)", Color.RED, 120, 0, -100);
        }
        else
        {
            showMessage("Stored. Next Fish...", Color.GREEN, 120, 0, -100);
        }

        pendingAfterMessage = () -> consumeCurrentAndContinue();
        pendingRender = true;
    }

    private void consumeCurrentAndContinue()
    {
        FishermanFishData.popNextPending();

        current = FishermanFishData.peekNextPending();
        savedCurrent = current;

        if (current == null)
        {
            phase = Phase.FIGURE_OUT;
            savedPhase = phase;
            savedSetSeed = 0;

            finished = true;
            renderEmptyPanelMessage();
            return;
        }

        finished = false;
        phase = Phase.FIGURE_OUT;
        savedPhase = phase;

        savedSetSeed = (int)(System.currentTimeMillis() & 0x7fffffff);
        rng = new Random(savedSetSeed);
    }

    // ===========================
    // Text building
    // ===========================
    private String buildFullStatsText(FishermanFishData.FishAttempt a)
    {
        FishSpecies f = a.fish;
        return
            "Species: " + f.species + "\n" +
            "Rarity: " + f.rarity.label + "\n" +
            "Species Size: " + f.speciesSize + "\n" +
            "Current Size: " + a.currentSize + "\n" +
            "Depth: " + f.depth + "\n" +
            "Habitat: " + f.habitat + "\n" +
            "Diet: " + f.diet + "\n" +
            "Threat status: " + f.threatStatus;
    }

    private String buildPartialStatsText(FishSpecies f, Random rng)
    {
        List<String> fields = new ArrayList<>();
        fields.add("Rarity: " + f.rarity.label);
        fields.add("Species Size: " + f.speciesSize);
        fields.add("Depth: " + f.depth);
        fields.add("Habitat: " + f.habitat);
        fields.add("Diet: " + f.diet);

        Collections.shuffle(fields, rng);

        return fields.get(0) + "\n" + fields.get(1) + "\n" + fields.get(2);
    }

    // ===========================
    // Messaging + update loop
    // ===========================
    private void clearMessageState()
    {
        if (messageText != null && messageText.getWorld() != null)
            board.removeContent(messageText);

        messageText = null;
        messageTimer = 0;
        pendingAfterMessage = null;
        pendingRender = false;
    }

    private void showMessage(String msg, Color c, int frames, int offsetX, int offsetY)
    {
        if (board == null) return;
        if (board.getWorld() == null) return;
        
        if (messageText != null && messageText.getWorld() != null)
            board.removeContent(messageText);
    
        messageText = new Text(msg, 26, c, true);
        board.addContent(messageText, offsetX, offsetY);
    
        messageTimer = frames;
    }
    
    private void showMessage(String msg, Color c, int frames)
    {
        showMessage(msg, c, frames, 0, 0);
    }

    public void update()
    {
        if (board == null) return;
        if (board.getWorld() == null) return;
        if (current == null) return;

        if (messageTimer > 0)
        {
            messageTimer--;

            if (messageTimer == 0)
            {
                // Always remove message when timer ends
                if (messageText != null && messageText.getWorld() != null)
                    board.removeContent(messageText);
                messageText = null;

                // Run deferred action (phase 2 consume)
                if (pendingAfterMessage != null)
                {
                    Runnable r = pendingAfterMessage;
                    pendingAfterMessage = null;
                    r.run();
                }

                // Re-render if requested
                if (pendingRender)
                {
                    pendingRender = false;

                    // consumeCurrentAndContinue() might already have rendered the empty panel
                    if (current != null)
                        renderCurrentPhase();
                }
            }
        }
    }

    private void closeFromSystem()
    {
        cleanup();
        MinigameLock.setLocked(false);
        reopenCooldown = 30;

        if (listener != null)
            listener.onBiologistDone();
    }

    private void closeFromPlayer()
    {
        savedPhase = phase;
        savedCurrent = current;

        cleanup();
        MinigameLock.setLocked(false);
        reopenCooldown = 30;
    }

    private void cleanup()
    {
        finished = true;
    
        optionSpecies = null;
        zones = null;
        fishActor = null;
    
        if (board != null)
        {
            board.destroy();
            board = null;
        }
    }

    private class EscListener extends Actor
    {
        private boolean escConsumed = false;

        public EscListener()
        {
            setImage(new GreenfootImage(1, 1));
        }

        public void act()
        {
            boolean escDown = Greenfoot.isKeyDown("escape");
            if (!escDown)
            {
                escConsumed = false;
                return;
            }
            if (escConsumed) return;

            escConsumed = true;
            closeFromPlayer();
        }
    }
}