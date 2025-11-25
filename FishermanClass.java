import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class FishermanClass here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FishermanClass extends PlayerClass
{
    public FishermanClass()
    {
        GreenfootImage img = new GreenfootImage("fisherman.png");
        img.scale(200, 200);   // <- resize here
        setImage(img);
    }
}
