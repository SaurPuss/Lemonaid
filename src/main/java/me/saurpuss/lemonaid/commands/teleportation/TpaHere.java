package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaHere implements CommandExecutor {
    Lemonaid plugin = Lemonaid.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;

            if (args.length == 0) {
                target.sendMessage(Utils.tpa("Type: /tpahere <name> to request a teleport."));
                return true;
            } else {
                // Get the first argument for a player, ignore all other arguments
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    player.sendMessage(Utils.tpa(args[0] + " not found."));
                    return true;
                }

                if (Teleportation.isPendingPlayer(player)) {
                    target.sendMessage(Utils.tpa(player.getName() + " already has a pending teleportation request pending."));
                    return true;
                }

                // Send involved players a message about the tpa request
                player.sendMessage(Utils.tpa(target.getDisplayName() + " has requested you teleport to them." +
                        "Type: /tpaccept or /tpdeny"));
                target.sendMessage(Utils.tpa(player.getDisplayName() + " has 30 seconds to accept your teleport request."));

                // Put request in the tpaRequests HashMap
                Teleportation.request(player, target);

                // Schedule a 30 second window for teleportation request acceptance
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (Teleportation.isPendingPlayer(player)) {
                        Teleportation.remove(player);
                        // TODO refund tpa request in here and always withdraw when the command is used?
                        target.sendMessage(Utils.tpa("TpaHere request to " + player.getDisplayName() + " expired."));
                    }
                }, 600l);
                return true;
            }
        } else {
            return Teleportation.isConsole(sender);
        }

    }
}