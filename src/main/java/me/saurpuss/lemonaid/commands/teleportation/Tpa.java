package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Teleportation;
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
                player.sendMessage(Utils.chat("Type: /tpa <name> to request a teleport."));
                return true;
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Utils.chat(args[0] + " is not online."));
                    return true;
                }
                // TODO deny multiple uses

                // Send involved players a message about the tpa request
                target.sendMessage(Utils.chat(player.getDisplayName() + " has requested to teleport to you."));
                target.sendMessage(Utils.chat("Type: /tpaccept or /tpdeny"));
                player.sendMessage(Utils.chat(target.getDisplayName() + " has 30 seconds to accept your teleport request."));

                // Put request in the tpaRequests HashMap
                Teleportation.request(player, target);

                // Schedule a task to check for tpaccept
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                    if (Teleportation.isPendingPlayer(player)) {
                        player.sendMessage(Utils.chat("Teleportation request to " + target.getDisplayName() + " expired."));
                        Teleportation.remove(player);
                    }
                }, 600l);

                return true;
            }
        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
