import greenfoot.*;
import java.util.Random;

public class FishermanSkillCheckWheel extends Actor
{
    private final int size;
    private final int cx, cy;

    private double needleAngleDeg;
    private double needleSpeedDegPerFrame;

    private double targetAngleDeg;
    private double windowHalfWidthDeg;

    private final Random rng = new Random();

    private int outerR;
    private int innerR;

    private GreenfootImage hookImg;

    private boolean paused = false;

    public FishermanSkillCheckWheel(int sizePx, double startNeedleDeg, double speedDegPerFrame, double windowHalfWidthDeg)
    {
        this.size = sizePx;
        this.cx = size / 2;
        this.cy = size / 2;

        this.needleAngleDeg = startNeedleDeg;
        this.needleSpeedDegPerFrame = speedDegPerFrame;
        this.windowHalfWidthDeg = windowHalfWidthDeg;

        this.outerR = (int)(size * 0.44);
        this.innerR = (int)(size * 0.30);

        try {
            hookImg = new GreenfootImage("hook.png");
            hookImg.scale(26, 26);
        } catch (Exception e) {
            hookImg = null;
        }

        randomizeTargetAny();
        setImage(new GreenfootImage(size, size));
        redraw();
    }

    public void act()
    {
        if (!paused)
        {
            needleAngleDeg = normalizeDeg(needleAngleDeg + needleSpeedDegPerFrame);
        }
        redraw();
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    public double getTargetAngleDeg()
    {
        return targetAngleDeg;
    }

    public void randomizeTargetAny()
    {
        targetAngleDeg = rng.nextInt(360);
    }

    /**
     * Picks the next target angle so it is NOT too close to the previous target.
     * This matches your "blue arc" concept: pick a delta in [minSep,maxSep] and
     * apply it clockwise or counterclockwise from the previous target.
     */
    public void randomizeNextTarget(double minSeparationDeg, double maxSeparationDeg)
    {
        minSeparationDeg = clamp(minSeparationDeg, 0.0, 180.0);
        maxSeparationDeg = clamp(maxSeparationDeg, minSeparationDeg, 180.0);

        double prev = targetAngleDeg;

        double delta = minSeparationDeg + rng.nextDouble() * (maxSeparationDeg - minSeparationDeg);
        int sign = rng.nextBoolean() ? 1 : -1;

        targetAngleDeg = normalizeDeg(prev + sign * delta);
    }

    public boolean isNeedleInWindow()
    {
        double d = smallestAngleDiffDeg(needleAngleDeg, targetAngleDeg);
        return Math.abs(d) <= windowHalfWidthDeg;
    }

    private void redraw()
    {
        GreenfootImage img = getImage();
        img.clear();

        img.setColor(new Color(0, 0, 0, 0));
        img.fill();

        img.setColor(new Color(160, 160, 160));
        drawRing(img, cx, cy, outerR, innerR);

        double start = targetAngleDeg - windowHalfWidthDeg;
        double end   = targetAngleDeg + windowHalfWidthDeg;

        img.setColor(new Color(0, 220, 0));
        fillRingSector(img, cx, cy, outerR, innerR, start, end, 2.5);

        int markerR = (outerR + innerR) / 2;
        int hx = cx + (int)Math.round(Math.cos(Math.toRadians(targetAngleDeg)) * markerR);
        int hy = cy - (int)Math.round(Math.sin(Math.toRadians(targetAngleDeg)) * markerR);

        if (hookImg != null)
            img.drawImage(hookImg, hx - hookImg.getWidth()/2, hy - hookImg.getHeight()/2);
        else
        {
            img.setColor(Color.WHITE);
            img.fillOval(hx - 5, hy - 5, 10, 10);
        }

        img.setColor(Color.RED);
        int needleLen = outerR + 8;
        int nx = cx + (int)Math.round(Math.cos(Math.toRadians(needleAngleDeg)) * needleLen);
        int ny = cy - (int)Math.round(Math.sin(Math.toRadians(needleAngleDeg)) * needleLen);
        img.drawLine(cx, cy, nx, ny);
        img.drawLine(cx + 1, cy, nx + 1, ny);
        img.drawLine(cx, cy + 1, nx, ny + 1);

        img.fillOval(cx - 4, cy - 4, 8, 8);
    }

    private void drawRing(GreenfootImage img, int x, int y, int rOuter, int rInner)
    {
        img.fillOval(x - rOuter, y - rOuter, rOuter * 2, rOuter * 2);

        Color prev = img.getColor();
        img.setColor(new Color(0, 0, 0, 0));
        img.fillOval(x - rInner, y - rInner, rInner * 2, rInner * 2);
        img.setColor(prev);
    }

    // Wrap-safe annular sector fill
    private void fillRingSector(GreenfootImage img, int x, int y, int rOuter, int rInner,
                                double startDeg, double endDeg, double stepDeg)
    {
        startDeg = normalizeDeg(startDeg);

        endDeg = endDeg % 360.0;
        if (endDeg < 0) endDeg += 360.0;
        if (endDeg == 0.0 && startDeg > 0.0) endDeg = 360.0;

        if (endDeg < startDeg)
        {
            fillRingSector(img, x, y, rOuter, rInner, startDeg, 360.0, stepDeg);
            fillRingSector(img, x, y, rOuter, rInner, 0.0, endDeg, stepDeg);
            return;
        }

        int steps = Math.max(2, (int)Math.ceil((endDeg - startDeg) / stepDeg));

        int[] xs = new int[(steps + 1) * 2];
        int[] ys = new int[(steps + 1) * 2];

        for (int i = 0; i <= steps; i++)
        {
            double a = startDeg + (endDeg - startDeg) * (i / (double)steps);
            double rad = Math.toRadians(a);
            xs[i] = x + (int)Math.round(Math.cos(rad) * rOuter);
            ys[i] = y - (int)Math.round(Math.sin(rad) * rOuter);
        }

        for (int i = 0; i <= steps; i++)
        {
            double a = endDeg - (endDeg - startDeg) * (i / (double)steps);
            double rad = Math.toRadians(a);
            xs[(steps + 1) + i] = x + (int)Math.round(Math.cos(rad) * rInner);
            ys[(steps + 1) + i] = y - (int)Math.round(Math.sin(rad) * rInner);
        }

        img.fillPolygon(xs, ys, xs.length);
    }

    public double getNeedleAngleDeg()
    {
        return needleAngleDeg;
    }
    
    public void setTargetAngleDeg(double angleDeg)
    {
        targetAngleDeg = normalizeDeg(angleDeg);
    }
    
    private double normalizeDeg(double a)
    {
        a %= 360.0;
        if (a < 0) a += 360.0;
        return a;
    }

    private double smallestAngleDiffDeg(double a, double b)
    {
        double d = normalizeDeg(a) - normalizeDeg(b);
        if (d > 180) d -= 360;
        if (d < -180) d += 360;
        return d;
    }

    private double clamp(double v, double lo, double hi)
    {
        return Math.max(lo, Math.min(hi, v));
    }
}