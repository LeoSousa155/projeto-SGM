import greenfoot.*;

public class MoneyDisplay extends Actor
{
    private static int money = 0;

    private static int moneyGained = 0;
    private static int moneyLost = 0;

    private static MoneyDisplay instance;

    private static final int FONT_SIZE = 24;

    private static boolean endShown = false;

    // ✅ NEW: schedule endgame for NEXT frame
    private static boolean endPending = false;
    private static boolean pendingWin = false;

    public MoneyDisplay()
    {
        instance = this;
        redraw();
    }

    public static void debugSetMoney(int value)
    {
        money = value;
        moneyGained = Math.max(0, value);
        moneyLost = 0;

        endShown = false;
        endPending = false;

        if (instance != null) instance.redraw();
    }

    public static void resetMoney()
    {
        money = 0;
        moneyGained = 0;
        moneyLost = 0;

        endShown = false;
        endPending = false;

        if (instance != null) instance.redraw();
    }

    public static void addMoney(int delta)
    {
        money += delta;

        if (delta > 0) moneyGained += delta;
        if (delta < 0) moneyLost += -delta;

        if (instance != null) instance.redraw();

        checkEndgameSchedule(); // ✅ schedule only
    }

    public static int getMoney() { return money; }
    public static int getMoneyGained() { return moneyGained; }
    public static int getMoneyLost() { return moneyLost; }

    private static void checkEndgameSchedule()
    {
        if (endShown || endPending) return;

        if (money >= 2000)
        {
            endShown = true;
            endPending = true;
            pendingWin = true;
        }
        else if (money <= -500)
        {
            endShown = true;
            endPending = true;
            pendingWin = false;
        }
    }

    // ✅ Call this from the World act() (next frame)
    public static void processEndgameIfPending(World w)
    {
        if (!endPending) return;
        endPending = false;

        // Close any active minigame safely (we are now in a NEW frame)
        if (MinigameLock.isLocked())
            MinigameLock.forceCloseActiveMinigame();

        // Show panel
        EndgamePanel.show(w, pendingWin);
    }

    private void redraw()
    {
        String text = money + " $";
        setImage(new GreenfootImage(text, FONT_SIZE, Color.WHITE, new Color(0,0,0,128)));
    }
}