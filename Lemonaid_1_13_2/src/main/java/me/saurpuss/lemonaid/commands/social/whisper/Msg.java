package me.saurpuss.lemonaid.commands.social.whisper;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg implements CommandExecutor {

    Lemonaid plugin = Lemonaid.getInstance();

    /**
     * Send a private message to another player.
     * Logged in the console using:
     *      [Lemonaid][MSG] {sender >> receiver} message
     * @param sender Command Sender
     * @param command /mgs, /tell, /whisper
     * @param label msg, tell, whisper
     * @param args target message..
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // The sender is another player
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length <= 1) {
                player.sendMessage(Utils.chat("Type: /" + label + " <player> <message>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Utils.chat(args[0] + " is not online."));
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (int i = 1; i <args.length; i++) {
                message.append(args[i] + " ");
            }

            target.sendMessage(Utils.dm("&6" + player.getName() + "&c >> &r" + message.toString()));
            player.sendMessage(Utils.dm("&6me &c>>&r " + message.toString()));

            // Log a human readable copy in the console
            // TODO social spy copy for admins
            plugin.getLogger().info(Utils.console("[MSG] {" + player.getName() + " >> " +
                    target.getName() + "} " + message.toString()));
            return true;
        }
        // The sender is the console
        else {
            if (args.length <= 1) {
                sender.sendMessage(Utils.console("Type: /" + label + " <player> <message>"));
                return true;
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(Utils.console(args[0] + " is not online."));
                    return true;
                }

                StringBuilder message = new StringBuilder();
                for (int i = 1; i <args.length; i++) {
                    message.append(args[i] + " ");
                }

                target.sendMessage(Utils.dm("&6" + sender.getName() + "&c >> &r" + message.toString()));
                sender.sendMessage(Utils.dm("[MSG] >> " + message.toString()));
                return true;
            }
        }
    }
}
