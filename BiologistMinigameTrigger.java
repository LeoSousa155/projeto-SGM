import greenfoot.*;
import java.util.List;

public class BiologistMinigameTrigger extends Actor
{
    private int worldX, worldY;
    private int activationDistance = 40;
    private boolean canUseKey = true;

    private GreenfootImage visibleImg;
    private GreenfootImage hiddenImg;
    private boolean isVisibleNow = false;

    public BiologistMinigameTrigger(int worldX, int worldY)
    {
        this.worldX = worldX;
        this.worldY = worldY;

        visibleImg = new GreenfootImage("BiologistTrigger.png");
        visibleImg.scale(235, 110);

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
    
        if (MinigameLock.isLocked()) return;
        if (!BiologistMinigameController.canReopen()) return;
    
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
        PanelBoard board = new PanelBoard("panelboard.png", 700, 500);
        world.addObject(board, world.getWidth() / 2, world.getHeight() / 2);
    
        BiologistMinigameController.ResultListener listener = null;
        if (world instanceof BiologistMinigameController.ResultListener)
            listener = (BiologistMinigameController.ResultListener) world;
    
        new BiologistMinigameController(world, board, listener);
    }
}