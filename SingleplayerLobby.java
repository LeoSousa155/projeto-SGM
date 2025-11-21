import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SingleplayerLobby here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SingleplayerLobby extends World
{
    private static GreenfootImage bg = new GreenfootImage("menu_lobby.png");
    
    static {
        bg.scale(1200, 860);
    }
    
    /**
     * Constructor for objects of class SingleplayerLobby.
     * 
     */
    public SingleplayerLobby()
    {    
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
     
        addObject(new CaptainClass(), 550, 560);
        addObject(new EngineerClass(), 705, 580);
        addObject(new BiologistClass(), 860, 580);
        addObject(new FishermanClass(), 1020, 560);
        
        addObject(new Logo(), 300, 220);
        addObject(new Couch(), 730, 615);
        addObject(new DebugMouse(), 0, 0);
        
        addObject(new StartButton(), 250, 550);
        addObject(new BackButton(), 250, 630);
        
        
    }
}
