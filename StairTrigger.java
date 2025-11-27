import greenfoot.*;

/**
 * A trigger zone for moving between floors.
 * Shows a visible icon ONLY when the player is touching it.
 * Supports image scaling AND rotation.
 */
public class StairTrigger extends Actor
{
    public static final int TYPE_UP = 1;
    public static final int TYPE_DOWN = -1;

    private int type;
    private int worldX;
    private int worldY;
    private int targetX;
    private int targetY;

    private GreenfootImage visibleImage;
    private boolean isVisible = false;

    /**
     * Full-feature StairTrigger
     *
     * @param type TYPE_UP or TYPE_DOWN
     * @param worldX world coordinate X of this trigger
     * @param worldY world coordinate Y of this trigger
     * @param targetX teleport destination X
     * @param targetY teleport destination Y
     * @param scaleW scale width (or -1 for original)
     * @param scaleH scale height (or -1 for original)
     * @param rotation rotation angle in degrees (0–359)
     */
    public StairTrigger(int type, int worldX, int worldY,
                        int targetX, int targetY,
                        int scaleW, int scaleH, int rotation)
    {
        this.type = type;
        this.worldX = worldX;
        this.worldY = worldY;
        this.targetX = targetX;
        this.targetY = targetY;

        // Load the icon (replace with your PNG)
        visibleImage = new GreenfootImage("blue-arrow.png");

        // Scale
        if (scaleW > 0 && scaleH > 0)
            visibleImage.scale(scaleW, scaleH);

        // Rotate
        if (rotation != 0)
            visibleImage.rotate(rotation);

        // Start invisible
        GreenfootImage invisible = new GreenfootImage(visibleImage.getWidth(), visibleImage.getHeight());
        invisible.setTransparency(0);
        setImage(invisible);
    }

    public int getType() { return type; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }

    /** Show image only if player is inside the trigger area. */
    public void updateVisibility()
    {
        PlayerClass player = (PlayerClass)getOneIntersectingObject(PlayerClass.class);

        if (player != null && !isVisible)
        {
            setImage(visibleImage);
            isVisible = true;
        }
        else if (player == null && isVisible)
        {
            GreenfootImage invisible = new GreenfootImage(visibleImage.getWidth(), visibleImage.getHeight());
            invisible.setTransparency(0);
            setImage(invisible);
            isVisible = false;
        }
    }

    /** Move with camera and update visibility. */
    public void updateScreenPosition(SingleplayerPlaying world)
    {
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);

        updateVisibility();
    }
}
