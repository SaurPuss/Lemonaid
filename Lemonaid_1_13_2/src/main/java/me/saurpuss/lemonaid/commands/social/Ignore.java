package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
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
            Lemon user = plugin.getUser(player.getUniqueId());
            // Display ignored list
            if (args.length == 0) {
                if (user.getIgnored().size() == 0) {
                    player.sendMessage("§aThere are currently no players on your ignored list!");
                } else {
                    player.sendMessage("§cPlayers you current have ignored:");
                    for (UUID uuid : user.getIgnored()) {
                        player.sendMessage("- " + Bukkit.getOfflinePlayer(uuid).getName());
                    }
                }
                return true;
            }

            // Try to retrieve a target player to ignore
            Player target = Utils.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cCan't find " + args[0]);
                return true;
            }

            // Toggle ignore on retrieved player
            user.toggleIgnore(target.getUniqueId());
            user.updateUser();
            player.sendMessage("§eYou have " + (user.isIgnored(target.getUniqueId()) ?
                    "§cignored" : "§aunignored") + "§e " + target.getName());

            // Notify admins
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.notify")) {
                    p.sendMessage("§e" + player.getName() + (user.isIgnored(target.getUniqueId()) ?
                            "§cignored" : "§unignored") + "§e " + target.getName());
                }
            }

            return true;
        } else {
            // The console can only force real players to ignore each other
            sender.sendMessage("Usage: §3/ignore <player1> <player2>");
            return true;
        }
    }
}
