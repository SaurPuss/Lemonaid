package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.Teleport;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpAccept implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;
            // Check if there are incoming requests
            HashSet<Teleport> incoming = Teleport.retrieveRequest(target);
            if (incoming.isEmpty()) {
                target.sendMessage(Utils.color("&cYou have no pending teleport requests."));
                return true;
            } else if (incoming.size() == 1) {
                for (Teleport tp : incoming) {
                    Teleport.teleportEvent(tp);
                }
                return true;
            }
            // There are multiple incoming requests
            else {
                if (args.length == 0) {
                    StringBuilder s = new StringBuilder();
                    s.append("&6You have incoming requests from: \n");
                    for (Teleport tp : incoming) {
                        s.append("&5- " + tp.getClient().getName() + "\n");
                    }
                    // TODO make the command bits a different color
                    s.append("&6Use /tpaccept <name>, or /tpaccept all to accept a request.");

                    target.sendMessage(Utils.color(s.toString()));
                } else {
                    if (args[0].equalsIgnoreCase("all")) {
                        for (Teleport tp : incoming) {
                            Teleport.teleportEvent(tp);
                        }
                        return true;
                    }
                    // accept all valid arguments
                    for (Teleport tp : incoming) {
                        for (String arg : args) {
                            if (arg.equalsIgnoreCase(tp.getClient().getName())) {
                                Teleport.teleportEvent(tp);
                                incoming.remove(tp);
                            }
                        }
                    }
                    // Confirmation if all were successful
                    if (incoming.isEmpty()) {
                        target.sendMessage(Utils.color("Accepted all incoming teleport requests."));
                    } // TODO list the pending requests?
                }
                return true;
            }
        } else {
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
