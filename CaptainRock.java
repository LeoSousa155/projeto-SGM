import greenfoot.*;

/**
 * Static obstacle in the Captain minigame.
 * If the CaptainBoat touches a rock, the minigame is failed.
 */
public class CaptainRock extends Actor
{   
    private CollisionBox collisionBox;

    public CaptainRock()
    {
        // TODO: replace with actual rock sprite
        GreenfootImage img = new GreenfootImage("rockstopdown.png");
        img.scale(50, 50);
        setImage(img);
    }
    
    public void attachCollisionBox(PanelBoard board)
    {
        collisionBox = new CollisionBox(this, 30, 30, 0, 0);
        board.addContent(collisionBox, 0, 0);
    }

    public void act()
    {
        // Rock is static; no behavior needed
    }
}
