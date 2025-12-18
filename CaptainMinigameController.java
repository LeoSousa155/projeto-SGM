import greenfoot.*;

/**
 * Handles logic for the Captain minigame.
 *
 * Responsibilities:
 *  - Create boat, background, rocks, and goal zone on top of a PanelBoard.
 *  - Receive events from the boat (hit rock / reached goal).
 *  - Handle success/failure, cleanup, and notify the parent world.
 *  - Provide an X button and ESC key to close the minigame immediately.
 */
public class CaptainMinigameController
{
    // ====== GLOBAL MOVEMENT LOCK (used by ControllablePlayer) ======
    private static boolean controlsLocked = false;
    
    private boolean hitAnyRock = false;
    private boolean decidedNeedsRepair = false;

    /** Lock/unlock character movement globally while the minigame is active. */
    public static void setControlsLocked(boolean locked)
    {
        controlsLocked = locked;
    }

    /** True while any Captain minigame is running. */
    public static boolean areControlsLocked()
    {
        return controlsLocked;
    }

    // ====== REOPEN COOL-DOWN (used by CaptainMinigameTrigger) ======
    // Frames remaining before the minigame can be opened again.
    private static int reopenCooldown = 0;

    /** True if the trigger is allowed to open the minigame. */
    public static boolean canReopen()
    {
        return reopenCooldown == 0;
    }

    /** Called once per frame by the main world to count down the reopen timer. */
    public static void tickReopenTimer()
    {
        if (reopenCooldown > 0)
            reopenCooldown--;
    }
    // ===============================================================

    private World world;
    private PanelBoard board;

    private CaptainBoat boat;
    private boolean finished = false;

    private Text successMessage;
    private int exitTimer = 0;   // frames until minigame closes after success

    /** Callback interface to notify the game world when the minigame is over. */
    public interface ResultListener {
        void onCaptainMinigameSuccess();
        void onCaptainMinigameFailure();
    }

    private ResultListener listener;

    public CaptainMinigameController(World world, PanelBoard board, ResultListener listener)
    {
        this.world = world;
        this.board = board;
        this.listener = listener;

        // Allow board to call update() each frame
        board.attachControllerAdapter(new PanelBoard.ControllerAdapter() {
            public void update() { CaptainMinigameController.this.update(); }
        });

        // Lock normal character controls while the minigame is active
        if (!MinigameLock.tryLock())
        {
            // Another minigame is already open; just destroy this board and abort.
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

        // --- Background inside the panel (drawn behind everything else) ---
        CaptainGameBackground bg = new CaptainGameBackground();
        board.addContent(bg, 0, 0);

        // --- Create the boat ---
        boat = new CaptainBoat(board, this);
        board.addContent(boat, -250, 150); // center of board

        // Boat collision box (smaller than sprite)
        CollisionBox boatBox = new CollisionBox(boat, 40, 25, 0, 0);
        board.addContent(boatBox, 0, 0);
        boat.setCollisionBox(boatBox);
        
        // --- Place goal zone ---
        CaptainGoalZone goal = new CaptainGoalZone();
        board.addContent(goal, 250, -150);
        
        // Goal collision box (tight around the fish body)
        CollisionBox goalBox = new CollisionBox(goal, 35, 20, 0, 0);
        board.addContent(goalBox, 0, 0);

        // --- Place a few rocks ---
        addRock(200, 205);
        addRock(150, 190);
        addRock(100, 150);
        addRock(50, 110);
        addRock(50, -100);
        addRock(70, -50);
        addRock(30, -150);
        addRock(0, 90);
        addRock(0, -190);
        addRock(-50, -205);
        addRock(-50, 70);
        addRock(-100, 50);
        addRock(-150, 30);
        addRock(100, 0);
        addRock(150, 40);
        
        // --- ESC key listener (invisible actor on top of the board) ---
        EscListener escListener = new EscListener();
        board.addContent(escListener, 0, 0);

        // --- "X" close button on the top-right of the panel ---
        Button closeButton = new Button(
            "X",
            "button1.png",
            40, 40,
            () -> closeFromPlayer()  // same behaviour as ESC
        );

        int offsetX = board.getHalfWidth() - closeButton.getImage().getWidth() / 2 - 5;
        int offsetY = -board.getHalfHeight() + closeButton.getImage().getHeight() / 2 + 5;
        board.addContent(closeButton, offsetX, offsetY);
    }

    private void addRock(int offsetX, int offsetY)
    {
        CaptainRock rock = new CaptainRock();
        board.addContent(rock, offsetX, offsetY);
        
        rock.attachCollisionBox(board);
    }

    public void onBoatHitRock()
    {
        hitAnyRock = true;
    }

    /**
     * Old immediate-success hook, kept for compatibility.
     * If you no longer call this, you can delete it.
     */
    public void onBoatReachedGoal()
    {
        notifyGoalReached();
    }

    public void notifyGoalReached()
    {
        if (finished) return;
        finished = true;
    
        MoneyDisplay.addMoney(50);
    
        if (!boardAlive()) return;
    
        boolean needsRepairNow;
    
        if (TutorialController.isTutorialMode())
        {
            needsRepairNow = true;
        }
        else
        {
            // 100% if you hit at least one rock, otherwise 50%
            if (hitAnyRock) needsRepairNow = true;
            else needsRepairNow = (Greenfoot.getRandomNumber(2) == 0);
        }
    
        decidedNeedsRepair = needsRepairNow;
    
        String msg = needsRepairNow
            ? "You arrived! But the engine needs repairs... (+50$)"
            : "You arrived! (+50$)";
    
        successMessage = new Text(msg, 32, Color.GREEN, true);
        board.addContent(successMessage, 0, 0);
    
        if (needsRepairNow)
            EngineRepairState.setNeedsRepair(true);
    
        exitTimer = 120;
    }

    private boolean boardAlive()
    {
        return board != null && board.getWorld() != null;
    }
    
    /**
     * Called every frame from PanelBoard.act().
     * Handles the delayed closing after success.
     */
    public void update()
    {
        if (exitTimer > 0)
        {
            exitTimer--;

            if (exitTimer == 0)
            {
                if (successMessage != null && successMessage.getWorld() != null)
                    board.removeContent(successMessage);

                cleanup();  // remove panel & contents
                MinigameLock.setLocked(false);
                
                // Start cooldown so the trigger can reopen after 1 second
                reopenCooldown = 60;

                if (listener != null)
                    listener.onCaptainMinigameSuccess();
            }
        }
    }

    /**
     * Immediate close requested by the player:
     *  - Clicked the "X" button
     *  - Or pressed ESC (handled by EscListener)
     *
     * No money changes, no success/failure callbacks.
     */
    private void closeFromPlayer()
    {
        if (finished) return; // already closed/end sequence

        finished = true;
        
        // Notify tutorial system even on manual close
        if (world instanceof SingleplayerPlaying)
            TutorialController.onCaptainMinigameClosed();

        cleanup();
        //setControlsLocked(false);
        MinigameLock.setLocked(false);

        // Start cooldown so player can reopen after 1 second
        reopenCooldown = 60;
    }

    /**
     * Removes the PanelBoard and all actors on it.
     */
    private void cleanup()
    {
        if (board != null)
        {
            board.destroy();
            board = null;
        }
    }

    /**
     * Used by the boat to know if the minigame is already resolved.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Invisible helper actor that listens for the ESC key
     * while the minigame panel is present.
     */
    private class EscListener extends Actor
    {
        private boolean escConsumed = false;

        public void act()
        {
            // Only active while the controller is still alive
            if (finished) return;

            boolean escDown = Greenfoot.isKeyDown("escape");

            if (!escDown)
            {
                // key released -> re-arm
                escConsumed = false;
                return;
            }

            if (escConsumed)
                return; // still holding from a previous frame

            escConsumed = true;

            closeFromPlayer();
        }
    }
}
