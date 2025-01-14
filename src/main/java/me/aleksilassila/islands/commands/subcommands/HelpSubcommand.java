package me.aleksilassila.islands.commands.subcommands;

import me.aleksilassila.islands.commands.IslandCommands;
import me.aleksilassila.islands.commands.Subcommand;
import me.aleksilassila.islands.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpSubcommand extends Subcommand {
    IslandCommands commands;

    public HelpSubcommand(IslandCommands commands) {
        this.commands = commands;
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] args, boolean confirmed) {
        Player player = (Player) commandSender;
        Messages.send(player, "info.AVAILABLE_SUBCOMMANDS");

        for (Subcommand subcommand : commands.subcommands) {
            if (subcommand.getPermission() == null || player.hasPermission(subcommand.getPermission()))
                Messages.send(player, "info.AVAILABLE_SUBCOMMAND", subcommand.getName(), subcommand.help());
        }
    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String help() {
        return "Shows help about /island subcommands";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public Boolean consoleOnly() {
        return false;
    }
}
