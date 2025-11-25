import greenfoot.*;
import java.util.*;

/**
 * Write a description of class OptionsMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OptionsMenu extends World
{
    private static GreenfootImage bg = new GreenfootImage("background.png");
    
    private enum PanelType { NONE, GRAPHICS, CONTROLS, SOUND }
    private PanelType currentPanelType = PanelType.NONE;
    
    private PanelBoard currentPanel = null;
    
    private PanelBoard graphicsBoard;
    private Slider resolutionSlider;

    private PanelBoard controlsBoard;
    
    private PanelBoard soundBoard;
    private Slider masterSlider;
    private Slider musicSlider;
    private Slider ambientSlider;
    private Slider actionSlider;
    
    static {
        bg.scale(800, 600);
    }
    
    @Override
    public void started()
    {
        // OptionsMenu also uses the same menu music
        MusicManager.enableMenuMusic();
        MusicManager.onScenarioStarted();
    }
    
    @Override
    public void stopped()
    {
        MusicManager.onScenarioStopped();
    }
    
    /**
     * Constructor for objects of class MenuPrincipal.
     */
    public OptionsMenu()
    {    
        // Create world using the image's width and height
        super(bg.getWidth(), bg.getHeight(), 1);
        setBackground(bg);
        
        //addObject(new DebugMouse(), 0, 0);
        
        Button graphics = new Button(
            "Graphics",
            "button2.png",
            () -> showGraphicsPanel()
        );
        addObject(graphics, 180, 340);
        
        Button controls = new Button(
            "Controls",
            "button2.png",
            () -> showControlsPanel()
        );
        addObject(controls, 180, 400);
        
        Button sound = new Button(
            "Sound",
            "button2.png",
            () -> showSoundPanel()
        );
        addObject(sound, 180, 460);
        
        Button back = new Button(
            "Back",
            "button2.png",
            () -> Greenfoot.setWorld(new MainMenu())
        );
        addObject(back, 180, 520);
        
        // paint order
        setPaintOrder(Button.class, Text.class, Slider.class, PanelBoard.class);
    }

    public void act() 
    {
        updateMusicVolume();
    }
    
    private void updateMusicVolume()
    {
        // Only update values when sliders actually exist in the world.
        if (masterSlider != null && masterSlider.getWorld() != null) {
            MusicManager.setMasterVolume(masterSlider.getValue());
        }
    
        if (musicSlider != null && musicSlider.getWorld() != null) {
            MusicManager.setMusicVolume(musicSlider.getValue());
        }
    }

    public void openPanel(PanelBoard newPanel, int x, int y)
    {
        // Close the old panel (if any)
        if (currentPanel != null && currentPanel.getWorld() != null)
        {
            currentPanel.removePanel();
        }
    
        // Add and activate the new one
        addObject(newPanel, x, y);
        currentPanel = newPanel;
    }

    private void showGraphicsPanel() 
    {
        
        if (currentPanelType == PanelType.GRAPHICS) {
            return;
        }
        currentPanelType = PanelType.GRAPHICS;
    
        graphicsBoard = new PanelBoard("panelboard.png");
    
        openPanel(graphicsBoard, getWidth()/2 + 150, getHeight()/2);
        
        Text qualityText = new Text("Overall Quality:", 25, Color.WHITE);
        addObject(qualityText, 420, 150);
        graphicsBoard.addContent(qualityText);
        
        Button lowBtn = new Button(
            "Low", 
            20, 
            "select.png",
            80, 40,
            () -> { /* TODO */ }
        );
        addObject(lowBtn, 400, 200);
        graphicsBoard.addContent(lowBtn);
        
        Button mediumBtn = new Button(
            "Medium",
            20,
            "select.png",
            80, 40,
            () -> { /* TODO */ }
        );
        addObject(mediumBtn, 500, 200);
        graphicsBoard.addContent(mediumBtn);
        
        Button highBtn = new Button(
            "High",
            20,
            "select.png",
            80, 40,
            () -> { /* TODO */ }
        );
        addObject(highBtn, 600, 200);
        graphicsBoard.addContent(highBtn);
        
        Button customBtn = new Button(
            "Custom",
            20,
            "select.png",
            80, 40,
            () -> { /* TODO */ }
        );
        addObject(customBtn, 700, 200);
        graphicsBoard.addContent(customBtn);
        
        Text resolutionText = new Text("Resolution Scale:", 25, Color.WHITE);
        addObject(resolutionText, 430, 250);
        graphicsBoard.addContent(resolutionText);
        
        resolutionSlider = new Slider(0, 100, 50, 350);
        addObject(resolutionSlider, 550, 280);
        graphicsBoard.addContent(resolutionSlider);
    }
    
    private void showControlsPanel()
    {
        if (currentPanelType == PanelType.CONTROLS) {
            return;
        }
        currentPanelType = PanelType.CONTROLS;
        
        controlsBoard = new PanelBoard("panelboard.png");

        openPanel(controlsBoard, getWidth()/2 + 150, getHeight()/2);
        
        Button EditBtn = new Button(
            "Edit Keybinds", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(EditBtn, 400, 170);
        controlsBoard.addContent(EditBtn);
        
        Button LeftBtn = new Button(
            "Move Left", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(LeftBtn, 400, 220);
        controlsBoard.addContent(LeftBtn);
        
        Button LeftBtnSelect = new Button(
            "A", 
            20, 
            "select.png",
            220, 40,
            () -> { /* TODO */ }
        );
        addObject(LeftBtnSelect, 600, 220);
        controlsBoard.addContent(LeftBtnSelect);
        
        Button RightBtn = new Button(
            "Move Right", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(RightBtn, 400, 270);
        controlsBoard.addContent(RightBtn);
        
        Button RightBtnSelect = new Button(
            "D", 
            20, 
            "select.png",
            220, 40,
            () -> { /* TODO */ }
        );
        addObject(RightBtnSelect, 600, 270);
        controlsBoard.addContent(RightBtnSelect);
        
        Button AscendBtn = new Button(
            "Ascend", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(AscendBtn, 400, 320);
        controlsBoard.addContent(AscendBtn);
        
        Button AscendBtnSelect = new Button(
            "W", 
            20, 
            "select.png",
            220, 40,
            () -> { /* TODO */ }
        );
        addObject(AscendBtnSelect, 600, 320);
        controlsBoard.addContent(AscendBtnSelect);
        
        Button DescendBtn = new Button(
            "Descend", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(DescendBtn, 400, 370);
        controlsBoard.addContent(DescendBtn);
        
        Button DescendBtnSelect = new Button(
            "S", 
            20, 
            "select.png",
            220, 40,
            () -> { /* TODO */ }
        );
        addObject(DescendBtnSelect, 600, 370);
        controlsBoard.addContent(DescendBtnSelect);
        
        Button InteractBtn = new Button(
            "Interact", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(InteractBtn, 400, 420);
        controlsBoard.addContent(InteractBtn);
        
        Button InteractBtnSelect = new Button(
            "E", 
            20, 
            "select.png",
            220, 40,
            () -> { /* TODO */ }
        );
        addObject(InteractBtnSelect, 600, 420);
        controlsBoard.addContent(InteractBtnSelect);
    }
    
    private void showSoundPanel() 
    {
        int initialMaster = MusicManager.getMasterVolume();
        int initialMusic  = MusicManager.getMusicVolume();
        
        if (currentPanelType == PanelType.SOUND) {
            return;
        }
        currentPanelType = PanelType.SOUND;
        
        soundBoard = new PanelBoard("panelboard.png");

        // Again, use openPanel so any existing panel is closed
        openPanel(soundBoard, getWidth()/2 + 150, getHeight()/2);
        
        Text masterText = new Text("Master Volume:", 25, Color.WHITE);
        addObject(masterText, 420, 150);
        soundBoard.addContent(masterText);
        
        masterSlider = new Slider(0, 100, initialMaster, 350);
        addObject(masterSlider, 550, 180);
        soundBoard.addContent(masterSlider);
        
        Text musicText = new Text("Music Volume:", 25, Color.WHITE);
        addObject(musicText, 420, 210);
        soundBoard.addContent(musicText);
        
        musicSlider = new Slider(0, 100, initialMusic, 350);
        addObject(musicSlider, 550, 240);
        soundBoard.addContent(musicSlider);
        
        Text ambientText = new Text("Ambient Volume:", 25, Color.WHITE);
        addObject(ambientText, 420, 270);
        soundBoard.addContent(ambientText);
        
        ambientSlider = new Slider(0, 100, 100, 350);
        addObject(ambientSlider, 550, 300);
        soundBoard.addContent(ambientSlider);
        
        Text actionText = new Text("Action Volume:", 25, Color.WHITE);
        addObject(actionText, 420, 330);
        soundBoard.addContent(actionText);
        
        actionSlider = new Slider(0, 100, 100, 350);
        addObject(actionSlider, 550, 360);
        soundBoard.addContent(actionSlider);
        
        Text langText = new Text("Language:", 25, Color.WHITE);
        addObject(langText, 400, 390);
        soundBoard.addContent(langText);
        
        Button LangBtnPt = new Button(
            "Português", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(LangBtnPt, 450, 430);
        soundBoard.addContent(LangBtnPt);
        
        Button LangBtnEng = new Button(
            "English", 
            20, 
            "select.png",
            120, 40,
            () -> { /* TODO */ }
        );
        addObject(LangBtnEng, 650, 430);
        soundBoard.addContent(LangBtnEng);
    }
}