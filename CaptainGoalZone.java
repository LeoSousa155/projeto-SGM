import greenfoot.*;

/**
 * Goal area for the Captain minigame.
 * When the CaptainBoat touches this zone, the minigame succeeds.
 *
 * This actor should typically be semi-transparent.
 */
public class CaptainGoalZone extends Actor
{
    public CaptainGoalZone()
    {
        GreenfootImage img = new GreenfootImage("goalfish.png");
        img.scale(70, 40);

        img.setTransparency(180);

        setImage(img);
    }

    public void act()
    {
        // Passive trigger zone — no behavior needed
    }
}
