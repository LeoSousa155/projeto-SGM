import greenfoot.*;

public class TutorialArrow extends Actor
{
    private int targetWorldX;
    private int targetWorldY;

    // Where the arrow sits on the SCREEN (HUD position)
    private int hudX = 400;
    private int hudY = 230;

    public TutorialArrow(String imageFile, int targetWorldX, int targetWorldY)
    {
        this.targetWorldX = targetWorldX;
        this.targetWorldY = targetWorldY;

        GreenfootImage img = new GreenfootImage(imageFile);
        // optional sizing:
        img.scale(80, 50);
        setImage(img);
    }

    public void act()
    {
        World w = getWorld();
        if (!(w instanceof SingleplayerPlaying)) return;

        SingleplayerPlaying world = (SingleplayerPlaying) w;

        // Keep arrow fixed on screen
        setLocation(hudX, hudY);

        // Convert target WORLD -> SCREEN
        int tx = world.worldToScreenX(targetWorldX);
        int ty = world.worldToScreenY(targetWorldY);

        int dx = tx - getX();
        int dy = ty - getY();

        double angleRad = Math.atan2(dy, dx);
        int angleDeg = (int) Math.toDegrees(angleRad);

        setRotation(angleDeg);
    }

    public void setTarget(int worldX, int worldY)
    {
        targetWorldX = worldX;
        targetWorldY = worldY;
    }

    public void setHudPosition(int screenX, int screenY)
    {
        hudX = screenX;
        hudY = screenY;
    }
}
