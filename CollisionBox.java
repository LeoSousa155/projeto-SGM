import greenfoot.*;

public class CollisionBox extends Actor
{
    private Actor owner;
    private int offsetX, offsetY;

    public CollisionBox(Actor owner, int width, int height, int offsetX, int offsetY)
    {
        this.owner = owner;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        GreenfootImage img = new GreenfootImage(width, height);
        img.setTransparency(0);
        setImage(img);
    }

    public void act()
    {
        if (owner == null || owner.getWorld() == null)
        {
            if (getWorld() != null) getWorld().removeObject(this);
            return;
        }
        setLocation(owner.getX() + offsetX, owner.getY() + offsetY);
    }

    public Actor getOwner() { return owner; }

    /** Public wrapper around Actor's protected collision query */
    public <T extends Actor> T getOneIntersectingPublic(Class<T> cls)
    {
        return (T) getOneIntersectingObject(cls);
    }
}