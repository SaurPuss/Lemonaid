package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tpa implements CommandExecutor {
    private Lemonaid plugin = Lemonaid.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(Utils.tpa("Type: /tpa <name> to request a teleport."));
                return true;
            } else {
                // Check if player has a pending tpa
                if (Teleportation.isPendingPlayer(player)) {
                    player.sendMessage(Utils.tpa("You have a tpa request pending. Type: /tpacancel remove it."));
                    return true;
                }

                // Get the first argument for a player, ignore all other arguments
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Utils.tpa(args[0] + " not found."));
                    return true;
                }

                // Send involved players a message about the tpa request
                target.sendMessage(Utils.tpa(player.getDisplayName() + " has requested to teleport to you. " +
                        "Type: /tpaccept or /tpdeny"));
                player.sendMessage(Utils.tpa(target.getDisplayName() + " has 30 seconds to accept your teleport request."));

                // Put request in the tpaRequests HashMap
                Teleportation.request(player, target);

                // Schedule a 30 second window for teleportation request acceptance
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (Teleportation.isPendingPlayer(player)) {
                        Teleportation.remove(player);
                        player.sendMessage(Utils.tpa("Teleportation request to " + target.getDisplayName() + " expired."));
                    }
                }, 600l);
                return true;
            }
        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
