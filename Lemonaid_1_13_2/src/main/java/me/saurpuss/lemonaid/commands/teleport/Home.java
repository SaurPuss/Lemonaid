package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home implements CommandExecutor {

    private Lemonaid plugin;

    public Home(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Lemon user = plugin.getUser(player.getUniqueId());
            Location location;
            if (args.length == 0) {
                // player only has a single home to teleport to
                if (user.homeCount() == 1) {
                    location = user.getFirstHome(); //homes.size() is 1
                    if (location != null) // fallback check
                        plugin.getTeleportManager().teleportEvent(
                                new Teleport(player, null, location, TeleportType.HOME));

                    return true;
                }
                // player error
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Multiple homes found! Use " +
                        ChatColor.DARK_PURPLE + "/homelist" + ChatColor.LIGHT_PURPLE +
                        " do display your options.");
                return false;
            }

            // This is an admin attempting to teleport to a specific player's home
            if (player.hasPermission("lemonaid.teleport.admin") && args[0].contains(":")) {
                // /home player:home
                String[] subArg = args[0].split(":");

                // Try to retrieve the player
                Player target = Utils.getPlayer(subArg[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Can't find " + args[0]);
                    return true;
                }

                user = plugin.getUser(target.getUniqueId());
                location = user.getHome(subArg[1]);
                if (location != null) {
                    player.teleport(location);
                    player.sendMessage(ChatColor.GREEN + "Successfully teleported to " +
                            target.getName() + "'s home: " + subArg[1]);
                } else {
                    player.sendMessage(ChatColor.RED + "Can't find home " + subArg[1] +
                            " for player " + target.getName());
                    player.sendMessage(ChatColor.RED + "Use " + ChatColor.DARK_PURPLE +
                            "/homes §d[§5get§e:§5player§d]" + ChatColor.RED +
                            " to list their available homes.");
                }
                return true;
            }

            // Find valid location in the player's home list
            location = user.getHome(args[0]);
            if (location == null) {
                player.sendMessage(ChatColor.RED + args[0] + " is not a valid home. Use " +
                        ChatColor.DARK_PURPLE + "/homes" + ChatColor.RED + " to display " +
                        "your available homes.");
                return true;
            }

            // Start a TeleportTask with the TeleportManager
            plugin.getTeleportManager().teleportEvent(
                    new Teleport(player, null, location, TeleportType.HOME));
            return true;
        } else {
            // Console can't teleport to a player home
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
