public class EngineRepairState
{
    private static boolean needsRepair = false;

    public static boolean needsRepair()
    {
        return needsRepair;
    }

    public static void setNeedsRepair(boolean v)
    {
        needsRepair = v;
    }
}