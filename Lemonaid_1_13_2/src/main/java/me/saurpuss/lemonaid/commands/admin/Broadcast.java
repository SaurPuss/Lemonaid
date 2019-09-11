package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

/**
 * Bring the message of the gods to all who can hear
 */
public class Broadcast implements CommandExecutor {

    /**
     * Broadcast a message to the entire server
     * @param sender Executor of the broadcast command
     * @param command Command sent
     * @param label Label of the command sent
     * @param args Message to be broadcast to all online players and the console
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.broadcast"))) {
            if (args.length == 0) {
                sender.sendMessage(Utils.color("&cUsage: /bc <message>"));
                return true;
            }

            String message = StringUtils.join(args, ' ', 0, args.length);
            // TODO confirmation step

            // Broadcast the message to all players and the console
            Bukkit.broadcastMessage(Utils.color("&c[&eBROADCAST&c]&b " + message));
        } else {
            sender.sendMessage(Utils.noPermission());
        }
        return true;
    }
}
