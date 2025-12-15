public enum FishRarity
{
    COMMON("Common", 50, 50),
    UNCOMMON("Uncommon", 33, 100),
    RARE("Rare", 17, 250);

    public final String label;
    public final int weightPercent;
    public final int value;

    FishRarity(String label, int weightPercent, int value)
    {
        this.label = label;
        this.weightPercent = weightPercent;
        this.value = value;
    }
}