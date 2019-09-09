package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.util.Utils;
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
            Player target = (Player) sender;
            // Check if there are incoming requests
            HashSet<Teleport> incoming = Teleport.retrieveRequest(target);

            if (incoming.isEmpty()) {
                target.sendMessage(Utils.color("&cYou have no pending teleport requests."));
                return true;
            }

            // there is 1 request: activate tp, ignore args
            if (incoming.size() == 1) {
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
                } if (args[0].equalsIgnoreCase("all")) {
                    // accept all
                    for (Teleport tp : incoming) {
                        Teleport.teleportEvent(tp);
                    }
                    return true;
                } else {
                    // accept all valid arguments
                    for (String arg : args) {
                        for (Teleport tp : incoming) {
                            if (arg.equalsIgnoreCase(tp.getClient().getDisplayName())) {
                                Teleport.teleportEvent(tp);
                                incoming.remove(tp);
                            } else {
                                target.sendMessage(Utils.color("&cCan't find " + arg + "."));
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
