package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.util.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCancel implements CommandExecutor {


    /**
     * Cancel an outgoing tpa request
     * @param sender PLayer
     * @param command /tpacancel
     * @param label /tpacancel
     * @param args null
     * @return message to Player
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Teleportation.isPendingPlayer(player)) {
                Teleportation.remove(player);
                player.sendMessage(Utils.chat("Pending tpa request canceled."));
                return true;
            } else {
                player.sendMessage(Utils.chat("You have no pending tpa requests."));
                return true;
            }
        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
