import greenfoot.World;
import greenfoot.Greenfoot;

public class WorldNavigator
{
    private static World previousWorld = null;

    public static void goTo(World from, World next)
    {
        previousWorld = from;
        Greenfoot.setWorld(next);
    }

    public static void backOr(World fallback)
    {
        if (previousWorld != null)
        {
            World w = previousWorld;
            previousWorld = null;
            Greenfoot.setWorld(w);
        }
        else
        {
            Greenfoot.setWorld(fallback);
        }
    }
}