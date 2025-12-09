import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * World where the player walks around a big map and the camera follows.
 */
public class SingleplayerPlaying extends World implements CaptainMinigameController.ResultListener
{
    // Size of the visible window (camera view)
    public static final int VIEW_WIDTH  = 800;
    public static final int VIEW_HEIGHT = 600;

    // The big map image
    private GreenfootImage mapImage;
    private int mapWidth;
    private int mapHeight;

    // Camera top-left position in map coordinates
    private int camX = 0;
    private int camY = 0;

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
    
    @Override
    public void onCaptainMinigameSuccess() {
        // currently empty – money is already handled inside the minigame
    }

    @Override
    public void onCaptainMinigameFailure() {
        // currently unused; rock penalty is handled inside the minigame
    }

    public SingleplayerPlaying()
    {
        // World size = window size, not full map size
        super(VIEW_WIDTH, VIEW_HEIGHT, 1, false);

        setPaintOrder(
            Button.class, Text.class, 
            CaptainBoat.class, CaptainGoalZone.class, CaptainRock.class, CaptainGameBackground.class, PanelBoard.class,
            ControllablePlayer.class, StairTrigger.class
        );
        
        // Optional: turn on collision debug visuals
        Solid.DEBUG = false;      // set to false when you're happy
        SlopeArea.DEBUG = false;  // see the slope area

        // Load original background
        mapImage = new GreenfootImage("boat1.jpg");

        // Stretch it to whatever size you want
        mapImage.scale(1789, 1200);

        // Save final size
        mapWidth  = mapImage.getWidth();
        mapHeight = mapImage.getHeight();

        // Numeric boundaries (playable area)
        int playMinX = 260;              // left boundary of playable area
        int playMaxX = mapWidth - 280;   // right boundary of playable area
        int playMaxY = mapHeight - 400;  // bottom boundary of playable area

        // --- Create Solid collision blocks ---

        // Left border
        int leftWidth = playMinX;
        Solid leftWall = new Solid(leftWidth, mapHeight, leftWidth / 2, mapHeight / 2);
        addObject(leftWall, 0, 0); // real position will be set in centerOn()

        // Right border
        int rightWidth = mapWidth - playMaxX;
        Solid rightWall = new Solid(rightWidth, mapHeight,
                                    playMaxX + rightWidth / 2,
                                    mapHeight / 2);
        addObject(rightWall, 0, 0);

        // Floor collisions

        // --- Slope surface ---
        SlopeArea medSlope = new SlopeArea(2000, 800, 500, 750);
        addObject(medSlope, 0, 0);
        
        SlopeArea medSlope2 = new SlopeArea(500, 750, 100, 700);
        addObject(medSlope2, 0, 0);
        
        SlopeArea lowSlope = new SlopeArea(450, 950, 50, 900);
        addObject(lowSlope, 0, 0);
        
        SlopeArea lowSlope2 = new SlopeArea(1750, 900, 1350, 950);
        addObject(lowSlope2, 0, 0);

        // Start camera around the area
        centerOn(mapWidth / 2, 765);

        // Create the player, place at center of view,
        // and set his world (map) position (y=765 as you had)
        JackClass player = new JackClass();
        addObject(player, VIEW_WIDTH / 2, VIEW_HEIGHT / 2);
        player.setWorldLocation(mapWidth / 2, 765); //765 = Med; 565 = Top; 950 = Low

        // --- Stairs triggers ---
        StairTrigger upMedTrigger = new StairTrigger(
            StairTrigger.TYPE_UP,
            1120, 765,     // trigger area world pos (x, y)
            900, 565,      // teleport target (x, y)
            100, 60,
            225
        );
        addObject(upMedTrigger, 0, 0);
        
        StairTrigger downTopTrigger = new StairTrigger(
            StairTrigger.TYPE_DOWN,
            1000, 565,
            1120, 765,
            100, 60,
            90
        );
        addObject(downTopTrigger, 0, 0);
        
        StairTrigger upLowTrigger = new StairTrigger(
            StairTrigger.TYPE_UP,
            1170, 915,     // trigger area world pos (x, y)
            920, 765,      // teleport target (x, y)
            100, 60,
            225
        );
        addObject(upLowTrigger, 0, 0);
        
        StairTrigger downMedTrigger = new StairTrigger(
            StairTrigger.TYPE_DOWN,
            950, 765,
            1120, 950,
            100, 60,
            45
        );
        addObject(downMedTrigger, 0, 0);
        
        // --- Captain minigame trigger on the captain's cabin ---
        CaptainMinigameTrigger captainTrigger = new CaptainMinigameTrigger(
            900, 565
        );
        addObject(captainTrigger, 0, 0);

        // --- UI buttons (fixed to the screen) ---
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

        // --- Money display between Options and Main Menu ---
        MoneyDisplay.resetMoney();  // start at 0$ for this game
        MoneyDisplay moneyDisplay = new MoneyDisplay();
        addObject(moneyDisplay, VIEW_WIDTH / 2, 20); // x=400, between 100 and 700

        // After everything is added, ensure solids & slope are placed correctly
        updateAllSolidPositions();
    }
    
    /** Center the camera around a position in MAP coordinates. */
    public void centerOn(int worldX, int worldY)
    {
        camX = worldX - VIEW_WIDTH / 2;
        camY = worldY - VIEW_HEIGHT / 2;

        // Clamp camera to the edges of the map
        camX = Math.max(0, Math.min(camX, mapWidth  - VIEW_WIDTH));
        camY = Math.max(0, Math.min(camY, mapHeight - VIEW_HEIGHT));

        updateBackground();
        updateAllSolidPositions();
    }

    /** Convert map/world X to screen X. */
    public int worldToScreenX(int worldX)
    {
        return worldX - camX;
    }

    /** Convert map/world Y to screen Y. */
    public int worldToScreenY(int worldY)
    {
        return worldY - camY;
    }

    /** Convert screen X to map/world X. */
    public int screenToWorldX(int screenX)
    {
        return screenX + camX;
    }

    /** Convert screen Y to map/world Y. */
    public int screenToWorldY(int screenY)
    {
        return screenY + camY;
    }

    public int getMapWidth()
    {
        return mapWidth;
    }

    public int getMapHeight()
    {
        return mapHeight;
    }

    /** Draw the visible part of the big background into the world. */
    private void updateBackground()
    {
        GreenfootImage view = new GreenfootImage(VIEW_WIDTH, VIEW_HEIGHT);
        view.drawImage(mapImage, -camX, -camY);
        setBackground(view);
    }

    /** Reposition all Solid, SlopeArea, StairTrigger and CaptainMinigameTrigger blocks according to the current camera. */
    private void updateAllSolidPositions()
    {
        List<Solid> solids = getObjects(Solid.class);
        for (Solid s : solids)
        {
            s.updateScreenPosition(this);
        }
    
        List<SlopeArea> slopes = getObjects(SlopeArea.class);
        for (SlopeArea sa : slopes)
        {
            sa.updateScreenPosition(this);
        }
    
        // Stair triggers
        List<StairTrigger> triggers = getObjects(StairTrigger.class);
        for (StairTrigger st : triggers)
        {
            st.updateScreenPosition(this);
        }

        // Captain minigame trigger
        List<CaptainMinigameTrigger> captainTriggers =
            getObjects(CaptainMinigameTrigger.class);
        for (CaptainMinigameTrigger ct : captainTriggers)
        {
            ct.updateScreenPosition(this);
        }
    }

    /** Called once per frame. Used here to tick the minigame reopen cooldown. */
    public void act()
    {
        CaptainMinigameController.tickReopenTimer();
    }
}
