import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Button here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Button extends Actor
{
    private String label;
    
    public Button(String label) {
        this.label = label;
        updateImage();
    }

    // Draw the wood button + text
    private void updateImage() {
        GreenfootImage base = new GreenfootImage("woodbutton.png");

        GreenfootImage text = new GreenfootImage(label, 28, Color.WHITE, new Color(0,0,0,0));
        int x = (base.getWidth() - text.getWidth()) / 2;
        int y = (base.getHeight() - text.getHeight()) / 2;

        base.drawImage(text, x, y);
        setImage(base);
    }
    
    /**
     * Act - do whatever the Button wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            onClick();
        }
    }
    
    /** Each subclass decides what happens when clicked */
    protected abstract void onClick();
}
