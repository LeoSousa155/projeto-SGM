import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * Write a description of class PanelBoard here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PanelBoard extends Actor
{
    private List<Actor> contents = new ArrayList<Actor>();
    private GreenfootImage baseImage;
    
    public interface ControllerAdapter { void update(); }

    private ControllerAdapter controller;
    
    public PanelBoard(String imageFile) 
    {
        GreenfootImage img = new GreenfootImage(imageFile);
        img.scale(450, 350);   // <- resize here
        setImage(img);
    }
    
    public PanelBoard(String imageFile, int width, int height)
    {
        baseImage = new GreenfootImage(imageFile);

        if (width > 0 && height > 0)
        {
            baseImage.scale(width, height);
        }

        setImage(baseImage);
    }
    
    public void act()
    {
        if (controller != null)
            controller.update();
    }
    
    public void attachControllerAdapter(ControllerAdapter c)
    {
        controller = c;
    }
    
    public void addContent(Actor a)
    {
        contents.add(a);
    }
    
    public void addContent(Actor a, int offsetX, int offsetY)
    {
        World w = getWorld();
        if (w == null || a == null) return;

        int x = getX() + offsetX;
        int y = getY() + offsetY;

        w.addObject(a, x, y);
        contents.add(a);
    }
    
    /**
     * Removes a specific child from this panel (if it was previously added).
     */
    public void removeContent(Actor a)
    {
        if (a == null) return;
        World w = getWorld();
        if (w == null) return;

        if (contents.remove(a) && a.getWorld() != null)
        {
            w.removeObject(a);
        }
    }

    /**
     * Returns the half-width of the panel image (useful for clamping movement).
     */
    public int getHalfWidth()
    {
        return getImage().getWidth() / 2;
    }

    /**
     * Returns the half-height of the panel image.
     */
    public int getHalfHeight()
    {
        return getImage().getHeight() / 2;
    }

    /**
     * True if the given world coordinates are inside this panel’s image.
     */
    public boolean containsWorldPoint(int worldX, int worldY)
    {
        int halfW = getHalfWidth();
        int halfH = getHalfHeight();

        int left   = getX() - halfW;
        int right  = getX() + halfW;
        int top    = getY() - halfH;
        int bottom = getY() + halfH;

        return worldX >= left && worldX <= right &&
               worldY >= top  && worldY <= bottom;
    }

    /**
     * Removes all children that were added to this panel, but keeps the panel itself.
     */
    public void clearContents()
    {
        World w = getWorld();
        if (w == null) return;

        for (Actor a : contents)
        {
            if (a != null && a.getWorld() != null)
                w.removeObject(a);
        }
        contents.clear();
    }

    /**
     * Destroys the panel and all of its children.
     * Call this when the minigame finishes.
     */
    public void destroy()
    {
        World w = getWorld();
        if (w == null) return;

        // Remove all children
        for (Actor a : contents)
        {
            if (a.getWorld() != null)
            {
                w.removeObject(a);
            }
        }
        contents.clear();

        // Remove the panel itself
        w.removeObject(this);
    }
}
