import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

public class EngineerWireLayer extends Actor
{
    private static class Line
    {
        int x1, y1, x2, y2;
        Color color;
        Line(int x1, int y1, int x2, int y2, Color color)
        { this.x1=x1; this.y1=y1; this.x2=x2; this.y2=y2; this.color=color; }
    }

    private final int width, height;
    private final List<Line> lines = new ArrayList<>();

    // temp (drag) line
    private boolean hasTemp = false;
    private int tx1, ty1, tx2, ty2;
    private Color tColor = Color.WHITE;

    public EngineerWireLayer(int width, int height)
    {
        this.width = width;
        this.height = height;
        setImage(new GreenfootImage(width, height));
        redraw();
    }

    /** World coords -> layer local coords helper */
    private int toLocalX(int worldX) { return worldX - (getX() - width / 2); }
    private int toLocalY(int worldY) { return worldY - (getY() - height / 2); }

    public void addPermanentLine(int worldX1, int worldY1, int worldX2, int worldY2, Color color)
    {
        lines.add(new Line(toLocalX(worldX1), toLocalY(worldY1), toLocalX(worldX2), toLocalY(worldY2), color));
        redraw();
    }
    
    private void drawThickLine(GreenfootImage img,
                               int x1, int y1, int x2, int y2,
                               Color color, int thickness)
    {
        img.setColor(color);
    
        for (int i = -thickness / 2; i <= thickness / 2; i++)
        {
            img.drawLine(x1, y1 + i, x2, y2 + i);
            img.drawLine(x1 + i, y1, x2 + i, y2);
        }
    }

    public void setTempLine(int worldX1, int worldY1, int worldX2, int worldY2, Color color)
    {
        hasTemp = true;
        tx1 = toLocalX(worldX1);
        ty1 = toLocalY(worldY1);
        tx2 = toLocalX(worldX2);
        ty2 = toLocalY(worldY2);
        tColor = color;
        redraw();
    }

    public void clearTempLine()
    {
        if (!hasTemp) return;
        hasTemp = false;
        redraw();
    }

    private void redraw()
    {
        GreenfootImage img = getImage();
        img.clear();

        // transparent background
        img.setColor(new Color(0,0,0,0));
        img.fill();

        // permanent lines
        for (Line ln : lines)
        {
            drawThickLine(img, ln.x1, ln.y1, ln.x2, ln.y2, ln.color, 5);
        }

        // temp line (draw last)
        if (hasTemp)
        {
            drawThickLine(img, tx1, ty1, tx2, ty2, tColor, 3);
        }
    }
}