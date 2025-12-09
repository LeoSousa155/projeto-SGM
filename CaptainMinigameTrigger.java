import greenfoot.*;
import java.util.List;

/**
 * CaptainMinigameTrigger
 *
 * Placed on the big boat map (e.g. at the steering wheel).
 * When the player is close and presses E, it opens the Captain minigame.
 *
 * SingleplayerPlaying must implement CaptainMinigameController.ResultListener.
 */
public class CaptainMinigameTrigger extends Actor
{
    // World (map) coordinates of the trigger
    private int worldX;
    private int worldY;

    // How close the player must be to interact (in pixels, screen space)
    private int activationDistance = 40;

    // Debounce for the E key
    private boolean canUseKey = true;

    public CaptainMinigameTrigger(int worldX, int worldY)
    {
        this.worldX = worldX;
        this.worldY = worldY;

        // TEMP: using couch image as placeholder helm
        GreenfootImage img = new GreenfootImage("couch.png");
        img.scale(150, 50);
        setImage(img);
    }

    /**
     * Called from SingleplayerPlaying.updateAllSolidPositions()
     * to place this trigger on screen according to the camera.
     */
    public void updateScreenPosition(SingleplayerPlaying world)
    {
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    }

    public void act()
    {
        checkInteraction();
    }

    private void checkInteraction()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;

        // If a Captain minigame is currently running, ignore interaction
        if (CaptainMinigameController.areControlsLocked())
            return;

        // If we're still in cooldown after closing, ignore interaction
        if (!CaptainMinigameController.canReopen())
            return;

        // Find the controllable player (Jack)
        List<ControllablePlayer> players = w.getObjects(ControllablePlayer.class);
        if (players == null || players.isEmpty()) return;
        ControllablePlayer player = players.get(0);

        // Distance in screen coordinates
        int dx = player.getX() - getX();
        int dy = player.getY() - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > activationDistance)
        {
            // Out of range: reset key debounce so E can be pressed again later
            canUseKey = true;
            return;
        }

        boolean eDown = Greenfoot.isKeyDown("e");

        if (!eDown)
        {
            // Key released: re-arm
            canUseKey = true;
            return;
        }

        if (!canUseKey)
            return; // still holding key from previous frame

        canUseKey = false;
        openMinigame(w);
    }

    /**
     * Starts the Captain minigame by placing a PanelBoard and
     * creating a CaptainMinigameController.
     */
    private void openMinigame(World world)
    {
        if (!(world instanceof CaptainMinigameController.ResultListener))
        {
            System.out.println("ERROR: World must implement ResultListener for CaptainMinigameTrigger.");
            return;
        }

        CaptainMinigameController.ResultListener listener =
            (CaptainMinigameController.ResultListener) world;

        // Create the board in the center of the screen
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500); // uses your scaling
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);

        // Create controller for this session
        new CaptainMinigameController(world, board, listener);
    }
}
