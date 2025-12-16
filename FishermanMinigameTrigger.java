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
    
    private int bucketPanelCooldown = 0;
    private int zonePanelCooldown = 0;
    private boolean zonePanelJustShown = false;

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
        if (bucketPanelCooldown > 0) bucketPanelCooldown--;
        if (zonePanelCooldown > 0) zonePanelCooldown--;
    
        // clear latch once cooldown finishes
        if (zonePanelCooldown == 0) zonePanelJustShown = false;
    
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
        if (bucketPanelCooldown > 0) return;
    
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
    
        if (FishermanFishData.isZoneDepleted())
        {
            showZoneDepleted(w);
            return;
        }
        
        if (zonePanelJustShown) return;
        
        if (FishermanFishData.isCatchLimitReached())
        {
            showStorageFull(w);
            return;
        }
        
        openMinigame(w);
    }

    private void showZoneDepleted(World world)
    {
        if (zonePanelCooldown > 0) return;
        if (!MinigameLock.tryLock()) return;
    
        zonePanelJustShown = true;
    
        PanelBoard board = new PanelBoard("panelboard.png", 600, 300);
        world.addObject(board, world.getWidth()/2, world.getHeight()/2);
    
        Text t = new Text(
            "No fish left in this zone! (0/" + FishermanFishData.MAX_ZONE_FISH + ")\n" +
            "Play the Captain minigame to move to a new zone.",
            24, Color.WHITE, true
        );
        board.addContent(t, 0, -40);
    
        Button ok = new Button("OK", 28, "button1.png", 200, 60, () -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
    
            zonePanelCooldown = 20;
            bucketPanelCooldown = 20;
        });
        board.addContent(ok, 0, 90);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
    
            zonePanelCooldown = 20;
            bucketPanelCooldown = 20;
        });
    }
    
    private void showStorageFull(World world)
    {
        if (!MinigameLock.tryLock()) return;
    
        PanelBoard board = new PanelBoard("panelboard.png", 600, 300);
        world.addObject(board, world.getWidth()/2, world.getHeight()/2);
    
        Text t = new Text(
            "Fish bucket full! (" + FishermanFishData.getPendingCaughtCount() +
            "/" + FishermanFishData.MAX_PENDING_CAUGHT + ")\nGo to the Biologist to Keep/Release fish!",
            24, Color.WHITE, true
        );
        board.addContent(t, 0, -40);
    
        Button ok = new Button("OK", 28, "button1.png", 200, 60, () -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
            bucketPanelCooldown = 20;
        });
        board.addContent(ok, 0, 90);
    
        MinigameLock.registerForceClose(() -> {
            if (board.getWorld() != null) board.destroy();
            MinigameLock.setLocked(false);
            bucketPanelCooldown = 20;
        });
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