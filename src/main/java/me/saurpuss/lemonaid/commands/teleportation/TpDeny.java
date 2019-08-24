package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.util.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDeny implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // TODO combine with TpaCancel
            if (Teleportation.isPendingPlayer(player)) {
                player.sendMessage(Utils.chat("Request canceled."));
                Teleportation.remove(player);

                return true;
            } else if (Teleportation.isPendingTarget(player)) {
                // TODO allow for specific denial, instead of all

                player.sendMessage(Utils.chat("Request canceled."));
                Teleportation.removeTarget(player);
                return true;
            } else {
                player.sendMessage(Utils.chat("You have no pending teleportation requests."));
                return true;
            }
        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
