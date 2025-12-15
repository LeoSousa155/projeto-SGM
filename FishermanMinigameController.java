import greenfoot.*;

public class FishermanMinigameController
{
    // ===== REOPEN COOLDOWN =====
    private static int reopenCooldown = 0;
    public static boolean canReopen() { return reopenCooldown == 0; }
    public static void tickReopenTimer() { if (reopenCooldown > 0) reopenCooldown--; }

    private final World world;
    private PanelBoard board;

    private boolean finished = false;

    private Text infoText;
    private Text resultText;
    private Text countdownText;

    private int exitTimer = 0;

    private FishermanSkillCheckWheel wheel;

    // progression
    private final int requiredHits;
    private int currentHits = 0;

    private int misses = 0;
    private int maxMisses = 3;
    
    private int missCooldownFrames = 0;
    private static final int MISS_COOLDOWN_DURATION = 20; // 0.33s at 60fps
    
    // Fish rolled for this minigame instance
    private FishSpecies targetFish;

    // --- countdown ---
    private enum State { COUNTDOWN, ACTIVE }
    private State state = State.COUNTDOWN;
    private int countdownFrames = 180; // 3 seconds @ ~60fps

    // Input debounce (E only)
    private boolean eArmed = false;     // becomes true only after E is released once
    private boolean canConsumePress = true;

    // Zone pass logic
    private boolean wasInWindow = false;
    private boolean hitRegisteredThisPass = false;

    // Next-zone safety (degrees). Tune these to match your image.
    private double nextMinSeparationDeg = 90.0;
    private double nextMaxSeparationDeg = 170.0;

    public interface ResultListener {
        void onFishermanMinigameSuccess();
        void onFishermanMinigameFailure();
    }

    private final ResultListener listener;

    public FishermanMinigameController(World world, PanelBoard board, ResultListener listener, int requiredHits)
    {
        this.world = world;
        this.board = board;
        this.listener = listener;
        this.requiredHits = Math.max(1, requiredHits);

        board.attachControllerAdapter(new PanelBoard.ControllerAdapter() {
            public void update() { FishermanMinigameController.this.update(); }
        });

        if (!MinigameLock.tryLock())
        {
            board.destroy();
            return;
        }
        
        MinigameLock.registerForceClose(() -> {
            finished = true;
            cleanup(); // destroys panelboard safely
        });

        setupMinigame();
    }

    private void setupMinigame()
    {
        if (board == null) return;
        
        // Roll which fish this minigame is about
        targetFish = FishermanFishData.rollFish();

        wheel = new FishermanSkillCheckWheel(
            320,   // size
            90.0,   // initial needle angle
            3.8,   // speed
            28.0   // window half-width
        );
        
        // First success zone always opposite the cursor (180 degrees away)
        wheel.setTargetAngleDeg(wheel.getNeedleAngleDeg() + 180.0);
        wheel.setPaused(true); // paused during countdown
        board.addContent(wheel, 0, 10);

        infoText = new Text(
            "Fish: " + targetFish.rarity.label + "   " +
            "Hits: " + currentHits + " / " + requiredHits +
            "   Misses: " + misses + " / " + maxMisses,
            22,
            Color.WHITE
        );
        board.addContent(infoText, 0, -board.getHalfHeight() + 40);

        countdownText = new Text("READY", 48, Color.WHITE);
        board.addContent(countdownText, 0, 0);

        // ESC listener
        board.addContent(new EscListener(), 0, 0);

        // X button
        Button closeButton = new Button(
            "X", "button1.png", 40, 40,
            () -> closeFromPlayer()
        );

        int offsetX = board.getHalfWidth() - closeButton.getImage().getWidth() / 2 - 5;
        int offsetY = -board.getHalfHeight() + closeButton.getImage().getHeight() / 2 + 5;
        board.addContent(closeButton, offsetX, offsetY);
    }

        private boolean isMissOnCooldown()
    {
        return missCooldownFrames > 0;
    }
    
    private void startMissCooldown()
    {
        missCooldownFrames = MISS_COOLDOWN_DURATION;
    }

    private void setCountdownLabel(String text)
    {
        if (!boardAlive()) return;
        
        if (countdownText != null && countdownText.getWorld() != null)
            board.removeContent(countdownText);

        countdownText = new Text(text, 48, Color.WHITE);
        board.addContent(countdownText, 0, 0);
    }

    private void hideCountdown()
    {
        if (!boardAlive()) return;
        
        if (countdownText != null && countdownText.getWorld() != null)
            board.removeContent(countdownText);
        countdownText = null;
    }

    private boolean boardAlive()
    {
        return board != null && board.getWorld() != null;
    }
    
    private void showResult(String msg, Color c)
    {
        if (!boardAlive()) return;
        
        if (resultText != null && resultText.getWorld() != null)
            board.removeContent(resultText);

        resultText = new Text(msg, 28, c);
        board.addContent(resultText, 0, 0);
    }

    private void updateInfo()
    {
        if (!boardAlive()) return;
        
        if (infoText != null && infoText.getWorld() != null)
            board.removeContent(infoText);

        infoText = new Text(
            "Fish: " + targetFish.rarity.label + "   " +
            "Hits: " + currentHits + " / " + requiredHits +
            "   Misses: " + misses + " / " + maxMisses,
            22,
            Color.WHITE
        );
        board.addContent(infoText, 0, -board.getHalfHeight() + 40);
    }

    private void beginActive()
    {
        state = State.ACTIVE;
        hideCountdown();

        // Important: prevent the "trigger press" from counting.
        // We only arm input after E is released at least once.
        eArmed = false;
        canConsumePress = true;

        // Reset pass tracking
        wasInWindow = wheel.isNeedleInWindow();
        hitRegisteredThisPass = false;

        wheel.setPaused(false);
    }

    private void registerMiss(String reason)
    {
        if (isMissOnCooldown())
            return;
    
        startMissCooldown();
    
        misses++;
        updateInfo();
        showResult("Miss! (" + misses + "/" + maxMisses + ")", Color.RED);
    
        if (misses >= maxMisses)
            notifyFailure();
    }

    private void onHitSuccess()
    {
        currentHits++;
        updateInfo();
        showResult("Nice!", Color.GREEN);

        // Immediately choose a new target, but not too close to the previous
        wheel.randomizeNextTarget(nextMinSeparationDeg, nextMaxSeparationDeg);

        // Reset pass tracking for the new zone
        wasInWindow = wheel.isNeedleInWindow();
        hitRegisteredThisPass = false;

        if (currentHits >= requiredHits)
            notifySuccess();
    }

    private void notifySuccess()
    {
        if (finished) return;
        finished = true;
        wheel.setPaused(true);
    
        int value = targetFish.rarity.value;
    
        MoneyDisplay.addMoney(+value);
        FishermanFishData.logAttempt(targetFish, true);
        
        if (!boardAlive() || finished && board == null) return;
    
        showResult(
            "You caught a " + targetFish.rarity.label + " fish! (+" + value + "$)",
            Color.GREEN
        );
    
        exitTimer = 180; // 3 seconds
    }

    private void notifyFailure()
    {
        if (finished) return;
        finished = true;
        wheel.setPaused(true);
    
        int value = targetFish.rarity.value;
    
        MoneyDisplay.addMoney(-value);
        FishermanFishData.logAttempt(targetFish, false);
    
        if (!boardAlive() || finished && board == null) return;
        
        showResult(
            "You lost a " + targetFish.rarity.label + " fish... (-" + value + "$)",
            Color.RED
        );
    
        exitTimer = 180; // 3 seconds
    }

    public void update()
    {
        if (!boardAlive()) return;
        if (finished && exitTimer == 0) return;
        if (missCooldownFrames > 0)
            missCooldownFrames--;
        
        // Close timer
        if (exitTimer > 0)
        {
            exitTimer--;
            if (exitTimer == 0)
            {
                cleanup();
                MinigameLock.setLocked(false);
                reopenCooldown = 60;

                if (listener != null)
                {
                    if (currentHits >= requiredHits)
                        listener.onFishermanMinigameSuccess();
                    else
                        listener.onFishermanMinigameFailure();
                }
            }
            return;
        }

        // ===== COUNTDOWN =====
        if (state == State.COUNTDOWN)
        {
            // Block any early E input from being treated as a miss:
            // we do not even read skillcheck input in this state.
            countdownFrames--;

            if (countdownFrames > 120)      setCountdownLabel("READY");
            else if (countdownFrames > 60)  setCountdownLabel("SET");
            else if (countdownFrames > 0)   setCountdownLabel("FISH!");

            if (countdownFrames <= 0)
                beginActive();

            return;
        }

        // ===== ACTIVE =====
        // 1) Zone enter/exit miss rule
        boolean inWindow = wheel.isNeedleInWindow();

        // If we just ENTERED the window, start a new "pass"
        if (!wasInWindow && inWindow)
        {
            hitRegisteredThisPass = false;
        }

        // If we EXITED the window without hitting -> miss
        if (!isMissOnCooldown())
        {
            if (wasInWindow && !inWindow)
            {
                if (!hitRegisteredThisPass)
                    registerMiss("exit-without-hit");
            }
        }

        wasInWindow = inWindow;

        // 2) E-only input with arming (must release first)
        boolean eDown = Greenfoot.isKeyDown("e");

        if (eDown)
        {
            // If E is held, only one press counts
            if (!canConsumePress) return;

            // If E hasn't been released since active began, ignore (no miss)
            if (!eArmed) return;

            canConsumePress = false;

            // Press while inside window = success; outside = miss
            if (inWindow)
            {
                hitRegisteredThisPass = true;
                onHitSuccess();
            }
            else
            {
                if (!isMissOnCooldown())
                    registerMiss("pressed-outside");
            }

            return;
        }

        // Key released
        canConsumePress = true;

        // Once we see E released at least once after countdown, we arm it
        if (!eArmed)
            eArmed = true;
    }

    private void closeFromPlayer()
    {
        if (finished) return;
        finished = true;

        cleanup();
        MinigameLock.setLocked(false);
        reopenCooldown = 60;
        // manual abort: no callbacks
    }

    private void cleanup()
    {
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
            if (finished) return;

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