package me.aleksilassila.islands.commands.subcommands;

import me.aleksilassila.islands.commands.Subcommand;
import me.aleksilassila.islands.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ConfirmSubcommand extends Subcommand {
    @Override
    public void onCommand(CommandSender commandSender, String[] args, boolean confirmed) {
        Player player = (Player) commandSender;

    }

    @Override
    public List<String> onTabComplete(Player player, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "confirm";
    }

    @Override
    public String help() {
        return "Confirm dangerous command";
    }

    @Override
    public String getPermission() {
        return Permissions.command.confirm;
    }

    @Override
    public Boolean consoleOnly() {
        return false;
    }
}
