package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpDeny implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;
            // Check if there are incoming requests
            HashSet<Teleport> incoming = Teleport.retrieveRequest(target);

            if (incoming.isEmpty()) {
                target.sendMessage(Utils.tpa("You have no pending teleport requests."));
                return true;
            }

            // there is 1 request: activate tp, ignore args
            if (incoming.size() == 1) {
                for (Teleport tp : incoming) {
                    Teleport.removeRequest(tp);
                }
                return true;
            }
            // There are multiple incoming requests
            else {
                if (args.length == 0) {
                    StringBuilder s = new StringBuilder();
                    s.append("You have incoming requests from: \n");

                    for (Teleport tp : incoming) {
                        s.append("- " + tp.getClient().getName() + "\n");
                    }

                    s.append("Use /tpdeny <name> to deny a request.");

                    target.sendMessage(Utils.tpa(s.toString()));
                } if (args[0].equalsIgnoreCase("all")) {
                    // deny all
                    for (Teleport tp : incoming) {
                        Teleport.removeRequest(tp);
                    }
                    return true;
                } else {
                    // deny all valid arguments
                    for (String arg: args) {
                        for (Teleport tp : incoming) {
                            if (arg.equalsIgnoreCase(tp.getClient().getDisplayName())) {
                                Teleport.removeRequest(tp);
                                incoming.remove(tp);
                            } else {
                                target.sendMessage(Utils.tpa("Can't find " + arg + "."));
                            }
                        }
                    }
                    return true;
                }
            }
        } else {
            return Teleport.isConsole(sender);
        }
    }
}
