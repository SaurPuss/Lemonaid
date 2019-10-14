package me.saurpuss.lemonaid.commands;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home implements CommandExecutor {

    private Lemonaid plugin;
    public Home(Lemonaid plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                // List homes for sender
                if (args[0].equalsIgnoreCase("list")) {
                    Lemon user = plugin.getUser(player.getUniqueId());
                    String homes = StringUtils.join(user.getHomes().keySet().toArray(), "§6, §e");
                    player.sendMessage(ChatColor.GOLD + "Available homes ("
                            + user.getHomes().size() + "/" + user.getMaxHomes() + "): §e" + homes);
                    return true;
                }

                // This is an admin checking home info for a player
                if (player.hasPermission("lemonaid.teleport.admin") && args[0].contains(":")) {
                    String[] subArg = args[0].split(":");
                    // /home list:player
                    if (subArg[0].equalsIgnoreCase("list")) {
                        Player target = Utils.getPlayer(subArg[1]);
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "Can't find " + subArg[1] + "!");
                            return true;
                        }

                        Lemon user = plugin.getUser(target.getUniqueId());
                        sender.sendMessage(ChatColor.GOLD + "Saved homes for " +
                                ChatColor.YELLOW + target.getName() + "§6:");
                        user.getHomes().forEach((home, loc) -> sender.sendMessage(ChatColor.GOLD +
                                "- §e" + home +  ChatColor.GOLD + " location: " + ChatColor.YELLOW +
                                loc.getWorld().toString() + " §6x: §e" + loc.getBlockX() +
                                " §6y:§e " + loc.getBlockY() + " §6z:§e " + loc.getBlockZ()));
                        return true;
                    }

                    // /home player:home
                    Player target = Utils.getPlayer(subArg[0]);
                    if (target == null) {
                        sender.sendMessage("Can't find " + args[0]);
                        return true;
                    }

                    Lemon user = plugin.getUser(target.getUniqueId());
                    Location location = user.getHome(subArg[1]);
                    if (location != null) {
                        player.teleport(location);
                        player.sendMessage(ChatColor.GREEN + "Successfully teleported to " +
                                target.getName() + "'s home: " + subArg[1]);
                    } else {
                        player.sendMessage(ChatColor.RED + "Can't find home " + subArg[1] + " for " +
                                "player " + target.getName());
                        player.sendMessage(ChatColor.RED + "Use " + ChatColor.DARK_PURPLE +
                                "/home list:§d<§5player§d>" + ChatColor.RED +
                                " to list available homes.");
                    }
                    return true;
                }
            }



            // /home arg[0] <homeName>
            if (args.length == 2) {
                Lemon user = plugin.getUser(player.getUniqueId());
                // Create a home if there is space
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("save")) {
                    short n = user.addHome(args[1], player.getLocation());
                    // check space
                    if (n == 0) {
                        player.sendMessage(ChatColor.RED + "You don't have any available home slots! " +
                                ChatColor.GOLD + "(§c" + user.getHomes().size() + "§6/§c" + user.getMaxHomes() + "§6)");
                    } else if (n == -1) {
                        player.sendMessage(ChatColor.RED + "Home " + args[1] + " already exists! Use " +
                                ChatColor.DARK_PURPLE + "/home update " + args[1] + ChatColor.RED +
                                " to set it to your current location!");
                    } else if (n == 1) { // redundant, oh well
                        player.sendMessage(ChatColor.GREEN + "Home " + args[1] + " saved!");
                    }
                }
                // update an existing home if the name matches
                else if (args[0].equalsIgnoreCase("update") ||
                        args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
                    // Check for matching home
                    boolean match = false;
                    for (String home : user.getHomes().keySet()) {
                        if (home.equalsIgnoreCase(args[1])) {
                            match = true;
                            break;
                        }
                    }

                    if (!match) {
                        player.sendMessage(ChatColor.RED + "Can't find a matching home for " + args[1]);
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("update")) {
                        user.updateHome(args[1], player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Home " + args[1] + " location updated!");
                    } else {
                        user.removeHome(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Home " + args[1] + " deleted!");
                    }

                    return true;
                }
            }

            return true;
        } else {
            if (args.length != 1) {
                sender.sendMessage("Only players can use this command, use /home <player> to see the homes of a player.");
                return true;
            } else {
                Player target = Utils.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage("Can't find " + args[0] + "!");
                    return true;
                }

                Lemon user = plugin.getUser(target.getUniqueId());
                sender.sendMessage(ChatColor.YELLOW + "Saved homes for " + target.getName() + ":");

                user.getHomes().forEach((home, location) -> sender.sendMessage(ChatColor.YELLOW +
                        "- " + home +  ChatColor.GOLD + " location: " + location.getWorld().toString() +
                        " x: " + location.getBlockX() + " y: " + location.getBlockY() + " z: " + location.getBlockZ()));
                return true;
            }
        }
    }


    // TODO delete home save to record in db manager class
}
