import greenfoot.*;

/**
 * Invisible collision block that lives in MAP coordinates.
 * SingleplayerPlaying will position it on screen based on the camera.
 */
public class Solid extends Actor
{
    public static boolean DEBUG = false;   // ← toggle this to show/hide solids
    
    private int worldX;
    private int worldY;  // center position in map/world coordinates

    public Solid(int width, int height, int worldX, int worldY)
    {
        GreenfootImage img = new GreenfootImage(width, height);
        
        if (DEBUG)
        {
            img.setColor(new Color(255, 0, 0, 80));  // translucent red
            img.fill();
        }
        else
        {
            img.setTransparency(0); // invisible
        }

        setImage(img);

        this.worldX = worldX;
        this.worldY = worldY;
    }

    public int getWorldX()
    {
        return worldX;
    }

    public int getWorldY()
    {
        return worldY;
    }

    /** Update this solid's on-screen position based on the current camera. */
    public void updateScreenPosition(SingleplayerPlaying world)
    {
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    }
}