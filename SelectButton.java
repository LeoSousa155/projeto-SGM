import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SelectButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SelectButton extends Button
{
    public SelectButton() {
        super("Select");
    }
    
    @Override
    protected void onClick() {
        Greenfoot.setWorld(new MainMenu());
    }
}
