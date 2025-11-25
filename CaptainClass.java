import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CaptainClass here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CaptainClass extends PlayerClass
{
    public CaptainClass()
    {
        GreenfootImage img = new GreenfootImage("captain.png");
        img.scale(175, 175);   // <- resize here
        setImage(img);
    }
}
