import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MultiplayerButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MultiplayerButton extends Button
{
    public MultiplayerButton() {
        super("Multiplayer");
    }
    
    @Override
    protected void onClick() {
        Greenfoot.setWorld(new SingleplayerLobby());
    }
}
