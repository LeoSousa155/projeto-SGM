import greenfoot.*;

/**
 * A mathematical sloped floor defined by two world-coordinate points (x1,y1) to (x2,y2).
 * The collision surface is the line between those points.
 */
public class SlopeArea extends Actor
{
    public static boolean DEBUG = false;

    private int x1, y1;   // world coords of left/right endpoints
    private int x2, y2;

    private int worldX;   // center of the visual/debug rectangle
    private int worldY;
    private int width;
    private int height;
    
    private boolean floorChangeSlope = false;

    public void setFloorChangeSlope(boolean value) {
        floorChangeSlope = value;
    }

    public boolean isFloorChangeSlope() {
        return floorChangeSlope;
    }

    public SlopeArea(int x1, int y1, int x2, int y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        width  = Math.abs(x2 - x1);
        height = Math.abs(y2 - y1) + 20; // add some thickness so it's easy to see

        // center of the debug rect
        worldX = Math.min(x1, x2) + width / 2;
        worldY = Math.min(y1, y2) + height / 2;

        GreenfootImage img = new GreenfootImage(width, height);
        if (DEBUG)
        {
            // translucent background
            img.setColor(new Color(0, 255, 0, 40));
            img.fill();

            // draw a diagonal line roughly matching the slope
            img.setColor(Color.GREEN);
            boolean leftToRightUp = (x2 > x1) == (y2 < y1);
            if (leftToRightUp)
            {
                // line from bottom-left to top-right
                img.drawLine(0, height - 1, width - 1, 0);
            }
            else
            {
                // line from top-left to bottom-right
                img.drawLine(0, 0, width - 1, height - 1);
            }
        }
        else
        {
            img.setTransparency(0);
        }
        setImage(img);
    }

    public int getMinX()
    {
        return Math.min(x1, x2);
    }

    public int getMaxX()
    {
        return Math.max(x1, x2);
    }

    /**
     * Returns the Y coordinate of the slope line at the given world X.
     * Assumes x1 != x2.
     */
    public int getYAtX(int wx)
    {
        double t = (double)(wx - x1) / (double)(x2 - x1);
        double y = y1 + t * (double)(y2 - y1);
        return (int)Math.round(y);
    }

    public int getWorldX()
    {
        return worldX;
    }

    public int getWorldY()
    {
        return worldY;
    }

    /** True if going from left to right moves upward (towards smaller y). */
    public boolean isLeftToRightUp()
    {
        return (x2 > x1) == (y2 < y1);
    }

    /**
     * Returns +1 if moving RIGHT along the slope goes up,
     * and -1 if moving LEFT along the slope goes up.
     */
    public int getHorizontalDirForUp()
    {
        return isLeftToRightUp() ? 1 : -1;
    }

    /** Update this slope's on-screen position based on the current camera. */
    public void updateScreenPosition(SingleplayerPlaying world)
    {
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    }
}
