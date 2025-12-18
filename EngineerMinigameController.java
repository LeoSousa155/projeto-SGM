import greenfoot.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EngineerMinigameController
{
    // ===== REOPEN COOLDOWN =====
    private static int reopenCooldown = 0;
    public static boolean canReopen() { return reopenCooldown == 0; }
    public static void tickReopenTimer() { if (reopenCooldown > 0) reopenCooldown--; }

    private World world;
    private PanelBoard board;

    private boolean paused = false;
    private int pauseTimer = 0;
    private Text wrongMessage = null;
    private boolean finished = false;
    private Text successMessage;
    private int exitTimer = 0;

    private EngineerWireLayer wireLayer;
    private final List<EngineerWirePeg> leftPegs  = new ArrayList<>();
    private final List<EngineerWirePeg> rightPegs = new ArrayList<>();
    private int matchedCount = 0;

    public interface ResultListener
    {
        void onEngineerMinigameSuccess();
        void onEngineerMinigameFailure();
    }

    private ResultListener listener;

    public EngineerMinigameController(World world, PanelBoard board, ResultListener listener)
    {
        this.world = world;
        this.board = board;
        this.listener = listener;

        board.attachControllerAdapter(new PanelBoard.ControllerAdapter() {
            public void update() { EngineerMinigameController.this.update(); }
        });

        if (!MinigameLock.tryLock())
        {
            board.destroy();
            return;
        }
        
        MinigameLock.registerForceClose(() -> {
            finished = true;
            cleanup();
        });

        setupMinigame();
    }

    private void setupMinigame()
    {
        if (board == null) return;

        // Background
        EngineerGameBackground bg = new EngineerGameBackground();
        board.addContent(bg, 0, -5);

        // Wire layer
        wireLayer = new EngineerWireLayer(700, 485);
        board.addContent(wireLayer, 0, -20);

        // Base set of colors
        List<EngineerWirePeg.WireColor> colors = new ArrayList<>();
        Collections.addAll(colors,
            EngineerWirePeg.WireColor.RED,
            EngineerWirePeg.WireColor.GREEN,
            EngineerWirePeg.WireColor.YELLOW,
            EngineerWirePeg.WireColor.BLUE
        );
        
        // Shuffle LEFT independently
        List<EngineerWirePeg.WireColor> orderLeft = new ArrayList<>(colors);
        Collections.shuffle(orderLeft);
        
        // Shuffle RIGHT independently
        List<EngineerWirePeg.WireColor> orderRight = new ArrayList<>(colors);
        Collections.shuffle(orderRight);

        // positions (relative to board center)
        int leftX  = -board.getHalfWidth() + 120;
        int rightX =  board.getHalfWidth() - 120;

        int[] ys = new int[] { -120, -40, 40, 120 };

        for (int i = 0; i < 4; i++)
        {
            EngineerWirePeg lp = new EngineerWirePeg(EngineerWirePeg.Side.LEFT, orderLeft.get(i));
            board.addContent(lp, leftX, ys[i]);
            leftPegs.add(lp);
        
            EngineerWirePeg rp = new EngineerWirePeg(EngineerWirePeg.Side.RIGHT, orderRight.get(i));
            board.addContent(rp, rightX, ys[i]);
            rightPegs.add(rp);
        }

        // Input handler (mouse drag logic)
        EngineerMouseHandler handler = new EngineerMouseHandler(board, this, wireLayer);
        board.addContent(handler, 0, 0);

        // ESC + X button
        EscListener escListener = new EscListener();
        board.addContent(escListener, 0, 0);

        Button closeButton = new Button(
            "X", "button1.png", 40, 40,
            () -> closeFromPlayer()
        );

        int offsetX = board.getHalfWidth() - closeButton.getImage().getWidth() / 2 - 5;
        int offsetY = -board.getHalfHeight() + closeButton.getImage().getHeight() / 2 + 5;
        board.addContent(closeButton, offsetX, offsetY);
    }

    public boolean isPaused()
    {
        return paused;
    }
    
    private void showWrongMessage()
    {
        if (paused) return;
    
        paused = true;
    
        wrongMessage = new Text("Wrong match! (-100$)", 28, Color.RED, true);
        board.addContent(wrongMessage, 0, 0);
    
        pauseTimer = 90; // 1.5 seconds at ~60 FPS
    }
    
    private void hideWrongMessage()
    {
        if (wrongMessage != null && wrongMessage.getWorld() != null)
            board.removeContent(wrongMessage);
    
        wrongMessage = null;
        paused = false;
        pauseTimer = 0;
    }
    
    /** Called by EngineerMouseHandler when player releases on a right peg. */
    public void attemptMatch(EngineerWirePeg fromLeft, EngineerWirePeg toRight)
    {
        if (paused) return;
        if (finished) return;
        if (fromLeft == null || toRight == null) return;

        boolean correct = fromLeft.getWireColor() == toRight.getWireColor();

        if (correct)
        {
            MoneyDisplay.addMoney(+25);

            fromLeft.setMatched(true);
            toRight.setMatched(true);

            // draw permanent line between peg centers
            wireLayer.addPermanentLine(
                fromLeft.getX(), fromLeft.getY(),
                toRight.getX(), toRight.getY(),
                colorFor(fromLeft.getWireColor())
            );

            matchedCount++;
            if (matchedCount >= 4)
                notifySuccess();
        }
        else
        {
            MoneyDisplay.addMoney(-100);
            showWrongMessage();
        }
    }

    public Color colorFor(EngineerWirePeg.WireColor wc)
    {
        switch (wc)
        {
            case RED:    return Color.RED;
            case GREEN:  return Color.GREEN;
            case YELLOW: return Color.YELLOW;
            case BLUE:   return Color.BLUE;
            default:     return Color.WHITE;
        }
    }

    public boolean isFinished() { return finished; }

    public void notifySuccess()
    {
        if (finished) return;
        finished = true;

        successMessage = new Text("All wires matched!", 28, Color.GREEN, true);
        board.addContent(successMessage, 0, 0);

        exitTimer = 90;
    }

    public void update()
    {
        if (paused)
        {
            if (pauseTimer > 0)
            {
                pauseTimer--;
                if (pauseTimer == 0)
                {
                    hideWrongMessage();
                }
            }
            return; // keep minigame paused while message is visible
        }
        
        if (exitTimer > 0)
        {
            exitTimer--;
            if (exitTimer == 0)
            {
                cleanup();
                MinigameLock.setLocked(false);
                reopenCooldown = 60;

                if (listener != null)
                {
                    if (successMessage != null) listener.onEngineerMinigameSuccess();
                    else listener.onEngineerMinigameFailure();
                }
            }
        }
    }

    private void closeFromPlayer()
    {
        if (finished) return;
        finished = true;
    
        if (world instanceof SingleplayerPlaying)
            TutorialController.onEngineerMinigameClosed();
    
        cleanup();
        MinigameLock.setLocked(false);
        reopenCooldown = 60;
    }

    private void cleanup()
    {
        if (board != null)
        {
            board.destroy();
            board = null;
        }
    }

    private class EscListener extends Actor
    {
        private boolean escConsumed = false;

        public void act()
        {
            if (finished) return;

            boolean escDown = Greenfoot.isKeyDown("escape");

            if (!escDown)
            {
                escConsumed = false;
                return;
            }

            if (escConsumed) return;

            escConsumed = true;
            closeFromPlayer();
        }
    }
}