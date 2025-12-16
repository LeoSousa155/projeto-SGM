import greenfoot.*;

public class FishCaughtDisplay extends Actor
{
    private static final int FONT_SIZE = 22;
    private String lastText = "";

    public FishCaughtDisplay()
    {
        redraw();
    }

    public void act()
    {
        redraw();
    }

    private void redraw()
    {
        String text =
            "Fishes Caught " +
            FishermanFishData.getPendingCaughtCount() +
            "/" + FishermanFishData.MAX_PENDING_CAUGHT;

        if (text.equals(lastText)) return;
        lastText = text;

        setImage(new GreenfootImage(text, FONT_SIZE, Color.WHITE, new Color(0,0,0,128)));
    }
}