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

    // Change these two values to resize the boat sprite:
    private static final int BOAT_WIDTH  = 60;   // pixels
    private static final int BOAT_HEIGHT = 40;   // pixels

    // Speed in pixels per frame
    private int speed = 4;

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
        GreenfootImage img = new GreenfootImage("couch.png");
        img.scale(BOAT_WIDTH, BOAT_HEIGHT);
        setImage(img);
    }

    public void act()
    {
        if (board == null || controller == null || controller.isFinished())
            return;

        handleMovement();
        updateMessage();

        // --- ROCK COLLISIONS (lose 100$, show message, rock becomes ghost) ---
        CaptainRock rock = (CaptainRock)getOneIntersectingObject(CaptainRock.class);
        if (rock != null && !disabledRocks.contains(rock))
        {
            handleRockHit(rock);
        }

        // --- GOAL COLLISION (still immediate success for now) ---
        // --- GOAL COLLISION ---
        if (isTouching(CaptainGoalZone.class))
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

    // ========================= ROCK HIT LOGIC =========================

    private void handleRockHit(CaptainRock rock)
    {
        // Mark as disabled so future collisions with this rock do nothing
        disabledRocks.add(rock);

        // Make the rock more transparent (visual feedback)
        GreenfootImage rockImg = new GreenfootImage(rock.getImage());
        rockImg.setTransparency(80); // mostly ghosted
        rock.setImage(rockImg);

        // Lose 100$
        MoneyDisplay.addMoney(-100);

        // Show crash message in the middle of the board
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
        messageActor = new Text(text, 28, Color.WHITE);
    
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
        }
    }
}