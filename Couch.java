import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Couch here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Couch extends Actor
{
    public Couch()
    {
        GreenfootImage img = new GreenfootImage("couch.png");
        img.scale(950, 500);   // <- resize here
        setImage(img);
    }
}
