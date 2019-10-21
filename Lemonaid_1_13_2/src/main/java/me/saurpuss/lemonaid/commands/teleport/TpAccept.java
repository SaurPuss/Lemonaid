package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.tp.PlayerTeleport;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpAccept implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;
            // Check if there are incoming requests
            HashSet<PlayerTeleport> incoming = PlayerTeleport.retrieveRequest(target);
            if (incoming.isEmpty()) {
                target.sendMessage("§cYou have no pending teleport requests.");
                return true;
            } else if (incoming.size() == 1) {
                for (PlayerTeleport tp : incoming) {
                    PlayerTeleport.teleportEvent(tp);
                }
                return true;
            }
            // There are multiple incoming requests
            else {
                if (args.length == 0) {
                    StringBuilder s = new StringBuilder();
                    s.append("§6You have incoming requests from: \n");
                    for (PlayerTeleport tp : incoming) {
                        s.append("§5- " + tp.getClient().getName() + "\n");
                    }
                    // TODO make the command bits a different color
                    s.append("§6Use §d/tpaccept §5<5dname§5>§6, or §d/tpaccept all §6to accept a request.");

                    target.sendMessage(s.toString());
                    return true;
                }

                // accept all incoming requests
                if (args[0].equalsIgnoreCase("all")) {
                    for (PlayerTeleport tp : incoming) {
                        PlayerTeleport.teleportEvent(tp);
                    }
                    return true;
                }

                // accept all valid arguments
                for (PlayerTeleport tp : incoming) {
                    for (String arg : args) {
                        if (arg.equalsIgnoreCase(tp.getClient().getName())) {
                            PlayerTeleport.teleportEvent(tp);
                            incoming.remove(tp);
                        }
                    }
                }
                // Confirmation if all were successful
                if (incoming.isEmpty()) {
                    target.sendMessage("§6Accepted all incoming teleport requests.");
                }
                return true;
            }
        } else {
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
