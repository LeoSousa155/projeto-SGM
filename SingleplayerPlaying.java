import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * World where the player walks around a big map and the camera follows.
 */
public class SingleplayerPlaying extends World 
implements CaptainMinigameController.ResultListener,
           EngineerMinigameController.ResultListener,
           FishermanMinigameController.ResultListener,
           BiologistMinigameController.ResultListener
{
    // Size of the visible window (camera view)
    public static final int VIEW_WIDTH  = 800;
    public static final int VIEW_HEIGHT = 600;
    
    private static GreenfootImage CACHED_MAP = null;

    // The big map image
    private GreenfootImage mapImage;
    private int mapWidth;
    private int mapHeight;
    
    private boolean tutorialPromptShown = false;

    public boolean isTutorialPromptShown() { return tutorialPromptShown; }
    public void setTutorialPromptShown(boolean v) { tutorialPromptShown = v; }

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
    public void onCaptainMinigameSuccess() {TutorialController.onCaptainMinigameClosed();}
    @Override
    public void onCaptainMinigameFailure() {TutorialController.onCaptainMinigameClosed();}
    
    @Override
    public void onEngineerMinigameSuccess() {TutorialController.onEngineerMinigameClosed();}
    @Override
    public void onEngineerMinigameFailure() {TutorialController.onEngineerMinigameClosed();}
    
    @Override 
    public void onFishermanMinigameSuccess() {TutorialController.onFishermanMinigameClosed();}
    @Override
    public void onFishermanMinigameFailure() {TutorialController.onFishermanMinigameClosed();}
    
    @Override
    public void onBiologistDone() {}

    public SingleplayerPlaying()
    {
        // World size = window size, not full map size
        super(VIEW_WIDTH, VIEW_HEIGHT, 1, false);

        setPaintOrder(
            Button.class, Text.class, 
            CaptainBoat.class, CaptainGoalZone.class, CaptainRock.class,
            EngineerWirePeg.class, EngineerWireLayer.class, EngineerGameBackground.class,
            FishermanSkillCheckWheel.class, CaptainGameBackground.class,
            BiologistDraggableFish.class, BiologistDropZone.class,
            PanelImage.class,
            PanelBoard.class, TutorialArrow.class, Door.class,
            ControllablePlayer.class, StairTrigger.class
        );
        
        MinigameLock.setLocked(false);
        
        // === DEBUG ===
        Solid.DEBUG = false;      // set to false when you're happy
        SlopeArea.DEBUG = false;  // see the slope area

        if (CACHED_MAP == null) {
            GreenfootImage img = new GreenfootImage("boat3.jpg");
            img.scale(1789, 1200);
            CACHED_MAP = img;
        }
        mapImage = CACHED_MAP;

        // Save final size
        mapWidth  = mapImage.getWidth();
        mapHeight = mapImage.getHeight();

        // Numeric boundaries (playable area)
        int playMinX = 260;              // left boundary of playable area
        int playMaxX = mapWidth - 280;   // right boundary of playable area
        int playMaxY = mapHeight - 400;  // bottom boundary of playable area

        // --- Create Solid collision blocks ---

        // Left borders
        int leftWidth = playMinX;
        Solid leftWall = new Solid(leftWidth, mapHeight, leftWidth / 2, mapHeight / 2);
        addObject(leftWall, 0, 0); // real position will be set in centerOn()
        
        Solid leftUpWall = new Solid(20, mapHeight, 800, mapHeight / 30);
        addObject(leftUpWall, 0, 0);

        // Right border
        int rightWidth = mapWidth - playMaxX;
        Solid rightWall = new Solid(rightWidth, mapHeight,
                                    playMaxX + rightWidth / 2,
                                    mapHeight / 2);
        addObject(rightWall, 0, 0);
        
        Solid rightUpWall = new Solid(20, mapHeight, 1150, mapHeight / 30);
        addObject(rightUpWall, 0, 0);
        
        Solid rightMidWall = new Solid(20, mapHeight, 1280, mapHeight / 5);
        addObject(rightMidWall, 0, 0);

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

        Door door = new Door();
        addObject(door, 0, 0);
        
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
            1000, 765,
            1120, 950,
            100, 60,
            45
        );
        addObject(downMedTrigger, 0, 0);
        
        // --- Captain minigame trigger on the captain's cabin ---
        CaptainMinigameTrigger captainTrigger = new CaptainMinigameTrigger(
            843, 564
        );
        addObject(captainTrigger, 0, 0);
        
        // --- Engineer minigame trigger on the engine room ---
        EngineerMinigameTrigger engineerTrigger = new EngineerMinigameTrigger(
            1440, 950
        );
        addObject(engineerTrigger, 0, 0);
        
        // --- Fisherman minigame trigger on the boat's tip  ---
        FishermanMinigameTrigger fishTrigger = new FishermanMinigameTrigger(
            364, 740,
            5            // required hits
        );
        addObject(fishTrigger, 0, 0);
        
        // --- Biologist minigame trigger on the research room  ---
        BiologistMinigameTrigger bioTrigger = new BiologistMinigameTrigger(
            mapWidth/2 + 25, 766
        );
        addObject(bioTrigger, 0, 0);

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
            () -> WorldNavigator.goTo(this, new OptionsMenuGame())
        );
        addObject(option, 100, 20);

        // --- Money display between Options and Main Menu ---
        MoneyDisplay.resetMoney();  // start at 0$ for this game
        MoneyDisplay moneyDisplay = new MoneyDisplay();
        addObject(moneyDisplay, VIEW_WIDTH / 2, 20); // x=400, between 100 and 700
        
        // --- Debug ---
        //MoneyDisplay.debugSetMoney(974);
        //injectDebugFish();
        //TutorialController.enableDebugTutorial(TutorialController.DebugStep.BIOLOGIST);

        // After everything is added, ensure solids & slope are placed correctly
        updateAllSolidPositions();

        TutorialController.maybeShowTutorial(this);
    }
    
    private void injectDebugFish()
    {
        if (FishermanFishData.hasPendingCaught())
            return; // don’t spam if real fish already exist
    
        // Pick any fish you want to test
        FishSpecies debugFish = FishermanFishData.getAllSpecies().get(0);
    
        // Log it as if it was caught
        FishermanFishData.logAttempt(debugFish, true);
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
    
        // Door
        List<Door> door = getObjects(Door.class);
        for (Door d : door)
        {
            d.updateScreenPosition(this);
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
        
        // Engineer minigame trigger
        List<EngineerMinigameTrigger> engineerTriggers = getObjects(EngineerMinigameTrigger.class);
        for (EngineerMinigameTrigger et : engineerTriggers)
        {
            et.updateScreenPosition(this);
        }
        
        // Fisherman minigame trigger
        List<FishermanMinigameTrigger> fishTriggers = getObjects(FishermanMinigameTrigger.class);
        for (FishermanMinigameTrigger ft : fishTriggers)
        {
            ft.updateScreenPosition(this);
        }
        
        // Biologist minigame trigger
        List<BiologistMinigameTrigger> bioTriggers = getObjects(BiologistMinigameTrigger.class);
        for (BiologistMinigameTrigger bt : bioTriggers)
        {
            bt.updateScreenPosition(this);
        }
    }

    /** Called once per frame. Used here to tick the minigame reopen cooldown. */
    public void act()
    {
        MoneyDisplay.processEndgameIfPending(this);
        
        CaptainMinigameController.tickReopenTimer();
        EngineerMinigameController.tickReopenTimer();
        FishermanMinigameController.tickReopenTimer();
        BiologistMinigameController.tickReopenTimer();
    }
}
