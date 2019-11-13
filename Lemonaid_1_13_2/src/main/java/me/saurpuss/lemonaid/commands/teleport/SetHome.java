package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PlayerSearch;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHome implements CommandExecutor, PlayerSearch {

    private Lemonaid lemonaid;

    public SetHome(Lemonaid plugin) {
        lemonaid = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) return false;

        // admin or console setting a home for a player
        if (sender.hasPermission("lemonaid.admin") && args[0].contains(":")) {
            // Admin forcing a sethome for a player
            String[] subArgs = args[0].split(":");

            Player player = getPlayer(subArgs[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + subArgs[0] + "!");
                return true;
            }

            User user = lemonaid.getUserManager().getUser(player.getUniqueId());
            if (user.getHome(subArgs[1]) != null) {
                sender.sendMessage(ChatColor.RED + subArgs[0] + " already has a home named "
                        + subArgs[1] + "!");
                return true;
            }

            if (args.length == 1 && sender instanceof Player) {
                Player admin = (Player) sender;
                user.addHome(subArgs[1], admin.getLocation());
                sender.sendMessage(ChatColor.GREEN + "Added home " + ChatColor.BLUE +
                        subArgs[1] + ChatColor.GREEN + " at your location for " +
                        ChatColor.LIGHT_PURPLE + subArgs[0]);
                return true;
            } else if (args.length == 5) {
                World world = Bukkit.getWorld(args[1]);
                String coords = StringUtils.join(args, ' ', 1, args.length);

                if (world == null || !StringUtils.isNumeric(args[2]) ||
                        !StringUtils.isNumeric(args[3]) || !StringUtils.isNumeric(args[4])) {

                    sender.sendMessage(ChatColor.RED + "Invalid location at " + ChatColor.GOLD + coords);
                }
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                Location location = new Location(world, x, y, z);

                user.addHome(subArgs[1], location);
                lemonaid.getUserManager().updateUser(user);
                sender.sendMessage(ChatColor.GREEN + "Added home " + ChatColor.BLUE +
                        subArgs[1] + ChatColor.GREEN + " at " + ChatColor.GOLD + coords +
                        ChatColor.GREEN + " for " + ChatColor.LIGHT_PURPLE + subArgs[0]);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.LIGHT_PURPLE +
                        "/sethome player:homeName world x y z" + ChatColor.RED + "!");
                return true;
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = lemonaid.getUserManager().getUser(player.getUniqueId());

            // Check if home slots available
            if (user.homeCount() >= user.getMaxHomes()) {
                // TODO potentially update output string
                player.sendMessage(ChatColor.RED + "No home slots available! " + user.listHomes());
                return true;
            }

            user.addHome(args[0], player.getLocation());
            lemonaid.getUserManager().updateUser(user);
            player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.GOLD + args[0] +
                    ChatColor.GREEN + " to your homes! Use " + ChatColor.BLUE + "/homes" +
                    ChatColor.GREEN + " too see your list of homes");
            return true;
        }
        // Console can only set for players
        else {
            sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.LIGHT_PURPLE +
                    "/sethome player:homeName world x y z" + ChatColor.RED + "!");
            return true;
        }
    }
}
