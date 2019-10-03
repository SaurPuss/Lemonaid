package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.Teleport;
import me.saurpuss.lemonaid.utils.TeleportType;
import me.saurpuss.lemonaid.utils.Utils;
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
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
