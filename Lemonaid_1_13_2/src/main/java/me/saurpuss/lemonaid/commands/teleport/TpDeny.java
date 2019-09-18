package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpDeny implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;
            HashSet<Teleport> incoming = Teleport.retrieveRequest(target);
            HashSet<Teleport> outgoing = Teleport.outgoingRequests(target);

            // There are both incoming and outgoing requests
            if ((!incoming.isEmpty()) && (!outgoing.isEmpty())) {
                if (args.length == 0) {
                    StringBuilder s = new StringBuilder();
                    // TODO add colors
                    s.append("\nYou have incoming teleport requests from: \n");
                    for (Teleport tp : incoming)
                        s.append("- " + tp.getClient().getName() + "\n");

                    s.append("You have outgoing teleport requests to: \n");
                    for (Teleport tp : outgoing)
                        s.append("- " + tp.getTarget().getName() + "\n");
                    s.append("Use /tpacancel <name>, or /tpacancel all to cancel requests.");

                    target.sendMessage(Utils.color(s.toString()));
                } else {
                    if (args[0].equalsIgnoreCase("all")) {
                        incoming.addAll(outgoing);
                        for (Teleport tp : incoming) {
                            Teleport.removeRequest(tp);
                        }

                        target.sendMessage(Utils.color("&5All pending teleport requests canceled!"));
                    } else {
                        // Check all arguments for incoming and outgoing requests
                        for (String arg : args) {
                            for (Teleport tp : incoming) {
                                if (arg.equalsIgnoreCase(tp.getClient().getName())) {
                                    Teleport.removeRequest(tp);
                                    incoming.remove(tp);
                                }
                            }
                            for (Teleport tp : outgoing) {
                                if (arg.equalsIgnoreCase(tp.getTarget().getName())) {
                                    Teleport.removeRequest(tp);
                                    outgoing.remove(tp);
                                }
                            }
                        }
                        target.sendMessage(Utils.color("&6Requests canceled."));
                    }
                }
            }
            // There are incoming requests
            else if (!incoming.isEmpty()) {
                if (incoming.size() == 1) {
                    for (Teleport tp : incoming) {
                        Teleport.removeRequest(tp);
                    }
                    target.sendMessage(Utils.color("&6Incoming teleport request canceled."));
                }
                // There are multiple incoming requests
                else {
                    if (args.length == 0) {
                        StringBuilder s = new StringBuilder();
                        // TODO add colors
                        s.append("You have incoming teleport requests from: \n");
                        for (Teleport tp : incoming)
                            s.append("- " + tp.getClient().getName() + "\n");
                        s.append("Use /tpdeny <name> to deny a request.");

                        target.sendMessage(Utils.color(s.toString()));
                    } else if (args[0].equalsIgnoreCase("all")) {
                        for (Teleport tp : incoming)
                            Teleport.removeRequest(tp);
                        target.sendMessage(Utils.color("Denied all incoming teleport requests"));
                    } else {
                        for (String arg : args)
                            for (Teleport tp : incoming)
                                if (arg.equalsIgnoreCase(tp.getClient().getDisplayName())) {
                                    Teleport.removeRequest(tp);
                                    incoming.remove(tp);
                                }
                        target.sendMessage(Utils.color("Denied all valid teleport requests."));
                    }
                }
            }
            // There are outgoing requests
            else if (!outgoing.isEmpty()) {
                if (outgoing.size() == 1) {
                    for (Teleport tp : outgoing) {
                        Teleport.removeRequest(tp);
                    }
                    target.sendMessage(Utils.color("&6Your pending teleport request has been canceled."));
                } else {
                    if (args.length == 0) {
                        StringBuilder s = new StringBuilder();
                        // TODO add color
                        s.append("You have outgoing teleport requests to: \n");
                        for (Teleport tp : outgoing)
                            s.append("- " + tp.getTarget().getName() + "\n");
                        s.append("Use /tpacancel <name>, or /tpacancel all to cancel a request.");

                        target.sendMessage(Utils.color(s.toString()));
                    } else if (args[0].equalsIgnoreCase("all")) {
                        for (Teleport tp : outgoing)
                            Teleport.removeRequest(tp);
                    } else {
                        // deny all valid arguments
                        for (String arg : args)
                            for (Teleport tp : outgoing)
                                if (arg.equalsIgnoreCase(tp.getTarget().getDisplayName())) {
                                    Teleport.removeRequest(tp);
                                    outgoing.remove(tp);
                                }
                    }
                }
            }
            // Incoming and outgoing are both empty
            else
                target.sendMessage(Utils.color("&cYou have no pending teleport requests."));
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
