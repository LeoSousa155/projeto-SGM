import greenfoot.*;

/**
 * Static obstacle in the Captain minigame.
 * If the CaptainBoat touches a rock, the minigame is failed.
 */
public class CaptainRock extends Actor
{
    // Change these to resize the rock sprite
    private static final int ROCK_WIDTH  = 50;  // px
    private static final int ROCK_HEIGHT = 50;  // px

    public CaptainRock()
    {
        // TODO: replace with actual rock sprite
        GreenfootImage img = new GreenfootImage("couch.png");
        img.scale(ROCK_WIDTH, ROCK_HEIGHT);
        setImage(img);
    }

    public void act()
    {
        // Rock is static; no behavior needed
    }
}
