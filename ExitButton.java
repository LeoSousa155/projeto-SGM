import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ExitButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ExitButton extends Button
{
    public ExitButton() {
        super("Exit");
    }
    
    @Override
    protected void onClick() {
        Greenfoot.stop();
    }
}
