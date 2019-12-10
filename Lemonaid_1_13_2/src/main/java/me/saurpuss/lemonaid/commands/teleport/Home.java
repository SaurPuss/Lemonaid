package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PermissionMessages;
import me.saurpuss.lemonaid.utils.utility.PlayerSearch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /home Command available to players, requires lemonaid.teleport.home permission node. Players
 * with the lemonaid.teleport.admin permission node can use /home player:home to teleport to
 * a specific player home instantly.
 */
public class Home implements CommandExecutor, PlayerSearch, PermissionMessages {

    /**
     * Dependency injection of the current plugin instance
     */
    private Lemonaid plugin;

    /**
     * Home Constructor
     * @param plugin Dependency injection of the current plugin instance.
     */
    public Home(Lemonaid plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to find a Location that matches a player's home to args[0] input. If
     * successful a Teleport event will be initiated. Players with lemonaid.teleport.admin
     * can attempt to retrieve a valid location for a player home and teleport to said
     * location immediately if successful.
     * @param sender Player
     * @param command home
     * @param label none
     * @param args home_name (or player:home_name for administrators)
     *             to search for in player homes
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = plugin.getUserManager().getUser(player.getUniqueId());
            Location location;
            if (args.length == 0) {
                // player only has a single home to teleport to
                if (user.homeCount() == 0) {
                    player.sendMessage(ChatColor.RED + "No Homes found!");
                    return true;
                }
                if (user.homeCount() == 1) {
                    location = user.getFirstHome();
                    if (location != null) // fallback check
                        plugin.getTeleportManager().addRequest(
                                new Teleport(player, null, location, TeleportType.HOME));

                    return true;
                }
                // player error
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Multiple homes found! Use " +
                        ChatColor.DARK_PURPLE + "/homes" + ChatColor.LIGHT_PURPLE +
                        " do display them.");
                return true;
            }

            // Intercept command meant for other home options
            if (args[0].equalsIgnoreCase("set") ||
                    args[0].equalsIgnoreCase("s") ||
                    args[0].equalsIgnoreCase("add")) {
                // home set homename
                // home set player:homename
                String sethome = "sethome " + args[1];

                return Bukkit.dispatchCommand(sender, sethome);
            } else if (args[0].equalsIgnoreCase("del") ||
                    args[0].equalsIgnoreCase("delete") ||
                    args[0].equalsIgnoreCase("d")) {
                // home del homename
                // home del player:homename
                String delhome = "delhome " + args[1];

                return Bukkit.dispatchCommand(sender, delhome);
            } else if (args[0].equalsIgnoreCase("update") ||
                    args[0].equalsIgnoreCase("edit")) {
                // home edit homename
                // home edit player:homename
                String updatehome = "updatehome " + args[1];

                return Bukkit.dispatchCommand(sender, updatehome);
            } else if (args[0].equalsIgnoreCase("list") ||
                    (args[0].contains("list:") && sender.hasPermission("lemonaid.teleport.admin"))) {
                // homes [self]
                // homes player
                String homes = "homes " + args[0];

                return Bukkit.dispatchCommand(sender, homes);
            }

            // This is an admin attempting to teleport to a specific player's home
            if (player.hasPermission("lemonaid.teleport.admin") && args[0].contains(":")) {
                // /home player:home
                String[] subArg = args[0].split(":");

                // Try to retrieve the player
                Player target = getPlayer(subArg[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Can't find " + subArg[0]);
                    return true;
                }

                user = plugin.getUserManager().getUser(target.getUniqueId());
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
            plugin.getTeleportManager().addRequest(
                    new Teleport(player, null, location, TeleportType.HOME));
            return true;
        } else {
            // Console can't teleport to a player home
            sender.sendMessage(playerOnly());
            return true;
        }
    }
}
