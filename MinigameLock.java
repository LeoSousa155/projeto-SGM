public class MinigameLock
{
    private static boolean locked = false;
    private static Runnable forceCloseCallback = null;

    public static boolean tryLock()
    {
        if (locked) return false;
        locked = true;
        return true;
    }

    public static void setLocked(boolean value)
    {
        locked = value;
        if (!locked)
            forceCloseCallback = null;
    }

    public static boolean isLocked()
    {
        return locked;
    }

    /** Register a cleanup action for the currently running minigame */
    public static void registerForceClose(Runnable r)
    {
        forceCloseCallback = r;
    }

    /** Immediately closes the active minigame (if any) */
    public static void forceCloseActiveMinigame()
    {
        if (forceCloseCallback != null)
        {
            forceCloseCallback.run();
            forceCloseCallback = null;
        }
        locked = false;
    }
}