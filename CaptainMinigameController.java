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

        // Allow board to call our update() each frame
        board.attachController(this);

        // Lock normal character controls while the minigame is active
        setControlsLocked(true);

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
        board.addContent(boat, 0, 0); // center of board

        // --- Place goal zone ---
        CaptainGoalZone goal = new CaptainGoalZone();
        board.addContent(goal, 0, -120); // adjust values as desired

        // --- Place a few rocks ---
        addRock(-100, 50);
        addRock(120, 20);
        addRock(0, 100);

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
    }

    /**
     * Old hook – rock penalty is now handled inside CaptainBoat.
     * Kept here in case you call it from elsewhere.
     */
    public void onBoatHitRock()
    {
        // Intentionally empty.
    }

    /**
     * Old immediate-success hook, kept for compatibility.
     * If you no longer call this, you can delete it.
     */
    public void onBoatReachedGoal()
    {
        notifyGoalReached();
    }

    /**
     * Called by CaptainBoat when the goal is reached.
     * Adds money, shows message, and starts delayed close.
     */
    public void notifyGoalReached()
    {
        if (finished) return;
        finished = true;

        // Add money
        MoneyDisplay.addMoney(+100);

        // Show success message
        successMessage = new Text("You arrived!", 32, Color.GREEN);
        board.addContent(successMessage, 0, 0);

        // Start ~1.5 second delay before closing (assuming ~60 fps)
        exitTimer = 90;
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
                setControlsLocked(false);

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

        cleanup();
        setControlsLocked(false);

        // Start cooldown so player can reopen after 1 second
        reopenCooldown = 60;
        // NOTE: we intentionally do NOT call listener.onCaptainMinigameSuccess/Failure()
        // because this is a manual abort.
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

            // ESC should behave exactly like clicking the X
            closeFromPlayer();
        }
    }
}
