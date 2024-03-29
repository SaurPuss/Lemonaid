package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Ignore implements CommandExecutor {

    private Lemonaid plugin;

    public Ignore(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Player wants to (un)ignore another player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = plugin.getUser(player.getUniqueId());

            // If moderators are not allowed to ignore players this happens
            if (player.hasPermission("lemonaid.ignoreexempt") &&
                    !plugin.getConfig().getBoolean("allow-moderator-ignore")) {
                player.sendMessage(ChatColor.RED + "Ignoring players when you're a moderator is bad form.");
                return true;
            }

            // Display ignored list
            if (args.length == 0) {
                if (user.getIgnored().size() == 0) {
                    player.sendMessage(ChatColor.GREEN + "There are currently no players on your ignored list!");
                } else {
                    player.sendMessage(ChatColor.RED + "Players you current have ignored:");
                    for (UUID uuid : user.getIgnored()) {
                        player.sendMessage("- " + Bukkit.getOfflinePlayer(uuid).getName());
                    }
                }
                return true;
            }

            // Try to retrieve a target player to ignore
            Player target = Utils.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + args[0] + " not found!");
                return true;
            }

            // Toggle ignore on retrieved player
            user.toggleIgnore(target.getUniqueId());
            user.updateUser();
            player.sendMessage(ChatColor.YELLOW + "You have " + (user.isIgnored(target.getUniqueId()) ?
                    ChatColor.RED + "ignored" : ChatColor.GREEN + "unignored") + ChatColor.YELLOW + target.getName());

            // Notify admins
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.notify")) {
                    p.sendMessage(ChatColor.YELLOW + player.getName() + (user.isIgnored(target.getUniqueId()) ?
                            ChatColor.RED + "ignored" : ChatColor.GREEN + "unignored") + ChatColor.YELLOW + target.getName());
                }
            }

            return true;
        }

        return false;
    }
}
