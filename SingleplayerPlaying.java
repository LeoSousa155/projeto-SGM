import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SingleplayerPlaying here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SingleplayerPlaying extends World
{
    private static GreenfootImage bg = new GreenfootImage("background.jpg");
    
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
    
    public SingleplayerPlaying()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(bg.getWidth(), bg.getHeight(), 1); 
        setBackground(bg);
        
        // Adiciona o barco ao centro do mundo
        Boat boat = new Boat();
        addObject(boat, bg.getWidth() / 2, bg.getHeight() / 2);
        
        Button mainMenu = new Button(
            "MainMenu",
            "button1.png",
            () -> Greenfoot.setWorld(new MainMenu())
        );
        addObject(mainMenu, 700, 20);
        Button option = new Button(
            "Options",
            "button1.png",
            () -> Greenfoot.setWorld(new OptionsMenuGame())
        );
        addObject(option, 100, 20);
        
    }
}
