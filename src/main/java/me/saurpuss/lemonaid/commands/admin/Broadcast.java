package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Broadcast implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Sender is a player
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("lemonaid.admin.broadcast")) {
                if (args.length == 0) {
                    player.sendMessage(Utils.chat("Type: /bc <message>"));
                    return true;
                }

                StringBuilder message = new StringBuilder();
                for (String s : args) {
                    message.append(s + " ");
                }

                // TODO confirmation step

                // Broadcast the message to all players and the console
                Bukkit.broadcastMessage(Utils.broadcast(message.toString()));
                return true;

            } else {
                player.sendMessage(Utils.error());
                return true;
            }
        }
        // Sender is the console
        else {
            if (args.length == 0) {
                sender.sendMessage(Utils.chat("Type: /bc <message>"));
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (String s : args) {
                message.append(s + " ");
            }

            // TODO confirmation step

            // Broadcast the message to all players and the console
            Bukkit.broadcastMessage(Utils.broadcast(message.toString()));
            return true;
        }
    }
}
