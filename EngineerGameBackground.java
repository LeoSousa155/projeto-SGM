import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class EngineerGameBackground here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EngineerGameBackground extends Actor
{
    public EngineerGameBackground()
    {
        GreenfootImage img = new GreenfootImage("enggame_bg.png");
        img.scale(675, 467);
        setImage(img);
    }

    public void act() {
        // Background is static; no behavior needed
    }
}