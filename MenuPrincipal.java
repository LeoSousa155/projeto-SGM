import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MenuPrincipal here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MenuPrincipal extends World
{
    private static GreenfootImage bg = new GreenfootImage("menu_background.png");
    
    static {
        bg.scale(1200, 860);
    }
    
    /**
     * Constructor for objects of class MenuPrincipal.
     */
    public MenuPrincipal()
    {    
        // Create world using the image's width and height
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
        
        // Add an actor at specific coordinates
        addObject(new WoodButtonSingle("Singleplayer"), 250, 470);
        addObject(new WoodButtonSingle("Multiplayer"), 250, 550);
        addObject(new WoodButtonSingle("Options"), 250, 630);
        addObject(new WoodButtonSingle("Exit"), 250, 710);
        
        addObject(new Boat(), 850, 550);
        
        addObject(new Logo(), 300, 220);
        
        addObject(new DebugMouse(), 0, 0);

    }
}
