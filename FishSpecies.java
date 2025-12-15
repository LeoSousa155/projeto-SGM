public class FishSpecies
{
    public final String species;        // display name (Species)
    public final FishRarity rarity;     // Common/Uncommon/Rare
    public final String speciesSize;    // Small / Medium / Medium–large / Large
    public final String depth;          // Shallow / Shallow–Mid / Surface–Mid / Mid–deep
    public final String habitat;        // Reef / Pelagic shoals / Oceanic pelagic
    public final String diet;           // Fish / invertebrates / crustaceans
    public final String threatStatus;   // Not threatened / Endangered
    public final String imageFile;

    public FishSpecies(
        String species,
        FishRarity rarity,
        String speciesSize,
        String depth,
        String habitat,
        String diet,
        String threatStatus,
        String imageFile
    )
    {
        this.species = species;
        this.rarity = rarity;
        this.speciesSize = speciesSize;
        this.depth = depth;
        this.habitat = habitat;
        this.diet = diet;
        this.threatStatus = threatStatus;
        this.imageFile = imageFile;
    }
}