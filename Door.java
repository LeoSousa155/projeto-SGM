import greenfoot.*;

/**
 * Write a description of class Door here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Door extends Actor
{
    private int worldX = 698;
    private int worldY = 742;
    
    public Door()
    {
        GreenfootImage img = new GreenfootImage("middoor.png");
        img.scale(69, 167);   // <- resize here
        setImage(img);
    }
    
    public void updateScreenPosition(SingleplayerPlaying world)
    {
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    }
}
