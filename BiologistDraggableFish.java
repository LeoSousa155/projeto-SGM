import greenfoot.*;

public class BiologistDraggableFish extends Actor
{
    private final BiologistMinigameController controller;
    private boolean dragging = false;
    private boolean draggable = true;

    private int homeX, homeY;
    private boolean homeSet = false;

    public BiologistDraggableFish(String imageFile, BiologistMinigameController controller)
    {
        this.controller = controller;

        GreenfootImage img;
        try {
            img = new GreenfootImage(imageFile);
        } catch (Exception e) {
            img = new GreenfootImage(140, 90);
            img.setColor(new Color(50,50,50));
            img.fill();
            img.setColor(Color.WHITE);
            img.drawString("Missing\n" + imageFile, 10, 20);
        }

        // make it a nice size
        if (img.getWidth() > 200)
            img.scale(200, (int)(200 * (img.getHeight() / (double)img.getWidth())));

        setImage(img);
    }

    public void setDraggable(boolean canDrag)
    {
        this.draggable = canDrag;
    }

    public void snapHome()
    {
        if (homeSet)
            setLocation(homeX, homeY);
    }

    public void addedToWorld(World w)
    {
        homeX = getX();
        homeY = getY();
        homeSet = true;
    }

    public void act()
    {
        if (!draggable) return;
        if (controller == null) return;

        MouseInfo mi = Greenfoot.getMouseInfo();
        if (mi == null) return;

        if (Greenfoot.mousePressed(this))
        {
            dragging = true;
        }

        if (dragging)
        {
            setLocation(mi.getX(), mi.getY());

            if (Greenfoot.mouseDragEnded(this) || Greenfoot.mouseClicked(null))
            {
                dragging = false;
                controller.onFishDropped(getX(), getY());
            }
        }
    }
}