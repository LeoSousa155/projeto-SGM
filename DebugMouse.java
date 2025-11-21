import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class d here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DebugMouse extends Actor
{
    public void act()
    {
        MouseInfo mi = Greenfoot.getMouseInfo();
        if (mi != null && Greenfoot.mouseClicked(null)) {
            System.out.println("X = " + mi.getX() + ", Y = " + mi.getY());
        }
    }
}