package me.saurpuss.lemonaid.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Kit implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("lemonaid.kits.admin")) {
            // TODO access to all kit files



        }


        return false;
    }
}
