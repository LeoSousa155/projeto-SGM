import greenfoot.*;

public class EndgamePanel
{
    public static void show(World world, boolean win)
    {
        // Lock the game UI so no minigames can open anymore
        MinigameLock.setLocked(true);

        PanelBoard panel = new PanelBoard("panelboard.png", 520, 380);

        int cx = world.getWidth() / 2;
        int cy = world.getHeight() / 2;

        world.addObject(panel, cx, cy);

        String title = win
            ? "Congratulations! You reached 2000$!"
            : "Game Over... You reached -500$...";

        Text titleText = new Text(title, 28, win ? Color.GREEN : Color.RED, true);
        panel.addContent(titleText, 0, -panel.getHalfHeight() + 50);

        int fishesFished   = FishermanFishData.getFishesFished();
        int fishesReleased = FishermanFishData.getFishesReleased();
        int moneyGained    = MoneyDisplay.getMoneyGained();
        int moneyLost      = MoneyDisplay.getMoneyLost();

        String stats =
            "Fishes Fished: " + fishesFished + "\n" +
            "Fishes Released: " + fishesReleased + "\n" +
            "Money Gained: " + moneyGained + "$\n" +
            "Money Lost: " + moneyLost + "$";

        Text statsText = new Text(stats, 24, Color.WHITE, true);
        panel.addContent(statsText, 0, -10);

        Button mainMenu = new Button(
            "Main Menu", "button1.png", 220, 60,
            () -> Greenfoot.setWorld(new MainMenu())
        );
        panel.addContent(mainMenu, 0, panel.getHalfHeight() - 60);
    }
}