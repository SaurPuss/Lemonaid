package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpAccept implements CommandExecutor {

    private Lemonaid plugin = Lemonaid.getInstance();
    private int timer = 0;
    private int scheduler = 1;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;
            HashSet<String> requests = Teleportation.pendingRequests(target);
            Player player;

            // Check if there are mutltiple teleportation requests
            if (requests.size() == 0) {
                // No requests
                target.sendMessage(Utils.chat("You have no pending teleportation requests."));
                return true;
            } else if (requests.size() > 1) {
                StringBuilder list = new StringBuilder();

                for (String p : requests) {
                    list.append("- " + p + "\n");
                }

                if (args.length != 1) {
                    target.sendMessage(Utils.chat("You have pending teleportation requests from: "
                            + list.toString()) + "\n Type /tpaccept <name> to accept.");
                    return true;
                } else {
                    player = Bukkit.getPlayer(args[0]);

                    if (player == null) {
                        target.sendMessage(Utils.chat(args[0] + " is not a valid player."));
                        return true;
                    } else if (Teleportation.isPendingPlayer(player)) {

                    } else {

                    }
                }
            } else {
                player = Teleportation.getSource(target);
            }

            Location origin = player.getLocation();


            // TODO replace this below
            player.sendMessage(Utils.chat("Activating teleport, please do not move!"));
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (timer == 5) {
                        Teleportation.updateRequests(player, target, true);
                        Bukkit.getServer().getScheduler().cancelTask(scheduler);
                    } else {
                        if (origin == player.getLocation())
                            timer++;
                        else {
                            player.sendMessage(Utils.chat("Teleportation request cancelled"));
                            Bukkit.getServer().getScheduler().cancelTask(scheduler);
                        }
                    }
                }
            }, 0, 20);

            return true;

        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
