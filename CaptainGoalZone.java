import greenfoot.*;

/**
 * Goal area for the Captain minigame.
 * When the CaptainBoat touches this zone, the minigame succeeds.
 *
 * This actor should typically be semi-transparent.
 */
public class CaptainGoalZone extends Actor
{
    // Change these to resize the goal zone
    private static final int GOAL_WIDTH  = 120; // px
    private static final int GOAL_HEIGHT = 80;  // px

    public CaptainGoalZone()
    {
        // TODO: replace with actual goal-zone sprite
        GreenfootImage img = new GreenfootImage("couch.png");
        img.scale(GOAL_WIDTH, GOAL_HEIGHT);

        // Optional: make the zone slightly see-through
        img.setTransparency(180);

        setImage(img);
    }

    public void act()
    {
        // Passive trigger zone — no behavior needed
    }
}
