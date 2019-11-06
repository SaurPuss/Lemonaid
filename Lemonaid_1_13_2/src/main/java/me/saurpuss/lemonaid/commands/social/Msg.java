package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg implements CommandExecutor {

    Lemonaid plugin;
    public Msg(Lemonaid plugin) {
        this.plugin = plugin;
    }

    /**
     * Send a private message to another player.
     * Logged in the console using:
     *      [MSG] {sender >> receiver} message
     * @param sender Command Sender
     * @param command /mgs, /tell, /whisper
     * @param label msg, tell, whisper
     * @param args target message...
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // The sender is another player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Not enough arguments
            if (args.length <= 1) return false;

            // If sender is set to busy they are not allowed to send a dm, unless mod
            // and only while mods are not allowed to
            User p = plugin.getUser(player.getUniqueId());
            if (p.isBusy() && (!player.hasPermission("lemonaid.exempt") ||
                    plugin.getConfig().getBoolean("allow-moderator-busy"))) {
                player.sendMessage(ChatColor.RED + "You can't send whispers while " +
                        ChatColor.DARK_PURPLE + "/busy" + ChatColor.RED + "!");
                return true;
            }

            // See if the intended target is online
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Can't find " + args[0] + "!");
                return true;
            }

            // Check if the user is set to busy
            User user = plugin.getUser(target.getUniqueId());
            if (user.isBusy()) {
                if (target.hasPermission("lemonaid.exempt") &&
                        !plugin.getConfig().getBoolean("allow-moderator-busy")) {
                    player.sendMessage(ChatColor.RED + args[0] + " is currently set to busy. " +
                            "They may not see your message!");
                } else {
                    player.sendMessage(ChatColor.RED + args[0] + " is currently unavailable.");
                    return true;
                }
            }

            // Compile and send the message
            String message = StringUtils.join(args, ' ', 1, args.length);
            target.sendMessage("§6[MSG]§c[§6" + player.getName() + "§c >> §6me§c]§f " +
                    (player.hasPermission("lemonaid.chat.color") ? Utils.color(message) : message));
            player.sendMessage("§6[MSG]§c[§6me §c >> §6" + target.getName() + "§c]§f " +
                    (player.hasPermission("lemonaid.chat.color") ? Utils.color(message) : message));
            user.setLastMessage(player.getUniqueId());
            user.updateUser();

            // Log a human readable copy in the console
            // TODO social spy copy for admins
            plugin.getLogger().info("[MSG] {" + player.getName() + " >> " + target.getName() + "} " + message);
            return true;
        }
        // The sender is the console
        else {
            if (args.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.DARK_PURPLE + "/msg <player> <message>");
                return true;
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Can't find " + args[0] + ".");
                    return true;
                }

                String message = StringUtils.join(args, ' ', 1, args.length);
                target.sendMessage("§e[MSG] >> §6 " + Utils.color(message));
                sender.sendMessage("[MSG] >> "+ target.getName() + ": " + message);
                return true;
            }
        }
    }
}
