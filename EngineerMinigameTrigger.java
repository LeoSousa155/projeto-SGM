import greenfoot.*;
import java.util.List;

public class EngineerMinigameTrigger extends Actor
{
    private int worldX;
    private int worldY;

    private int activationDistance = 40;
    private boolean canUseKey = true;

    private GreenfootImage visibleImg;
    private GreenfootImage hiddenImg;
    private boolean isVisibleNow = false;
    
    private int noRepairPanelCooldown = 0;

    public EngineerMinigameTrigger(int worldX, int worldY)
    {
        this.worldX = worldX;
        this.worldY = worldY;

        visibleImg = new GreenfootImage("EngineerTrigger.png");
        visibleImg.scale(140, 81);

        hiddenImg = new GreenfootImage(1, 1);
        setImage(hiddenImg);
    }

    public void updateScreenPosition(SingleplayerPlaying world)
    {
        setLocation(world.worldToScreenX(worldX), world.worldToScreenY(worldY));
    }

    public void act()
    {
        if (noRepairPanelCooldown > 0) noRepairPanelCooldown--;
        updateVisibility();
        checkInteraction();
    }

    private void updateVisibility()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;
        
        if (TutorialController.isTutorialMode() && !TutorialController.allowEngineerTrigger())
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
        if (!TutorialController.allowEngineerTrigger()) return;
        if (MinigameLock.isLocked()) return;
        if (!EngineerMinigameController.canReopen()) return;
        if (noRepairPanelCooldown > 0) return;

        List<ControllablePlayer> players = w.getObjects(ControllablePlayer.class);
        if (players == null || players.isEmpty()) return;
        ControllablePlayer player = players.get(0);

        int dx = player.getX() - getX();
        int dy = player.getY() - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > activationDistance)
        {
            canUseKey = true;
            return;
        }

        boolean eDown = Greenfoot.isKeyDown("e");

        if (!eDown)
        {
            canUseKey = true;
            return;
        }

        if (!canUseKey) return;
        
        if (!EngineRepairState.needsRepair())
        {
            showNoRepairNeeded(w);
            return;
        }

        canUseKey = false;
        openMinigame(w);
    }

    private void showNoRepairNeeded(World world)
    {
        if (!MinigameLock.tryLock()) return;

        PanelBoard board = new PanelBoard("panelboard.png", 600, 300);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);

        Text t = new Text(
            "The engine doesn't need repairs right now.",
            26, Color.WHITE, true
        );
        board.addContent(t, 0, -30);

        Button ok = new Button("OK", 28, "button1.png", 200, 60, () -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
            noRepairPanelCooldown = 20;
        });
        board.addContent(ok, 0, 90);

        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
            noRepairPanelCooldown = 20;
        });
    }
    
    private void openMinigame(World world)
    {
        // Tutorial intercept (only once)
        if (TutorialController.shouldShowEngineerIntro())
        {
            TutorialController.showEngineerIntro(world, () -> {
                openMinigame(world); // will skip intercept after engineerIntroShown=true
            });
            return;
        }
    
        // --- normal behaviour ---
        if (!(world instanceof EngineerMinigameController.ResultListener))
        {
            System.out.println("ERROR: World must implement ResultListener for EngineerMinigameTrigger.");
            return;
        }
    
        EngineerMinigameController.ResultListener listener =
            (EngineerMinigameController.ResultListener) world;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        new EngineerMinigameController(world, board, listener);
    }
}