import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class OptionsButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OptionsButton extends Button
{
    public OptionsButton() {
        super("Options");
    }

    @Override
    protected void onClick() {
        Greenfoot.setWorld(new SingleplayerLobby());
    }
}
