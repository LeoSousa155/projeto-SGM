import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class EngineerClass here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EngineerClass extends PlayerClass
{
    public EngineerClass()
    {
        GreenfootImage img = new GreenfootImage("engineer.png");
        img.scale(150, 150);   // <- resize here
        setImage(img);
    }
}
