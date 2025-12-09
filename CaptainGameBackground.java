import greenfoot.*;

/**
 * Background image for the Captain minigame.
 * Placed inside the PanelBoard BEFORE all gameplay actors.
 */
public class CaptainGameBackground extends Actor
{
    public CaptainGameBackground()
    {
        GreenfootImage img = new GreenfootImage("capgame_bg.png");
        img.scale(650, 450);
        setImage(img);
    }

    public void act() {
        // Background is static; no behavior needed
    }
}