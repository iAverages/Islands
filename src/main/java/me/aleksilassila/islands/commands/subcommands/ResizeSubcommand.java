package me.aleksilassila.islands.commands.subcommands;

import me.aleksilassila.islands.IslandsConfig;
import me.aleksilassila.islands.commands.AbstractIslandsWorldSubcommand;
import me.aleksilassila.islands.commands.Subcommand;
import me.aleksilassila.islands.utils.Messages;
import me.aleksilassila.islands.utils.Permissions;
import me.aleksilassila.islands.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ResizeSubcommand extends Subcommand {
    @Override
    public void onCommand(CommandSender commandSender, String[] args, boolean confirmed) {

        if (args.length < 1) {
            commandSender.sendMessage(Messages.get("usage.RESIZE"));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Messages.get("error.NO_PLAYER_FOUND"));
            return;
        }

        IslandsConfig.Entry island = Utils.getIslandFromBlock(player.getLocation().getBlock());
        if (island ==null) {
            Messages.send(player, "error.ISLAND_NOT_FOUND");
            commandSender.sendMessage(Messages.get("error.ISLAND_NOT_FOUND"));
            return;
        }

        try {
            island.resizeIsland(Integer.parseInt(args[1]));
            commandSender.sendMessage(Messages.get("success.RESIZE"));
        } catch (NumberFormatException ignored) {
            commandSender.sendMessage(Messages.get("error.NOT_A_NUMBER"));
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "resize";
    }

    @Override
    public String help() {
        return "Resize the current island without recreating it.";
    }

    @Override
    public String getPermission() {
        return Permissions.command.resize;
    }

    @Override
    public Boolean consoleOnly() {
        return true;
    }
}
