package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * When you speak the language of the gods you might as well fly too
 */
public class Fly implements CommandExecutor {

    /**
     * Toggle a player or a target player's fly mode.
     * @param sender Command Sender
     * @param command /fly or /fly [target]
     * @param label fly
     * @param args target player to toggle fly on
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("lemonaid.admin.fly")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Usage: /fly <target>");
                    return true;
                } else {
                    return fly((Player) sender);
                }
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.color("&c" + args[0] + " is not online."));
                return true;
            }

            return fly(target);
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }

    /**
     * Convenience method to toggle player fly
     * @param player target to toggle flight mode on
     * @return true
     */
    private boolean fly(Player player) {
        if (!player.isFlying()) {
            player.setAllowFlight(true);
            player.sendMessage(Utils.color("&6You can now fly!"));
            return true;
        }

        player.setAllowFlight(false);
        player.setFlying(false);
        player.sendMessage(Utils.color("&cYou can no longer fly!"));
        return true;
    }
}
