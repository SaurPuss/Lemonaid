package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;

public class TpDeny implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player target = (Player) sender;
            // Check if there are incoming requests
            HashSet<Teleport> incoming = Teleport.retrieveRequest(target);
            HashSet<Teleport> outgoing = Teleport.outgoingRequests(target);

            // There are both incoming and outgoing requests
            if ((!incoming.isEmpty()) && (!outgoing.isEmpty())) {
                if (args.length == 0) {
                    StringBuilder s = new StringBuilder();
                    s.append("\nYou have incoming teleport requests from: \n");
                    for (Teleport tp : incoming)
                        s.append("- " + tp.getClient().getName() + "\n");
                    s.append("You have outgoing teleport requests to: \n");
                    for (Teleport tp : outgoing)
                        s.append("- " + tp.getTarget().getName() + "\n");
                    s.append("Use /tpacancel <name>, or /tpacancel all to cancel a request.");

                    target.sendMessage(Utils.tpa(s.toString()));
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("all")) {
                        incoming.addAll(outgoing);
                        for (Teleport tp : incoming) {
                            Teleport.removeRequest(tp);
                        }

                        target.sendMessage(Utils.tpa("All pending teleport requests canceled."));
                        return true;
                    } else {
                        // Check all arguments for incoming and outgoing requests
                        ArrayList<String> invalid = new ArrayList<>();
                        for (String arg : args) {
                            boolean found = true;
                            for (Teleport tp : incoming) {
                                if (arg.equalsIgnoreCase(tp.getClient().getDisplayName())) {
                                    Teleport.removeRequest(tp);
                                    incoming.remove(tp);
                                } else {
                                    found = false;
                                }
                            }
                            for (Teleport tp : outgoing) {
                                if (arg.equalsIgnoreCase(tp.getTarget().getDisplayName())) {
                                    Teleport.removeRequest(tp);
                                    outgoing.remove(tp);
                                } else {
                                    found = false;
                                }
                            }

                            if (!found)
                                invalid.add(arg);
                        }

                        if (invalid.size() == 1) {
                            target.sendMessage(invalid.get(0) + " could not be found");
                        } else if (invalid.size() > 1) {
                            StringBuilder message = new StringBuilder();
                            message.append("Unable to cancel: \n");
                            for (String s : invalid) {
                                message.append("- " + s + "\n");
                            }

                            target.sendMessage(Utils.tpa(message.toString()));
                        } else {
                            target.sendMessage(Utils.tpa("Requests canceled."));
                        }
                        return true;
                    }
                }

            }
            // There are incoming requests
            else if (!incoming.isEmpty()) {
                // there is 1 request: activate tp, ignore args
                if (incoming.size() == 1) {
                    for (Teleport tp : incoming) {
                        Teleport.removeRequest(tp);
                    }
                    target.sendMessage(Utils.tpa("Incoming teleport request canceled."));
                    return true;
                }
                // There are multiple incoming requests
                else {
                    if (args.length == 0) {
                        StringBuilder s = new StringBuilder();
                        s.append("You have incoming teleport requests from: \n");

                        for (Teleport tp : incoming) {
                            s.append("- " + tp.getClient().getName() + "\n");
                        }

                        s.append("Use /tpdeny <name> to deny a request.");

                        target.sendMessage(Utils.tpa(s.toString()));
                    } else if (args[0].equalsIgnoreCase("all")) {
                        // deny all
                        for (Teleport tp : incoming) {
                            Teleport.removeRequest(tp);
                        }
                    } else {
                        // deny all valid arguments
                        for (String arg : args) {
                            for (Teleport tp : incoming) {
                                if (arg.equalsIgnoreCase(tp.getClient().getDisplayName())) {
                                    Teleport.removeRequest(tp);
                                    incoming.remove(tp);
                                } else {
                                    target.sendMessage(Utils.tpa("Can't find " + arg + "."));
                                }
                            }
                        }
                    }
                    return true;
                }
            }
            // There are outgoing requests
            else if (!outgoing.isEmpty()) {
                if (outgoing.size() == 1) {
                    for (Teleport tp : outgoing) {
                        Teleport.removeRequest(tp);
                    }
                    target.sendMessage(Utils.tpa("Your pending teleport request has been canceled."));
                    return true;
                } else {
                    if (args.length == 0) {
                        StringBuilder s = new StringBuilder();
                        s.append("You have outgoing teleport requests to: \n");

                        for (Teleport tp : outgoing) {
                            s.append("- " + tp.getTarget().getName() + "\n");
                        }

                        s.append("Use /tpacancel <name>, or /tpacancel all to cancel a request.");

                        target.sendMessage(Utils.tpa(s.toString()));
                    } else if (args[0].equalsIgnoreCase("all")) {
                        // deny all
                        for (Teleport tp : outgoing) {
                            Teleport.removeRequest(tp);
                        }
                    } else {
                        // deny all valid arguments
                        for (String arg : args) {
                            for (Teleport tp : outgoing) {
                                if (arg.equalsIgnoreCase(tp.getTarget().getDisplayName())) {
                                    Teleport.removeRequest(tp);
                                    outgoing.remove(tp);
                                } else {
                                    target.sendMessage(Utils.tpa("Can't find " + arg + "."));
                                }
                            }
                        }
                    }
                    return true;
                }
            }
            // Incoming and outgoing are both empty
            else {
                target.sendMessage(Utils.tpa("You have no pending teleport requests."));
                return true;
            }
        }
        else {
            return Teleport.isConsole(sender);
        }
    }
}
