package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.util.Teleportation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDeny implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {



            return true;
        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
