import greenfoot.*;
import java.util.List;

public class EngineerMouseHandler extends Actor
{
    private final PanelBoard board;
    private final EngineerMinigameController controller;
    private final EngineerWireLayer layer;

    private EngineerWirePeg draggingFrom = null;

    public EngineerMouseHandler(PanelBoard board, EngineerMinigameController controller, EngineerWireLayer layer)
    {
        this.board = board;
        this.controller = controller;
        this.layer = layer;

        // invisible
        setImage(new GreenfootImage(1, 1));
    }

    public void act()
    {
        if (controller.isFinished()) return;
        
        if (controller.isPaused())
        {
            // clear any drag line if we were mid-drag
            layer.clearTempLine();
            return;
        }
        
        MouseInfo mi = Greenfoot.getMouseInfo();
        if (mi == null) return;

        int mx = mi.getX();
        int my = mi.getY();

        // only interact inside the panel
        if (!board.containsWorldPoint(mx, my))
        {
            if (draggingFrom != null)
            {
                draggingFrom = null;
                layer.clearTempLine();
            }
            return;
        }

        // start drag on mouse press over LEFT peg
        if (Greenfoot.mousePressed(null))
        {
            EngineerWirePeg peg = getPegAt(mx, my);
            if (peg != null && peg.getSide() == EngineerWirePeg.Side.LEFT && !peg.isMatched())
            {
                draggingFrom = peg;
            }
        }

        // while dragging (mouse held)
        if (draggingFrom != null && Greenfoot.mouseDragged(null))
        {
            layer.setTempLine(
                draggingFrom.getX(), draggingFrom.getY(),
                mx, my,
                controller.colorFor(draggingFrom.getWireColor())
            );
        }

        // release -> attempt match if released over RIGHT peg
        if (draggingFrom != null && Greenfoot.mouseDragEnded(null))
        {
            EngineerWirePeg target = getPegAt(mx, my);

            layer.clearTempLine();

            if (target != null && target.getSide() == EngineerWirePeg.Side.RIGHT && !target.isMatched())
            {
                controller.attemptMatch(draggingFrom, target);
            }

            draggingFrom = null;
        }
    }

    private EngineerWirePeg getPegAt(int x, int y)
    {
        World w = getWorld();
        if (w == null) return null;

        List<EngineerWirePeg> hits = w.getObjectsAt(x, y, EngineerWirePeg.class);
        if (hits == null || hits.isEmpty()) return null;

        // top-most is fine
        return hits.get(0);
    }
}