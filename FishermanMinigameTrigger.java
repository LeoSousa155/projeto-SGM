import greenfoot.*;
import java.util.List;

public class FishermanMinigameTrigger extends Actor
{
    private int worldX, worldY;
    private int activationDistance = 40;
    private boolean canUseKey = true;

    private GreenfootImage visibleImg;
    private GreenfootImage hiddenImg;
    private boolean isVisibleNow = false;

    // Fish difficulty for this trigger (hits needed)
    private int requiredHits = 2;

    public FishermanMinigameTrigger(int worldX, int worldY, int requiredHits)
    {
        this.worldX = worldX;
        this.worldY = worldY;
        this.requiredHits = Math.max(1, requiredHits);

        visibleImg = new GreenfootImage("FishermanTrigger.png");
        visibleImg.scale(137, 122);

        hiddenImg = new GreenfootImage(1, 1);
        setImage(hiddenImg);
    }

    public void updateScreenPosition(SingleplayerPlaying world)
    {
        setLocation(world.worldToScreenX(worldX), world.worldToScreenY(worldY));
    }

    public void act()
    {
        updateVisibility();
        checkInteraction();
    }

    private void updateVisibility()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;

        if (TutorialController.isTutorialMode() && !TutorialController.allowFishermanTrigger())
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
        double dist = Math.sqrt(dx*dx + dy*dy);

        boolean should = dist <= activationDistance;

        if (should != isVisibleNow)
        {
            setImage(should ? visibleImg : hiddenImg);
            isVisibleNow = should;
        }
    }

    private void checkInteraction()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;
        
        if (!TutorialController.allowFishermanTrigger()) return;

        if (MinigameLock.isLocked()) return;
        if (!FishermanMinigameController.canReopen()) return;

        List<ControllablePlayer> players = w.getObjects(ControllablePlayer.class);
        if (players == null || players.isEmpty()) return;
        ControllablePlayer player = players.get(0);

        int dx = player.getX() - getX();
        int dy = player.getY() - getY();
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist > activationDistance)
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

        canUseKey = false;
        openMinigame(w);
    }

    private void openMinigame(World world)
    {
        // Tutorial intercept (only once)
        if (TutorialController.shouldShowFishermanIntro())
        {
            TutorialController.showFishermanIntro(world, () -> {
                openMinigame(world); // will skip intercept after fishermanIntroShown=true
            });
            return;
        }
    
        if (!(world instanceof FishermanMinigameController.ResultListener))
        {
            System.out.println("ERROR: World must implement ResultListener for FishermanMinigameTrigger.");
            return;
        }
    
        FishermanMinigameController.ResultListener listener =
            (FishermanMinigameController.ResultListener) world;
    
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        new FishermanMinigameController(world, board, listener, requiredHits);
    }
}