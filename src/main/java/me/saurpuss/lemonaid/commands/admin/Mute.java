package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Mute implements CommandExecutor {

    public HashSet<Player> mutelist = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.mute"))) {
            if (args.length == 0) {
                sender.sendMessage(Utils.admin("Usage: /mute <player> <time> <reason>"));
                return true;
            }
            // Get player from the first argument
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                // If there is no valid online player, check offline players
                OfflinePlayer[] list = Bukkit.getOfflinePlayers();
                for (OfflinePlayer p : list) {
                    if (p.getPlayer().getName().equalsIgnoreCase(args[0])) {
                        target = p.getPlayer();
                    }
                }
                // Still haven't found a match
                if (target == null) {
                    sender.sendMessage(Utils.admin(args[0] + " not found."));
                    return true;
                }
            } // We now have a target
            if (args.length == 1) {
                mute(target); // Just toggle that mute
                return true;
            } else {

            }


            return true;
        } else {
            sender.sendMessage(Utils.error());
            return true;
        }
    }



    private void mute(Player player) {
        if (!mutelist.contains(player)) {
            mutelist.add(player);
        } else {
            mutelist.remove(player);
        }

        // TODO mute action
    }
}
