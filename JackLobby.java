import greenfoot.*;

/**
 * Jack used as a static image in the SingleplayerLobby.
 * No movement, no controls.
 */
public class JackLobby extends PlayerClass
{
    public JackLobby()
    {
        GreenfootImage img = new GreenfootImage("jack.png");
        img.scale(150, 150);
        setImage(img);
    }

    // No act() -> stays purely visual
}