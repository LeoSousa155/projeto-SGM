import greenfoot.*;

/**
 * Write a description of class MainMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainMenu extends World
{
    private static GreenfootImage bg = new GreenfootImage("menu_background.png");
    
    static {
        bg.scale(1200, 860);
    }
    
    /**
     * Constructor for objects of class MenuPrincipal.
     */
    public MainMenu()
    {    
        // Create world using the image's width and height
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
        
        addObject(new Logo(), 300, 220);
        addObject(new Boat(), 850, 550);
        addObject(new DebugMouse(), 0, 0);
        
        // Add a Button at specific coordinates
        addObject(new SingleplayerButton(), 250, 470);
        addObject(new MultiplayerButton(), 250, 550);
        addObject(new OptionsButton(), 250, 630);
        addObject(new ExitButton(), 250, 710);
    }
}