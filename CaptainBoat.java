import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Boat controlled in the Captain minigame.
 * Moves inside a PanelBoard using WASD or arrow keys.
 */
public class CaptainBoat extends Actor
{
    private PanelBoard board;
    private CaptainMinigameController controller;

    // Speed in pixels per frame
    private int speed = 4;
    
    private CollisionBox collisionBox;

    // Prevent movement while "You crashed..." is showing
    private boolean movementLocked = false;
    
    // Track rocks that have already been hit (no more collision penalty)
    private List<CaptainRock> disabledRocks = new ArrayList<CaptainRock>();

    // Simple message system for "You crashed..." / "You arrived"
    private Text messageActor;
    private int messageTimer = 0;  // frames remaining

    public CaptainBoat(PanelBoard board, CaptainMinigameController controller)
    {
        this.board = board;
        this.controller = controller;

        // TODO: replace with your actual top-down boat sprite name
        GreenfootImage img = new GreenfootImage("boattopdown2.png");
        img.scale(120, 36);
        setImage(img);
    }
    
    public void act()
    {
        if (board == null || controller == null || controller.isFinished())
            return;
    
        updateMessage(); // update timer FIRST, so unlock can happen ASAP
    
        if (!movementLocked)
        {
            handleMovement();
        }
    
        // --- ROCK COLLISIONS ---
        CollisionBox box = collisionBox.getOneIntersectingPublic(CollisionBox.class);
        if (box != null && box.getOwner() instanceof CaptainRock)
        {
            CaptainRock rock = (CaptainRock) box.getOwner();
            if (!disabledRocks.contains(rock))
                handleRockHit(rock);
        }
    
        // --- GOAL COLLISION ---
        CollisionBox box2 = collisionBox.getOneIntersectingPublic(CollisionBox.class);
        if (box2 != null && box2.getOwner() instanceof CaptainGoalZone)
        {
            controller.notifyGoalReached();
        }
    }

    // ========================= MOVEMENT =========================

    private void handleMovement()
    {
        int dx = 0;
        int dy = 0;
    
        if (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("left"))  dx -= speed;
        if (Greenfoot.isKeyDown("d") || Greenfoot.isKeyDown("right")) dx += speed;
        if (Greenfoot.isKeyDown("w") || Greenfoot.isKeyDown("up"))    dy -= speed;
        if (Greenfoot.isKeyDown("s") || Greenfoot.isKeyDown("down"))  dy += speed;
    
        if (dx == 0 && dy == 0)
            return;
    
        // NEW: rotate to match movement direction (includes diagonals)
        faceDirection(dx, dy);
    
        int newX = getX() + dx;
        int newY = getY() + dy;
    
        // Clamp so the boat never leaves the inside of the PanelBoard
        if (board != null)
        {
            int halfW = board.getHalfWidth();
            int halfH = board.getHalfHeight();
    
            int left   = board.getX() - halfW  + getImage().getWidth()  / 2;
            int right  = board.getX() + halfW  - getImage().getWidth()  / 2;
            int top    = board.getY() - halfH  + getImage().getHeight() / 2;
            int bottom = board.getY() + halfH  - getImage().getHeight() / 2;
    
            if (newX < left)   newX = left;
            if (newX > right)  newX = right;
            if (newY < top)    newY = top;
            if (newY > bottom) newY = bottom;
        }
    
        setLocation(newX, newY);
    }

    private boolean boardAlive()
    {
        return board != null && board.getWorld() != null;
    }

    // ========================= ROCK HIT LOGIC =========================

    private void handleRockHit(CaptainRock rock)
    {
        disabledRocks.add(rock);
    
        GreenfootImage rockImg = new GreenfootImage(rock.getImage());
        rockImg.setTransparency(80);
        rock.setImage(rockImg);
    
        MoneyDisplay.addMoney(-100);
        
        if (!boardAlive()) return;
    
        // Lock movement for the same duration as the message
        movementLocked = true;
    
        showMessage("You crashed...");
    }

    // ========================= MESSAGE SYSTEM =========================

    private void showMessage(String text)
    {
        if (board == null) return;
    
        // Remove any previous message
        if (messageActor != null && messageActor.getWorld() != null)
        {
            board.removeContent(messageActor);
        }
    
        // Create new Text actor (white text, size 28)
        messageActor = new Text(text, 28, Color.RED, true);
    
        // Show it for ~1.5 seconds (90 frames at 60 FPS)
        messageTimer = 90;
    
        // Add in the center of the board
        board.addContent(messageActor, 0, 0);
    }

    private void updateMessage()
    {
        if (messageTimer <= 0)
            return;
    
        messageTimer--;
    
        if (messageTimer == 0 && messageActor != null)
        {
            if (messageActor.getWorld() != null && board != null)
            {
                board.removeContent(messageActor);
            }
            messageActor = null;
    
            // Message ended -> unlock movement
            movementLocked = false;
        }
    }
    
    public void setCollisionBox(CollisionBox box)
    {
        this.collisionBox = box;
    }
    
    private void faceDirection(int dx, int dy)
    {
        if (dx == 0 && dy == 0) return;
    
        // atan2 gives angle in radians. Greenfoot rotation: 0=right, 90=down.
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(dy, dx)));
    
        // Keep it in 0..359
        if (angle < 0) angle += 360;
    
        setRotation(angle);
    }
}