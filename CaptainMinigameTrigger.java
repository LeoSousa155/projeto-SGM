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
    
    private int repairPanelCooldown = 0;

    // How close the player must be to interact (in pixels, screen space)
    private int activationDistance = 40;

    // Debounce for the E key
    private boolean canUseKey = true;
    
    private GreenfootImage visibleImg;
    private GreenfootImage hiddenImg;
    private boolean isVisibleNow = false; // track state to avoid re-setting every frame

    public CaptainMinigameTrigger(int worldX, int worldY)
    {
        this.worldX = worldX;
        this.worldY = worldY;

        visibleImg = new GreenfootImage("CaptainTrigger.png");
        visibleImg.scale(90, 106);

        hiddenImg = new GreenfootImage(1, 1); // transparent by default

        setImage(hiddenImg); // start hidden
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
        if (repairPanelCooldown > 0) repairPanelCooldown--;
        updateVisibility();
        checkInteraction();
    }

    private void updateVisibility()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;

        if (TutorialController.isTutorialMode() && !TutorialController.allowCaptainTrigger())
        {
            if (isVisibleNow)
            {
                setImage(hiddenImg);
                isVisibleNow = false;
            }
            return;
        }

        List<ControllablePlayer> players = w.getObjects(ControllablePlayer.class);
        if (players == null || players.isEmpty()) return;
        ControllablePlayer player = players.get(0);

        int dx = player.getX() - getX();
        int dy = player.getY() - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        boolean shouldBeVisible = distance <= activationDistance;

        if (shouldBeVisible != isVisibleNow)
        {
            setImage(shouldBeVisible ? visibleImg : hiddenImg);
            isVisibleNow = shouldBeVisible;
        }
    }
    
    private void checkInteraction()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;
        
        if (!TutorialController.allowCaptainTrigger()) return;

        // If a Captain minigame is currently running, ignore interaction
        if (MinigameLock.isLocked())
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
        
        if (EngineRepairState.needsRepair() && repairPanelCooldown == 0)
        {
            showRepairEnginePanel(w);
            return;
        }

        canUseKey = false;
        openMinigame(w);
    }

    private void showRepairEnginePanel(World world)
    {
        if (!MinigameLock.tryLock()) return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 600, 300);
        world.addObject(board, world.getWidth()/2, world.getHeight()/2);
    
        Text t = new Text(
            "You can't sail right now!\n" +
            "The engine needs repairs.\n" +
            "Go play the Engineer minigame first.",
            24, Color.WHITE, true
        );
        board.addContent(t, 0, -40);
    
        Button ok = new Button("OK", 28, "button1.png", 200, 60, () -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
            repairPanelCooldown = 20; // prevent instant retrigger
        });
        board.addContent(ok, 0, 90);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
            repairPanelCooldown = 20;
        });
    }
    
    private void openMinigame(World world)
    {
        // Tutorial intercept (only once)
        if (TutorialController.shouldShowCaptainIntro())
        {
            TutorialController.showCaptainIntro(world, () -> {
                // After pressing Continue, start the real minigame
                openMinigame(world); // will skip intercept now because captainIntroShown = true
            });
            return;
        }
    
        // --- NORMAL behaviour below ---
        if (!(world instanceof CaptainMinigameController.ResultListener))
        {
            System.out.println("ERROR: World must implement ResultListener for CaptainMinigameTrigger.");
            return;
        }
    
        CaptainMinigameController.ResultListener listener =
            (CaptainMinigameController.ResultListener) world;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        new CaptainMinigameController(world, board, listener);
    }
}
