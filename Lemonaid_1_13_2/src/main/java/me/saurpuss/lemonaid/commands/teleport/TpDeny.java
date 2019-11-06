package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpDeny implements CommandExecutor {

    private Lemonaid plugin;

    public TpDeny(Lemonaid plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.playerOnly());
            return true;
        }

        // TODO refactor & rework
        Player target = (Player) sender;
        HashSet<Teleport> incoming = plugin.getTeleportManager().retrieveRequest(target);
        HashSet<Teleport> outgoing = plugin.getTeleportManager().outgoingRequests(target);

        // There are both incoming and outgoing requests
        if (!incoming.isEmpty() && !outgoing.isEmpty()) {
            // display available requests
            if (args.length == 0) {
                StringBuilder s = new StringBuilder();
                s.append("§6You have incoming teleport requests from: \n");
                for (Teleport tp : incoming)
                    s.append("- " + tp.getClient().getName() + "\n");

                s.append("§6You have outgoing teleport requests to: \n");
                for (Teleport tp : outgoing)
                    s.append("- " + tp.getTarget().getName() + "\n");
                s.append("§6Use §d/tpacancel §5<§dname§5>§6, or §d/tpacancel all §6to cancel requests.");

                target.sendMessage(s.toString());
                return true;
            }

            // deny all requests
            if (args[0].equalsIgnoreCase("all")) {
                incoming.addAll(outgoing);
                for (Teleport tp : incoming) {
                    plugin.getTeleportManager().removeRequest(tp);
                }

                target.sendMessage("§6All pending teleport requests canceled!");
                return true;
            }

            // Check all arguments for incoming and outgoing requests
            for (String arg : args) {
                for (Teleport tp : incoming) {
                    if (arg.equalsIgnoreCase(tp.getClient().getName())) {
                        plugin.getTeleportManager().removeRequest(tp);
                        incoming.remove(tp);
                    }
                }
                for (Teleport tp : outgoing) {
                    if (arg.equalsIgnoreCase(tp.getTarget().getName())) {
                        plugin.getTeleportManager().removeRequest(tp);
                        outgoing.remove(tp);
                    }
                }
            }

            target.sendMessage("§6Requests canceled.");
            return true;
        }

        // There are only incoming requests
        if (!incoming.isEmpty()) {
            // There is only 1 incoming request
            if (incoming.size() == 1) {
                for (Teleport tp : incoming) {
                    plugin.getTeleportManager().removeRequest(tp);
                }
                target.sendMessage("§6Incoming teleport request canceled.");
                return true;
            }

            // There are multiple incoming requests
            if (args.length == 0) {
                StringBuilder s = new StringBuilder();
                s.append("§6You have incoming teleport requests from: \n");
                for (Teleport tp : incoming)
                    s.append("- " + tp.getClient().getName() + "\n");
                s.append("§6Use §d/tpdeny §5<§dname§5> §6to deny a request.");

                target.sendMessage(s.toString());
                return true;
            }
            // Deny all incoming requests
            if (args[0].equalsIgnoreCase("all")) {
                for (Teleport tp : incoming)
                    plugin.getTeleportManager().removeRequest(tp);
                target.sendMessage("§6Denied all incoming teleport requests");
                return true;
            }

            // deny request matching args
            for (String arg : args)
                for (Teleport tp : incoming)
                    if (arg.equalsIgnoreCase(tp.getClient().getDisplayName())) {
                        plugin.getTeleportManager().removeRequest(tp);
                        incoming.remove(tp);
                    }
            target.sendMessage("§6Denied all valid teleport requests.");
            return true;
        }

        // There are only outgoing requests
        if (!outgoing.isEmpty()) {
            // There is only 1 outgoing request
            if (outgoing.size() == 1) {
                for (Teleport tp : outgoing) {
                    plugin.getTeleportManager().removeRequest(tp);
                }
                target.sendMessage("§6Your pending teleport request has been canceled.");
                return true;
            }
            // Display outgoing requests
            if (args.length == 0) {
                StringBuilder s = new StringBuilder();
                s.append("§6You have outgoing teleport requests to: \n");
                for (Teleport tp : outgoing)
                    s.append("- " + tp.getTarget().getName() + "\n");
                s.append("§6Use §d/tpacancel §5<§dname§5>§6, or §d/tpacancel all §6to cancel a request.");

                target.sendMessage(s.toString());
                return true;
            }

            // Cancel all outgoing requests
            if (args[0].equalsIgnoreCase("all")) {
                for (Teleport tp : outgoing)
                    plugin.getTeleportManager().removeRequest(tp);

                target.sendMessage("§6Your outgoing teleport requests have been canceled.");
                return true;
            }

            // deny all valid arguments
            for (String arg : args) {
                for (Teleport tp : outgoing) {
                    if (arg.equalsIgnoreCase(tp.getTarget().getDisplayName())) {
                        plugin.getTeleportManager().removeRequest(tp);
                        outgoing.remove(tp);
                    }
                }
            }
            target.sendMessage("§All valid outgoing teleport requests have been canceled.");
            return true;
        }

        // There are no pending requests
        target.sendMessage("§6You have no pending teleport requests.");
        return true;
    }
}
