import greenfoot.*;

/**
 * Base player sprite, no controls.
 * Use this (or subclasses) anywhere you just need an image.
 */
public class PlayerClass extends Actor
{
    public PlayerClass()
    {
        GreenfootImage img = new GreenfootImage("captain.png");
        setImage(img);
    }

    // No act() here – purely visual
}
