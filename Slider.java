import greenfoot.*;

public class Slider extends Actor
{
    private int min;
    private int max;

    private double position;  // 0.0 .. 1.0
    private int trackWidth;   // length of the grey bar
    private int height;

    private int imageWidth;   // whole image (track + % box)
    private int imageHeight;

    private int trackStartX = 2;  // left padding inside the image

    private boolean dragging = false;

    public Slider(int min, int max, int initialValue, int trackWidth)
    {
        this.min = min;
        this.max = max;
        this.trackWidth = trackWidth;
        this.height = 20;

        this.imageWidth = trackWidth + 60; // extra space for “65%”
        this.imageHeight = height;

        // convert initial value to 0..1
        this.position = (double)(initialValue - min) / (max - min);

        updateImage();
    }

    public void act()
    {
        MouseInfo mi = Greenfoot.getMouseInfo();
    
        // start dragging when mouse pressed on this slider
        if (Greenfoot.mousePressed(this)) {
            dragging = true;
        }
    
        // while dragging, follow the mouse
        if (dragging && mi != null) {
            int mouseX = mi.getX();
    
            int imgLeft = getX() - imageWidth / 2;
            int trackLeft = imgLeft + trackStartX;
            int trackRight = trackLeft + trackWidth;
    
            if (mouseX < trackLeft) mouseX = trackLeft;
            if (mouseX > trackRight) mouseX = trackRight;
    
            int relative = mouseX - trackLeft;        // 0 .. trackWidth
            position = (double)relative / trackWidth; // 0.0 .. 1.0
    
            updateImage();
        }
    
        // stop dragging on drag end *or* simple click release
        if (dragging && (Greenfoot.mouseDragEnded(null) || Greenfoot.mouseClicked(null))) {
            dragging = false;
        }
    }

    private void updateImage()
    {
        int knobSize = 12;

        GreenfootImage img = new GreenfootImage(imageWidth, imageHeight);

        // background
        img.setColor(new Color(143, 206, 255));
        img.fill();

        // track
        img.setColor(Color.WHITE);
        int trackY = height / 2 - 2;
        img.fillRect(trackStartX, trackY, trackWidth, 4);

        // knob (image coordinates)
        int knobCenterX = trackStartX + (int)(position * trackWidth);
        img.setColor(Color.WHITE);
        img.fillOval(knobCenterX - knobSize / 2, height / 2 - knobSize / 2,
                     knobSize, knobSize);

        // percentage text on the right
        int value = getValue();
        img.setColor(Color.WHITE);
        img.drawString(value + "%", trackWidth + 20, height / 2 + 5);

        setImage(img);
    }

    public int getValue()
    {
        return min + (int)Math.round(position * (max - min));
    }

    public void setValue(int v)
    {
        if (v < min) v = min;
        if (v > max) v = max;

        position = (double)(v - min) / (max - min);
        updateImage();
    }
}