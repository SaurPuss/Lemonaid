package me.saurpuss.lemonaid.commands.lemonaid;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // TODO reload configs and stuff
//        Lemonaid.updateLists();
        return false;
    }
}
