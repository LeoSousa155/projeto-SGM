import greenfoot.*;

public class MusicManager
{
    private static GreenfootSound menuMusic = new GreenfootSound("happy_mambo.wav");

    // Is this track meant to be active in the current world? (MainMenu, Options, etc.)
    private static boolean menuMusicEnabled = false;

    // Is the scenario currently "paused"? (Greenfoot stopped)
    private static boolean scenarioPaused = false;

    // Volume knobs (0–100)
    private static int masterVolume = 50;
    private static int musicVolume  = 100;

    // Remember last non-muted volume so we can "unpause" nicely if you want
    private static int lastEffectiveVolume = 100;

    /** Call from menu-like worlds where menu music should exist (MainMenu, Options, etc.) */
    public static void enableMenuMusic()
    {
        menuMusicEnabled = true;

        // Start loop only once; no stop/start spam
        if (!menuMusic.isPlaying()) {
            menuMusic.playLoop();
        }

        applyVolume();
    }

    /** Call from worlds where menu music should NOT be heard (e.g., gameplay). */
    public static void disableMenuMusic()
    {
        menuMusicEnabled = false;
        applyVolume(); // this will effectively mute it
    }

    /** Scenario resumed (Run pressed). */
    public static void onScenarioStarted()
    {
        scenarioPaused = false;

        if (menuMusicEnabled && !menuMusic.isPlaying()) {
            menuMusic.playLoop();
        }

        applyVolume();
    }

    /** Scenario paused (Pause pressed). */
    public static void onScenarioStopped()
    {
        scenarioPaused = true;
        applyVolume();   // mute while "paused"
    }

    public static int getMasterVolume()
    {
        return masterVolume;
    }

    public static void setMasterVolume(int value)
    {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        masterVolume = value;
        applyVolume();
    }

    public static int getMusicVolume()
    {
        return musicVolume;
    }
    
    public static void setMusicVolume(int value)
    {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        musicVolume = value;
        applyVolume();
    }

    private static void applyVolume()
    {
        int effective = masterVolume * musicVolume / 100;  // 0–100

        if (!menuMusicEnabled || scenarioPaused) {
            // logically "off" → mute
            menuMusic.setVolume(0);
        } else {
            menuMusic.setVolume(effective);
            lastEffectiveVolume = effective;
        }
    }
}