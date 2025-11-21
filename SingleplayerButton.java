import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SingleplayerButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SingleplayerButton extends Button
{
    public SingleplayerButton() {
        super("Singleplayer");
    }
    
    @Override
    protected void onClick() {
        Greenfoot.setWorld(new SingleplayerLobby());
    }
}
