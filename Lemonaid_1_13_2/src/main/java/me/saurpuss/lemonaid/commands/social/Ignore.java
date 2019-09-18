package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class Ignore implements CommandExecutor {

    private Lemonaid plugin;
    public Ignore(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender.hasPermission("lemonaid.admin.setignore")) && (args.length == 2)) {
            // Admin command to force an addition to a player's ignored list
            Player player = Utils.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(Utils.color("&cCan't find " + args[0]));
                return true;
            }

            Player target = Utils.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Utils.color("&cCan't find " + args[1]));
                return true;
            }

            Lemon user = plugin.getUser(player.getUniqueId());
            boolean ignore = user.isIgnored(target.getUniqueId());
            user.setIgnore(target.getUniqueId());
            user.updateUser();

            // Notify admins
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.admin.notify.ignore")) {
                    p.sendMessage(Utils.color(sender.getName() + " forced " + args[0] +
                            " to " + (ignore ? "ignore " : "unignore ") + args[1]));
                }
            }
            return true;
        } else {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    sender.sendMessage(Utils.color("Type: /ignore <name> to toggle a player's ignored status"));
                }
                Player player = (Player) sender;
                Player target = Utils.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Utils.color("&cCan't find " + args[0]));
                    return true;
                }

                Lemon user = plugin.getUser(player.getUniqueId());
                boolean ignore = user.isIgnored(target.getUniqueId());
                user.setIgnore(target.getUniqueId());
                user.updateUser();

                player.sendMessage(Utils.color("You have " + (ignore ? "ignored " : "unignored ") + args[0]));

                // Notify admins
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.ignore")) {
                        p.sendMessage(player.getName() + (ignore ? "ignored " : "unignored ") + args[0]);
                    }
                }
            } else {
                sender.sendMessage("Usage: /ignore <player> <player>");
            }
            return true;
        }
    }
}
