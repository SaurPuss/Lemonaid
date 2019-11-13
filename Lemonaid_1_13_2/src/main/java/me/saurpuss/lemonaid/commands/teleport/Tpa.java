package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PermissionMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tpa implements CommandExecutor, PermissionMessages {

    Lemonaid plugin;
    public Tpa(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) return false;
//                sender.sendMessage(ChatColor.GOLD + "Type: §d/tpa §5<§dname§5> §6to request a " +
//                        "teleport.");

            Player player = (Player) sender;
            // Attempt to get a tp target
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Can't find " + args[0]);
                return true;
            }

            // Check if target is /busy or has player /ignored
            User user = plugin.getUserManager().getUser(target.getUniqueId());
            if (user.isBusy() || user.isIgnored(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + target.getName() + " is unavailable.");
                return true;
            }

            // Start tp request
            plugin.getTeleportManager().addRequest(new Teleport(player, target, null,
                    TeleportType.TPA));
        } else { // Console can't do this, like at all
            sender.sendMessage(playerOnly());
        }
        return true;
    }
}
