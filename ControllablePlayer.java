import greenfoot.*;
import java.util.List;

/**
 * Player that can move around the big map and controls the camera.
 * Only use this (or subclasses) in SingleplayerPlaying.
 */
public class ControllablePlayer extends PlayerClass
{
    public final int SPEED = 3;

    // Max height in pixels the player can "step" up/down automatically
    private static final int STEP_HEIGHT = 10;
    private static final int SLOPE_SNAP_HEIGHT = 20;

    // Debounce so triggers only fire once per key press
    private boolean canUseTrigger = true;

    // Player position in MAP coordinates (not screen coords)
    protected int worldX;
    protected int worldY;
    private boolean hasWorldPos = false;

    // Info for subclasses (like JackClass) to animate with
    protected boolean isMoving = false; // true if moving this act()
    protected int facing = 1;           // 1 = right, -1 = left

    /** Explicitly set map coordinates for this player. */
    public void setWorldLocation(int wx, int wy)
    {
        worldX = wx;
        worldY = wy;
        hasWorldPos = true;

        SingleplayerPlaying w = (SingleplayerPlaying)getWorld();
        int screenX = w.worldToScreenX(worldX);
        int screenY = w.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    }

    /** When added to the world, initialise world coords if not already. */
    public void addedToWorld(World w)
    {
        if (!hasWorldPos && w instanceof SingleplayerPlaying)
        {
            SingleplayerPlaying sp = (SingleplayerPlaying)w;
            worldX = sp.screenToWorldX(getX());
            worldY = sp.screenToWorldY(getY());
            hasWorldPos = true;
        }
    }

    public void act()
    {
        // ===== BLOCK MOVEMENT DURING MINIGAMES =====
        if (MinigameLock.isLocked())
            return;

        // 1) Try stair triggers first (W/S)
        if (handleStairTriggers())
        {
            // We teleported this frame; skip normal movement
            afterMove();
            return;
        }

        // 2) Normal movement with A/D + slopes
        moveWithCamera();
        afterMove();  // hook for subclasses (e.g. animation)
    }

    /**
     * Subclasses can override this to do things after movement,
     * like updating an animation.
     */
    protected void afterMove() { }

    // ======================= STAIR TRIGGER LOGIC (W / S) =======================

    /**
     * If W is pressed while touching an "up" StairTrigger, snap to that trigger's targetY.
     * If S is pressed while touching a "down" StairTrigger, snap to its targetY.
     * Returns true if a teleport happened, false otherwise.
     */
    private boolean handleStairTriggers()
    {
        if (!(getWorld() instanceof SingleplayerPlaying)) return false;
        if (!hasWorldPos) return false;
    
        boolean wPressed = Greenfoot.isKeyDown("w");
        boolean sPressed = Greenfoot.isKeyDown("s");
    
        // When neither key is pressed, re-arm the trigger usage
        if (!wPressed && !sPressed)
        {
            canUseTrigger = true;
            return false;
        }
    
        // Already used a trigger on this continuous key press
        if (!canUseTrigger) return false;
    
        // Check if we're touching any StairTrigger
        StairTrigger tr = (StairTrigger)getOneIntersectingObject(StairTrigger.class);
        if (tr == null) return false;
    
        boolean doTeleport = false;
    
        // Up trigger + W
        if (tr.getType() == StairTrigger.TYPE_UP && wPressed)
            doTeleport = true;
    
        // Down trigger + S
        else if (tr.getType() == StairTrigger.TYPE_DOWN && sPressed)
            doTeleport = true;
    
        if (!doTeleport) return false;
    
        // Check collisions at target before teleporting
        if (collidesAt(tr.getTargetX(), tr.getTargetY()))
        {
            canUseTrigger = false;
            return false;
        }
    
        // ===== Perform teleport (X AND Y) =====
        worldX = tr.getTargetX();
        worldY = tr.getTargetY();
    
        SingleplayerPlaying world = (SingleplayerPlaying)getWorld();
    
        // Clamp and sync
        worldX = Math.max(0, Math.min(worldX, world.getMapWidth()  - 1));
        worldY = Math.max(0, Math.min(worldY, world.getMapHeight() - 1));
    
        world.centerOn(worldX, worldY);
    
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    
        canUseTrigger = false;
        return true;
    }


    // ======================= EXISTING MOVEMENT / COLLISION =======================

    private void moveWithCamera()
    {
        int dx = 0;

        // LEFT / RIGHT only (no manual up/down here)
        if (Greenfoot.isKeyDown("a")) dx -= SPEED;
        if (Greenfoot.isKeyDown("d")) dx += SPEED;

        isMoving = (dx != 0);

        // Update facing on horizontal movement
        if (dx < 0) facing = -1;
        else if (dx > 0) facing = 1;

        if (!isMoving) return;

        SingleplayerPlaying world = (SingleplayerPlaying)getWorld();

        int newWorldX = worldX;
        int newWorldY = worldY;

        // --- 1) Try horizontal move ---
        int tryX = worldX + dx;
        int tryY = worldY;

        if (!collidesAt(tryX, tryY))
        {
            // No collision: move freely
            newWorldX = tryX;
            newWorldY = tryY;
        }
        else
        {
            // --- 2) Try stepping UP a small amount (stairs / slope edges) ---
            boolean steppedUp = false;
            for (int offset = 1; offset <= STEP_HEIGHT; offset++)
            {
                int stepY = worldY - offset;
                if (!collidesAt(tryX, stepY))
                {
                    newWorldX = tryX;
                    newWorldY = stepY;
                    steppedUp = true;
                    break;
                }
            }

            // If stepping up failed, stay in place
            if (!steppedUp)
            {
                newWorldX = worldX;
                newWorldY = worldY;
            }
        }

        // --- 3) Try stepping DOWN a little to follow flat steps/edges ---
        if (!collidesAt(newWorldX, newWorldY + 1))
        {
            for (int offset = 1; offset <= STEP_HEIGHT; offset++)
            {
                int dropY = newWorldY + offset;
                if (collidesAt(newWorldX, dropY))
                {
                    newWorldY = dropY - 1; // stand just above collision
                    break;
                }
            }
        }

        // --- 4) Apply slope surfaces (mathematical ramps) ---
        newWorldY = applySlopes(newWorldX, newWorldY);

        worldX = newWorldX;
        worldY = newWorldY;

        // Safety clamp to map bounds
        worldX = Math.max(0, Math.min(worldX, world.getMapWidth()  - 1));
        worldY = Math.max(0, Math.min(worldY, world.getMapHeight() - 1));

        // Move camera to follow
        world.centerOn(worldX, worldY);

        // Update on-screen position based on camera
        int screenX = world.worldToScreenX(worldX);
        int screenY = world.worldToScreenY(worldY);
        setLocation(screenX, screenY);
    }

    /**
     * Returns a Y adjusted to sit on a SlopeArea surface if close enough,
     * otherwise returns the original Y.
     */
    private int applySlopes(int targetX, int targetY)
    {
        SingleplayerPlaying world = (SingleplayerPlaying)getWorld();
        List<SlopeArea> slopes = world.getObjects(SlopeArea.class);

        int adjustedY = targetY;

        for (SlopeArea slope : slopes)
        {
            int minX = slope.getMinX();
            int maxX = slope.getMaxX();

            if (targetX < minX || targetX > maxX)
                continue;

            int surfaceY = slope.getYAtX(targetX);
            int diff = targetY - surfaceY; // positive if below surface

            // If we're within snapping distance of the slope line, snap onto it
            if (diff > -SLOPE_SNAP_HEIGHT && diff < SLOPE_SNAP_HEIGHT)
            {
                adjustedY = surfaceY;
                break; // one slope is enough
            }
        }

        return adjustedY;
    }

    /** Checks if moving to (targetX, targetY) collides with a Solid object. */
    private boolean collidesAt(int targetX, int targetY)
    {
        SingleplayerPlaying world = (SingleplayerPlaying)getWorld();

        // Convert target world coords to screen coords using CURRENT camera
        int screenX = world.worldToScreenX(targetX);
        int screenY = world.worldToScreenY(targetY);

        // Temporarily move for collision check
        int oldX = getX();
        int oldY = getY();

        setLocation(screenX, screenY);
        Actor hit = getOneIntersectingObject(Solid.class);
        setLocation(oldX, oldY);

        return hit != null;
    }
}
