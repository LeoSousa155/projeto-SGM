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
    
    public PanelBoard(String imageFile) 
    {
        GreenfootImage img = new GreenfootImage(imageFile);
        img.scale(450, 350);   // <- resize here
        setImage(img);
    }
    
    public void addContent(Actor a)
    {
        contents.add(a);
    }
    
    public void removePanel()
    {
        World w = getWorld();
        if (w == null) return;

        // remove all children
        for (Actor a : contents) {
            if (a.getWorld() != null) {
                w.removeObject(a);
            }
        }
        contents.clear();

        // remove the panel itself
        w.removeObject(this);
    }
}
