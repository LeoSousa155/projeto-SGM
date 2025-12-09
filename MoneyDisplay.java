import greenfoot.*;

/**
 * Displays the player's money at the top of the screen.
 * Also acts as a simple global money manager.
 *
 * Usage:
 *   - Add ONE MoneyDisplay object to the world (e.g. SingleplayerPlaying)
 *   - Call MoneyDisplay.addMoney(+100) or MoneyDisplay.addMoney(-100)
 *   - It starts at 0$ by default (or call resetMoney() when a new game starts)
 */
public class MoneyDisplay extends Actor
{
    // Global money value for the game
    private static int money = 0;
    
    // Keep a reference to the on-screen instance so we can update its image
    private static MoneyDisplay instance;

    // Text config
    private static final int FONT_SIZE = 24;

    public MoneyDisplay()
    {
        instance = this;
        redraw();
    }

    /** Reset money to 0$ (e.g. at the start of a new game). */
    public static void resetMoney()
    {
        money = 0;
        if (instance != null)
        {
            instance.redraw();
        }
    }

    /** Add (or subtract) money. Negative values are allowed. */
    public static void addMoney(int delta)
    {
        money += delta;  // <-- no clamping; can go negative
    
        if (instance != null)
        {
            instance.redraw();
        }
    }

    /** Get current money value. */
    public static int getMoney()
    {
        return money;
    }

    /** Re-draw the text image. */
    private void redraw()
    {
        String text = money + " $";

        // Transparent/soft background behind the text
        Color textColor = Color.WHITE;
        Color bgColor = new Color(0, 0, 0, 128); // semi-transparent black

        GreenfootImage img = new GreenfootImage(text, FONT_SIZE, textColor, bgColor);
        setImage(img);
    }

    public void act()
    {
        // No per-frame logic needed for now.
        // Kept here in case you want flashing or animation later.
    }
}