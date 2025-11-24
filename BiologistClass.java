import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BiologistClass here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BiologistClass extends PlayerClass
{
    public BiologistClass()
    {
        GreenfootImage img = new GreenfootImage("biologist.png");
        img.scale(150, 150);   // <- resize here
        setImage(img);
    }
}
