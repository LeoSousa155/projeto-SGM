public class MinigameLock
{
    private static boolean locked = false;

    public static boolean isLocked() { return locked; }
    public static void setLocked(boolean value) { locked = value; }

    /** Returns true if it successfully locked; false if already locked. */
    public static boolean tryLock()
    {
        if (locked) return false;
        locked = true;
        return true;
    }
}