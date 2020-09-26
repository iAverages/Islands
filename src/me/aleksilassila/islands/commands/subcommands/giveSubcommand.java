package me.aleksilassila.islands.commands.subcommands;

import me.aleksilassila.islands.Main;
import me.aleksilassila.islands.Permissions;
import me.aleksilassila.islands.commands.Subcommand;
import me.aleksilassila.islands.generation.IslandGrid;
import me.aleksilassila.islands.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class giveSubcommand extends Subcommand {
    private Main plugin;
    private IslandGrid grid;

    public giveSubcommand(Main plugin) {
        this.plugin = plugin;
        this.grid = plugin.islands.grid;
    }

    @Override
    public void onCommand(Player player, String[] args, boolean confirmed) {
        if (!Permissions.checkPermission(player, Permissions.island.give)) {
            player.sendMessage(Messages.error.NO_PERMISSION);
            return;
        }

        if (!player.getWorld().equals(plugin.islandsWorld)) {
            player.sendMessage(Messages.error.WRONG_WORLD);
            return;
        }


        if (args.length != 1) {
            player.sendMessage(Messages.help.GIVE);
            return;
        }

        String islandId = grid.getIslandId(player.getLocation().getBlockX(), player.getLocation().getBlockZ());

        if (islandId == null) {
            player.sendMessage(Messages.error.UNAUTHORIZED);
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            player.sendMessage(Messages.error.NO_PLAYER_FOUND);
            return;
        }

        if (plugin.getIslandsConfig().getString("islands." + islandId + ".UUID").equals(player.getUniqueId().toString())
                || Permissions.checkPermission(player, Permissions.bypass.give)) {
            if (plugin.getIslandsConfig().getInt("islands." + islandId + ".public") == 1) {
                if (!confirmed) {
                    player.sendMessage(Messages.info.CONFIRM);
                    return;
                }

                grid.giveIsland(islandId, targetPlayer);

                player.sendMessage(Messages.success.OWNER_CHANGED(args[0]));
                targetPlayer.sendMessage(Messages.success.ISLAND_RECEIVED(targetPlayer.getName(), args[0]));
            } else {
                player.sendMessage(Messages.error.NOT_PUBLIC);
            }
        } else {
            player.sendMessage(Messages.error.UNAUTHORIZED);
        }

    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String help() {
        return Messages.help.GIVE;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}
