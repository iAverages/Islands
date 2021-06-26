package me.aleksilassila.islands.commands.subcommands;

import me.aleksilassila.islands.Islands;
import me.aleksilassila.islands.IslandsConfig;
import me.aleksilassila.islands.commands.Subcommand;
import me.aleksilassila.islands.utils.Messages;
import me.aleksilassila.islands.utils.Permissions;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleWaterSubcommand extends Subcommand {
    @Override
    public void onCommand(Player player, String[] args, boolean confirmed) {
        IslandsConfig.Entry island = getIsland(player);
        if (island == null) return;
        island.setAllowWaterFlow(!island.allowWaterFlow);
        String messageKey = island.allowWaterFlow ? "success.TOGGLED_WATER_ENABLED" : "success.TOGGLED_WATER_DISABLED";
        Messages.send(player, messageKey);
    }

    private IslandsConfig.Entry getIsland(Player player) {
        if (!player.getWorld().equals(Islands.islandsWorld)) {
            player.sendMessage(Messages.get("error.WRONG_WORLD"));
            return null;
        }

        IslandsConfig.Entry island = IslandsConfig.getEntry(player.getLocation().getBlockX(), player.getLocation().getBlockZ(), true);

        if (island == null) {
            player.sendMessage(Messages.get("error.NOT_ON_ISLAND"));
            return null;
        } else if (!player.getUniqueId().equals(island.uuid)
                && !player.hasPermission(Permissions.bypass.recreate)) {
            player.sendMessage(Messages.get("error.UNAUTHORIZED"));
            return null;
        }

        return island;
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "togglewater";
    }

    @Override
    public String help() {
        return "Toggles water flow below islands.";
    }

    @Override
    public String getPermission() {
        return Permissions.command.toggleWater;
    }
}
