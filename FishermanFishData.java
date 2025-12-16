import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FishermanFishData
{
    private static final Random rng = new Random();
    private static int fishesReleased = 0;

    // ===== FISH LIST (your provided set) =====
    private static final FishSpecies[] COMMON = new FishSpecies[] {
        new FishSpecies("Atlantic damselfish", FishRarity.COMMON, "Small",
            "Shallow", "Reef", "Fish", "Not threatened", "damselfish.png"),

        new FishSpecies("Rainbow wrasse", FishRarity.COMMON, "Small",
            "Shallow–Mid", "Pelagic shoals", "invertebrates", "Not threatened", "rainbow-wrasse.png"),

        new FishSpecies("Blue jack mackerel", FishRarity.COMMON, "Medium",
            "Surface–Mid", "Pelagic shoals", "crustaceans", "Not threatened", "jack-mackerel.png")
    };

    private static final FishSpecies[] UNCOMMON = new FishSpecies[] {
        new FishSpecies("Dusky grouper", FishRarity.UNCOMMON, "Medium–large",
            "Shallow–Mid", "Reef", "invertebrates", "Endangered", "dusky-grouper.png"),

        new FishSpecies("Common dentex", FishRarity.UNCOMMON, "Medium–large",
            "Surface–Mid", "Oceanic pelagic", "crustaceans", "Not threatened", "common-dentex.png"),

        new FishSpecies("Amberjack", FishRarity.UNCOMMON, "Large",
            "Mid–deep", "Reef", "Fish", "Endangered", "amberjack.png")
    };

    private static final FishSpecies[] RARE = new FishSpecies[] {
        new FishSpecies("Bigeye tuna", FishRarity.RARE, "Large",
            "Mid–deep", "Oceanic pelagic", "invertebrates", "Not threatened", "bigeye-tuna.png"),

        new FishSpecies("Madeiran ray", FishRarity.RARE, "Medium",
            "Mid–deep", "Shallow–Mid", "Fish", "Endangered", "madeiran-ray.png"),

        new FishSpecies("Smalltooth sand tiger", FishRarity.RARE, "Medium–large",
            "Mid–deep", "Reef", "Fish", "Endangered", "smalltooth-sand-tiger.png")
    };

    public static List<FishSpecies> getAllSpecies()
    {
        List<FishSpecies> all = new ArrayList<>();
        all.addAll(Arrays.asList(COMMON));
        all.addAll(Arrays.asList(UNCOMMON));
        all.addAll(Arrays.asList(RARE));
        return all;
    }

    // ===== LOG =====
    public static class FishAttempt
    {
        public final FishSpecies fish;
        public final boolean caught;
        public final int value;

        // New Biologist-relevant:
        public final String currentSize; // "too small" or "ideal"

        public FishAttempt(FishSpecies fish, boolean caught, int value, String currentSize)
        {
            this.fish = fish;
            this.caught = caught;
            this.value = value;
            this.currentSize = currentSize;
        }
    }

    private static final List<FishAttempt> attempts = new ArrayList<>();
    private static final List<FishAttempt> pendingCaught = new ArrayList<>();

    public static void logAttempt(FishSpecies fish, boolean caught)
    {
        int value = fish.rarity.value;

        // 30% too small, 70% ideal
        String currentSize = (rng.nextDouble() < 0.30) ? "too small" : "ideal";

        FishAttempt a = new FishAttempt(fish, caught, value, currentSize);
        attempts.add(a);

        if (caught)
            pendingCaught.add(a);
    }

    public static List<FishAttempt> getAttempts()
    {
        return attempts;
    }

    public static boolean hasPendingCaught()
    {
        return !pendingCaught.isEmpty();
    }

    public static FishAttempt peekNextPending()
    {
        if (pendingCaught.isEmpty()) return null;
        return pendingCaught.get(0);
    }

    public static FishAttempt popNextPending()
    {
        if (pendingCaught.isEmpty()) return null;
        return pendingCaught.remove(0);
    }
    
    public static void logBiologistRelease()
    {
        fishesReleased++;
    }
    
    public static int getFishesReleased()
    {
        return fishesReleased;
    }
    
    public static int getFishesFished()
    {
        return attempts.size(); // total attempts (caught + lost)
    }

    // ===== RNG =====
    public static FishSpecies rollFish()
    {
        FishRarity rarity = rollRarity();
        return randomFromRarity(rarity);
    }

    private static FishRarity rollRarity()
    {
        int r = rng.nextInt(100);
        if (r < 50) return FishRarity.COMMON;
        if (r < 83) return FishRarity.UNCOMMON;
        return FishRarity.RARE;
    }

    private static FishSpecies randomFromRarity(FishRarity rarity)
    {
        FishSpecies[] pool;
        switch (rarity)
        {
            case COMMON: pool = COMMON; break;
            case UNCOMMON: pool = UNCOMMON; break;
            default: pool = RARE; break;
        }
        return pool[rng.nextInt(pool.length)];
    }
}