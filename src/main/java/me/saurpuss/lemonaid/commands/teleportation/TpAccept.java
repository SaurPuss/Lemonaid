package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpAccept implements CommandExecutor {

    private Lemonaid plugin = Lemonaid.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            // Player is accepting of request
            Player receiver = (Player) sender;
            Player player = null; // Key
            Player target; // Value
            HashSet<String> requests;

            // Check tpa or tpahere requests
            if ((!Teleportation.isPendingTarget(receiver)) && (!Teleportation.isPendingPlayer(receiver))) {
                receiver.sendMessage(Utils.chat("You have no pending teleportation requests."));
                return true;
            }
            // Set the two parties to player and target depending on which is the destination
            else {
                // receiver is the destination
                if (Teleportation.isPendingTarget(receiver)) {
                    // build the list of requests
                    requests = Teleportation.pendingRequests(receiver);
                    target = receiver;

                    if (requests.size() == 1) {
                        player = Teleportation.getSource(receiver);
                    } else if (requests.size() > 1) {
                        if (args.length < 1) {
                            StringBuilder list = new StringBuilder();
                            for (String p : requests) {
                                list.append("- " + p + "\n");
                            }
                            receiver.sendMessage(Utils.chat("You have pending teleportation requests from: "
                                    + list.toString()) + "\n Type /tpaccept <name> to accept.");
                            return true;
                        } else {
                            player = Bukkit.getPlayer(args[0]);

                            // In case of a typo or offline player
                            if (player == null) {
                                receiver.sendMessage(Utils.chat(args[0] + " is not online right now."));
                                return true;
                            }
                        }
                    }
                }
                // receiver is the origin
                else {
                    player = receiver;
                    target = Teleportation.getSource(receiver);
                }
            }

            // Now that we have a player and a target, do the teleportation dance
            Teleportation.teleportationEvent(player, target);
            return true;
        } else {
            return Teleportation.isConsole(sender);
        }
    }
}
