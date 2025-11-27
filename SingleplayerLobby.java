import greenfoot.*;

/**
 * Write a description of class MultiplayerLobby here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SingleplayerLobby extends World
{
    private static GreenfootImage bg = new GreenfootImage("menu_lobby.png");
    
    private Couch couch           = new Couch();
    private PlayerClass jackLobby = new JackLobby();

    static {
        bg.scale(800, 600);
    }
    
    @Override
    public void started()
    {
        MusicManager.enableMenuMusic();
        MusicManager.onScenarioStarted();
    }
    
    @Override
    public void stopped()
    {
        MusicManager.onScenarioStopped();
    }

    /**
     * Constructor for objects of class MultiplayerLobby.
     * 
     */
    public SingleplayerLobby()
    {    
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
        
        setPaintOrder(
            Button.class,      // top
            Couch.class,       // middle
            PlayerClass.class  // bottom
        );
        
        addObject(jackLobby, 570, 380);
        addObject(couch, 500, 430);
        
        Button start = new Button(
            "Start",
            "button2.png",
            () -> Greenfoot.setWorld(new SingleplayerPlaying())
        );
        addObject(start, 150, 350);
        
        Button back = new Button(
            "Back",
            "button2.png",
            () -> Greenfoot.setWorld(new MainMenu())
        );
        addObject(back, 150, 410);
        


    }
}
