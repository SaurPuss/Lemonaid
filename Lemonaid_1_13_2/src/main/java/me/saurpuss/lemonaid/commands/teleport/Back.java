package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Back implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Teleport.teleportEvent(new Teleport(player, null, TeleportType.BACK));
            return true;
        } else {
            // TODO this can just be Utils.console() and return true
            return Teleport.isConsole(sender);
        }
    }
}
