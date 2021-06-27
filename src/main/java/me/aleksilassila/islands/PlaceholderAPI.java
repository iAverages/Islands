package me.aleksilassila.islands;

import me.aleksilassila.islands.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class PlaceholderAPI extends PlaceholderExpansion {

    private final Islands islands;

    public PlaceholderAPI(Islands plugin) {
        this.islands = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return islands.getDescription().getAuthors().toString();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "sh";
    }

    @NotNull
    @Override
    public String getVersion() {
        return islands.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier){
        if (player == null)
            return "";

        if (identifier.equalsIgnoreCase("can_water_flow")) {
            if (!(player instanceof Player))
                return "";
            Player p = (Player) player;
            IslandsConfig.Entry island = Utils.getIslandFromBlock(p.getLocation().getBlock());
            if (island == null) return "";
            return island.allowWaterFlow ? "true" : "false";
        }

        if (identifier.equalsIgnoreCase("size")) {
            if (!(player instanceof Player))
                return "";
            Player p = (Player) player;
            IslandsConfig.Entry island = Utils.getIslandFromBlock(p.getLocation().getBlock());
            if (island == null) return "";
            return String.valueOf(island.size);
        }

        return null;
    }
}
