import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Class here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PlayerClass extends Actor
{
    public final int SPEED = 3;
    public PlayerClass()
    {
        GreenfootImage img = new GreenfootImage("captain.png");
        //img.scale(500, 500);   // <- resize here
        setImage(img);
    }
    
        private void moveControl()
    {
        int x = getX();
        int y = getY();

        if (Greenfoot.isKeyDown("w")) {
            y -= SPEED;
        }
        if (Greenfoot.isKeyDown("s")) {
            y += SPEED;
        }
        if (Greenfoot.isKeyDown("a")) {
            x -= SPEED;
        }
        if (Greenfoot.isKeyDown("d")) {
            x += SPEED;
        }

        setLocation(x, y);
    }
}
