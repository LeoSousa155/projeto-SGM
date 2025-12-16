import greenfoot.*;

public class FishLeftDisplay extends Actor
{
    private static FishLeftDisplay instance;
    private static final int FONT_SIZE = 24;

    public FishLeftDisplay()
    {
        instance = this;
        redraw();
    }

    public static void refresh()
    {
        if (instance != null) instance.redraw();
    }

    private void redraw()
    {
        int left = FishermanFishData.getZoneFishesLeft();
        setImage(new GreenfootImage(
            "Fishes Left " + left + "/" + FishermanFishData.MAX_ZONE_FISH,
            FONT_SIZE, Color.WHITE, new Color(0,0,0,128)
        ));
    }

    public void act()
    {
        redraw();
    }
}