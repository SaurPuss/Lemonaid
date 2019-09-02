package me.saurpuss.lemonaid.commands.console;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MasterCuff implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // Fallback option to quick create the cufflist from scratch
            // List all unique players in the previous cufflist
            // Use Bukkit Conversation API


            // This only works if there is no file to pull from? put console warning at startup

            // /mastercuff <name> <name> <name> <name>
            // get from offline players

            // convo option to list all players in the cufflog

        }

        return true;
    }
}
