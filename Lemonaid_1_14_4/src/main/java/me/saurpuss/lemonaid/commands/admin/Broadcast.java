package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Broadcast implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Sender is not a player or a player with admin perms
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.broadcast"))) {
            if (args.length == 0) {
                sender.sendMessage(Utils.color("&cUsage: /bc <message>"));
                return true;
            }

            String message = StringUtils.join(args, ' ', 0, args.length);
            // TODO confirmation step

            // Broadcast the message to all players and the console
            Bukkit.broadcastMessage(Utils.color("&c[&eBROADCAST&c]&b " + message));
            return true;
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }
}
