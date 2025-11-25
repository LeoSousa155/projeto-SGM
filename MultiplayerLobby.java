import greenfoot.*;

/**
 * Write a description of class MultiplayerLobby here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MultiplayerLobby extends World
{
    private static GreenfootImage bg = new GreenfootImage("menu_lobby.png");
    
    private Couch couch           = new Couch();
    private PlayerClass captain   = new CaptainClass();
    private PlayerClass engineer  = new EngineerClass();
    private PlayerClass biologist = new BiologistClass();
    private PlayerClass fisherman = new FishermanClass();

    private PlayerClass currentPlayer;   // the one currently on the couch
    
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
    public MultiplayerLobby()
    {    
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
        
        setPaintOrder(
            Button.class,      // top
            Couch.class,       // middle
            PlayerClass.class  // bottom
        );
        
        addObject(couch, 500, 430);
        
        createButtons();
        
        Button start = new Button(
            "Start",
            "button2.png",
            () -> Greenfoot.setWorld(new MainMenu())
        );
        addObject(start, 150, 350);
        
        Button back = new Button(
            "Back",
            "button2.png",
            () -> Greenfoot.setWorld(new MainMenu())
        );
        addObject(back, 150, 410);

    }
    
    /** show exactly this player at (x,y) */
    private void showPlayer(PlayerClass p, int x, int y) {
        if (currentPlayer != null) {
            removeObject(currentPlayer);
        }
        currentPlayer = p;
        addObject(currentPlayer, x, y);
        
        // force couch to top
        removeObject(couch);
        addObject(couch, 500, 430);
    }

    private void createButtons() {
        Button selectCap = new Button(
            "Select\nCaptain",
            "select.png",
            100, 70,
            () -> showPlayer(captain, 380, 400)
        );
        addObject(selectCap, 370, 550);

        Button selectEng = new Button(
            "Select\nEngineer",
            "select.png",
            100, 70,
            () -> showPlayer(engineer, 480, 400)
        );
        addObject(selectEng, 480, 550);
        
        Button selectBio = new Button(
            "Select\nBiologist",
            "select.png",
            100, 70,
            () -> showPlayer(biologist, 590, 400)
        );
        addObject(selectBio, 590, 550);
        
        Button selectFish = new Button(
            "Select\nFisherman",
            "select.png",
            100, 70,
            () -> showPlayer(fisherman, 690, 400)
        );
        addObject(selectFish, 700, 550);
    }
}
