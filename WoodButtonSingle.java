import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class WoodButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class WoodButtonSingle extends Actor
{
    private String label;

    public WoodButtonSingle(String text)
    {
        this.label = text;
        updateImage();
    }
    
    private void updateImage()
    {
        // Load your button background image
        GreenfootImage button = new GreenfootImage("woodbutton.png");

         // Create an image from the text (auto-measured!)
        GreenfootImage textImg = new GreenfootImage(label, 28, Color.WHITE, new Color(0,0,0,0));

        // Calculate center position
        int x = (button.getWidth() - textImg.getWidth()) / 2;
        int y = (button.getHeight() - textImg.getHeight()) / 2;

        // Draw text onto the button
        button.drawImage(textImg, x, y);

        setImage(button);
    }
    
    /**
     * Act - do whatever the WoodButton wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
    }
}
