import greenfoot.*;

/**
 * Write a description of class MainMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainMenu extends World
{
    private static GreenfootImage bg = new GreenfootImage("background.png");
    
    static {
        bg.scale(800, 600);
    }
    
    @Override
    public void started()
    {
        // We are in a world where menu music should be playing
        MusicManager.enableMenuMusic();
        MusicManager.onScenarioStarted();
    }

    @Override
    public void stopped()
    {
        // Scenario paused/stopped from this world
        MusicManager.onScenarioStopped();
    }
    
    /**
     * Constructor for objects of class MenuPrincipal.
     */
    public MainMenu()
    {    
        // Create world using the image's width and height
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
        
        //addObject(new DebugMouse(), 0, 0);
        
        Button singleplayer = new Button(
            "Singleplayer",
            "button2.png",
            () -> Greenfoot.setWorld(new SingleplayerLobby())
        );
        addObject(singleplayer, 180, 340);
        
        Button multiplayer = new Button(
            "Multiplayer",
            "button2.png",
            () -> Greenfoot.setWorld(new MultiplayerMenu())
        );
        addObject(multiplayer, 180, 400);
        
        Button options = new Button(
            "Options",
            "button2.png",
            () -> Greenfoot.setWorld(new OptionsMenu())
        );
        addObject(options, 180, 460);
        
        Button exit = new Button(
            "Exit",
            "button2.png",
            () -> Greenfoot.stop()
        );
        addObject(exit, 180, 520);
    }
}