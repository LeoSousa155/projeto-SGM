import greenfoot.*;

public class EngineerWirePeg extends Actor
{
    public enum Side { LEFT, RIGHT }
    public enum WireColor { RED, GREEN, YELLOW, BLUE }

    private final Side side;
    private final WireColor wireColor;
    private boolean matched = false;

    public EngineerWirePeg(Side side, WireColor wireColor)
    {
        this.side = side;
        this.wireColor = wireColor;
        redraw(false);
    }

    public Side getSide() { return side; }
    public WireColor getWireColor() { return wireColor; }

    public boolean isMatched() { return matched; }

    public void setMatched(boolean value)
    {
        matched = value;
        redraw(matched);
    }

    private void redraw(boolean dim)
    {
        // Overall peg size
        int w = 86, h = 34;
        GreenfootImage img = new GreenfootImage(w, h);
    
        Color c = toColor(wireColor);
        if (dim) c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 120);
    
        // --- Mounting plate ---
        img.setColor(Color.DARK_GRAY);
        img.fillRect(0, 0, w, h);
    
        // --- “Screw” circles on the plate (optional detail) ---
        img.setColor(new Color(120, 120, 120));
        img.fillOval(6, h/2 - 5, 10, 10);
        img.fillOval(w - 16, h/2 - 5, 10, 10);
    
        // --- Colored insulation body ---
        // a thick rounded-ish look using ovals + rect
        int insX = 18, insY = 7, insW = 34, insH = 20;
    
        img.setColor(c);
        img.fillRect(insX + 6, insY, insW - 12, insH);
        img.fillOval(insX, insY, 12, insH);
        img.fillOval(insX + insW - 12, insY, 12, insH);
    
        // --- Metal plug ---
        img.setColor(Color.LIGHT_GRAY);
        int plugX = insX + insW;
        int plugY = h/2 - 5;
        int plugW = 26;
        int plugH = 10;
    
        img.fillRect(plugX, plugY, plugW, plugH);
    
        // --- Plug tip ---
        img.setColor(new Color(200, 200, 200));
        int tipX = plugX + plugW;
        int tipMidY = h/2;
    
        int[] xs = { tipX, tipX + 8, tipX };
        int[] ys = { plugY, tipMidY, plugY + plugH };
        img.fillPolygon(xs, ys, 3);
    
        // --- Small highlight on the metal plug ---
        img.setColor(new Color(230, 230, 230));
        img.fillRect(plugX + 1, plugY + 1, plugW - 2, 2);
        
        // Mirror for right-side pegs so they face inward
        if (side == Side.RIGHT)
        {
            img.mirrorHorizontally();
        }
    
        setImage(img);
    }

    private Color toColor(WireColor wc)
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
}