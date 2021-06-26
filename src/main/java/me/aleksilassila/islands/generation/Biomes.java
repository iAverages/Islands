package me.aleksilassila.islands.generation;

import org.jetbrains.annotations.NotNull;import javax.annotation.Nullable;
import me.aleksilassila.islands.Islands;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.*;

public enum Biomes {
    INSTANCE;
    private final Islands plugin;

    private final World world;
    public HashMap<Biome, List<Location>> availableLocations;
    private final int biggestIslandSize;

    int biomeSearchJumpBlocks;
    int biomeSearchSize;
    int maxLocationsPerBiome;
    List<String> biomeBlacklist;

    private final Map<Biome, Location> randomLocations;

    Biomes() {
        this.plugin = Islands.instance;
        this.world = Islands.islandsSourceWorld;
        this.biggestIslandSize = plugin.getConfig().getInt("generation.minBiomeSize");

        this.biomeSearchJumpBlocks = plugin.getConfig().getInt("generation.searchJump");
        this.biomeSearchSize = plugin.getConfig().getInt("generation.biomeSearchArea");
        this.maxLocationsPerBiome = plugin.getConfig().getInt("generation.maxVariationsPerBiome");
        this.biomeBlacklist = plugin.getConfig().getStringList("biomeBlacklist");

        this.availableLocations = new HashMap<>();
        randomLocations = new HashMap<>();

        // Generate biomes and save them to config
        if (plugin.getBiomesCache().getString("seed") == null || !String.valueOf(Islands.islandsSourceWorld.getSeed()).equals(plugin.getBiomesCache().getString("seed"))) {
            generateAndSaveBiomes();
        } else { // Load existing biomes from config
            loadBiomesFromConfig();
        }
    }

    private void loadBiomesFromConfig() {
        // Loop biomes
        for (String key : plugin.getBiomesCache().getKeys(false)) {
            Biome biome = getTargetBiome(key);
            if (biome != null) {
                List<Location> locations = new ArrayList<>();

                // Loop locations inside a biome
                for (String coordinatesIndex : plugin.getBiomesCache().getConfigurationSection(key).getKeys(false)) {
                    List<String> locationStrings = plugin.getBiomesCache().getStringList(key + "." + coordinatesIndex);

                    if (locationStrings.size() != 3) continue;

                    Location location = new Location(
                            Islands.islandsSourceWorld,
                            Integer.parseInt(locationStrings.get(0)),
                            Integer.parseInt(locationStrings.get(1)),
                            Integer.parseInt(locationStrings.get(2))
                    );

                    locations.add(location);
                }


                this.availableLocations.put(biome, locations);
            }
        }
    }

    private void generateAndSaveBiomes() {
        plugin.clearBiomesCache();
        this.availableLocations = generateIslandLocations();

        plugin.getBiomesCache().set("seed", String.valueOf(Islands.islandsSourceWorld.getSeed()));

        for (Biome biome : availableLocations.keySet()) {
            List<Location> locationsList = availableLocations.get(biome);

            int index = 0;
            for (Location location : locationsList) {
                List<String> stringsList = new ArrayList<>();

                stringsList.add(String.valueOf(location.getBlockX()));
                stringsList.add(String.valueOf(location.getBlockY()));
                stringsList.add(String.valueOf(location.getBlockZ()));

                plugin.getBiomesCache().set(biome.toString() + "." + index, stringsList);
                index++;
            }

        }

        plugin.saveBiomesConfig();
    }

    @Nullable
    private static Biome getTargetBiome(String biome) {
         Biome targetBiome = null;

         for (Biome b : Biome.values()) {
             if (b.name().equalsIgnoreCase(biome)) {
                 targetBiome = b;
             }
         }

         return targetBiome;
    }

    private boolean isBlacklisted(Biome biome) {
        for (String biomeName : biomeBlacklist) {
            if (biomeName.equalsIgnoreCase(biome.name())) return true;
        }

        return false;
    }

    @NotNull
    public HashMap<Biome, List<Location>> generateIslandLocations() {
        HashMap<Biome, List<Location>> locations = new HashMap<>();
        List<int[]> usedPositions = new ArrayList<>();

        plugin.getLogger().info("Generating biomes...");

        for (int x = 0; x < biomeSearchSize - biggestIslandSize; x += biomeSearchJumpBlocks) {
            zLoop: for (int z = 0; z < biomeSearchSize - biggestIslandSize; z += biomeSearchJumpBlocks) {
                for (int[] pos : usedPositions) {
                    if (pos[0] <= x && x <= pos[0] + biggestIslandSize && pos[1] <= z && z <= pos[1] + biggestIslandSize) {
                        z += biggestIslandSize;
                        continue zLoop;
                    }
                }

                Biome biome = getBiome(x, z);

                if (isBlacklisted(biome)
                        || (locations.containsKey(biome) && locations.get(biome).size() >= maxLocationsPerBiome)) {
                    continue;
                }

                if (isSuitableLocation(x, z, biggestIslandSize, biome)) {
                    Location location = new Location(world, x, 0, z);

                    if (locations.containsKey(biome)) {
                        locations.get(biome).add(location);
                    } else {
                        List<Location> list = new ArrayList<>();
                        list.add(location);
                        locations.put(biome, list);
                    }

                    usedPositions.add(new int[]{x, z});
                    z += biggestIslandSize;
                }

            }
        }

        Set<String> biomes = new HashSet<>();

        for (Biome biome : locations.keySet()) {
            biomes.add(biome.name());
        }

        plugin.getLogger().info("Locations generated for " + locations.size() + " biomes: " + String.join(", ", biomes));

        return locations;
    }

    private Biome getBiome(int x, int z) {
        return world.getBiome(x, 180, z);
    }

    boolean isSuitableLocation(int xCorner, int zCorner, int rectSize, Biome biome) {
        for (int x = 0; x < rectSize; x += biomeSearchJumpBlocks) {
            for (int z = 0; z < rectSize; z += biomeSearchJumpBlocks) {
                if (getBiome(xCorner + x, zCorner + z) != biome) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Biome getRandomBiome() {
        int size = INSTANCE.availableLocations.keySet().size();
        int item = new Random().nextInt(size);
        int i = 0;

        for (Biome biome : INSTANCE.availableLocations.keySet()) {
            if (i == item) return biome;
            i++;
        }

        // To make it always return biome (never gets here)
        return new ArrayList<>(INSTANCE.availableLocations.keySet()).get(0);
    }
}
