import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class JackClass here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class JackClass extends PlayerClass
{
    public JackClass()
    {
        GreenfootImage img = new GreenfootImage("jack.png");
        img.scale(150, 150);   // <- resize here
        setImage(img);
    }
}
