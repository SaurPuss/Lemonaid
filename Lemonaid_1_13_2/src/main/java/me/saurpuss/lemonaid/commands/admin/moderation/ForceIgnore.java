package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceIgnore implements CommandExecutor {

    private Lemonaid plugin;
    public ForceIgnore(Lemonaid plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Admin command to force an addition to a player's ignored list
        if (args.length == 2) {
            Player player = Utils.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + args[0]);
                return true;
            }

            Player target = Utils.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + args[1]);
                return true;
            }

            Lemon user = plugin.getUser(player.getUniqueId());
            user.toggleIgnore(target.getUniqueId());
            user.updateUser();

            // Notify moderators
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.notify")) {
                    p.sendMessage(ChatColor.YELLOW + sender.getName() + " forced " + player.getName() +
                            " to " + (user.isIgnored(target.getUniqueId()) ? ChatColor.RED + "ignore" :
                            ChatColor.GREEN + "unignore") + ChatColor.YELLOW + target.getName());
                }
            }

            plugin.getLogger().info(ChatColor.YELLOW + sender.getName() + " forced " + player.getName() +
                    " to " + (user.isIgnored(target.getUniqueId()) ? ChatColor.RED + "ignore" :
                    ChatColor.GREEN + "unignore") + ChatColor.YELLOW + target.getName());
            return true;
        }

        return false;
    }

}