import greenfoot.*;

/**
 * Jack with 8-frame walking animation.
 */
public class JackClass extends ControllablePlayer
{
    private GreenfootImage[] walkRight = new GreenfootImage[8];
    private GreenfootImage[] walkLeft  = new GreenfootImage[8];

    private int frameIndex = 0;
    private int animationCounter = 0;
    private int animationSpeed = 6; // lower = faster animation

    public JackClass()
    {
        // Load frames
        GreenfootImage m1 = new GreenfootImage("jack_move1.png");
        GreenfootImage m2 = new GreenfootImage("jack_move2.png");
        GreenfootImage m3 = new GreenfootImage("jack_move3.png");
        GreenfootImage m4 = new GreenfootImage("jack_move4.png");
        GreenfootImage m5 = new GreenfootImage("jack_move5.png");

        // Scale
        m1.scale(35,100);
        m2.scale(42,100);
        m3.scale(50,100);
        m4.scale(37,100);
        m5.scale(50,100);

        // Animation order (8 frames):
        // 1 → 2 → 3 → 2 → 1 → 4 → 5 → 4 → (repeat)
        walkRight[0] = m1;
        walkRight[1] = m2;
        walkRight[2] = m3;
        walkRight[3] = m2;
        walkRight[4] = m1;
        walkRight[5] = m4;
        walkRight[6] = m5;
        walkRight[7] = m4;

        // Build left-facing frames
        for (int i = 0; i < walkRight.length; i++)
        {
            walkLeft[i] = new GreenfootImage(walkRight[i]);
            walkLeft[i].mirrorHorizontally();
        }

        // Idle frame (move1)
        setImage(m1);
    }

    @Override
    protected void afterMove()
    {
        updateAnimation();
    }

    private void updateAnimation()
    {
        // Idle = move1
        if (!isMoving)
        {
            if (facing == -1)
                setImage(walkLeft[0]);
            else
                setImage(walkRight[0]);

            frameIndex = 0;
            animationCounter = 0;
            return;
        }

        // Walking animation
        animationCounter++;
        if (animationCounter >= animationSpeed)
        {
            animationCounter = 0;
            frameIndex = (frameIndex + 1) % 8;  // 8-frame loop
        }

        if (facing == -1)
            setImage(walkLeft[frameIndex]);
        else
            setImage(walkRight[frameIndex]);
    }
}
