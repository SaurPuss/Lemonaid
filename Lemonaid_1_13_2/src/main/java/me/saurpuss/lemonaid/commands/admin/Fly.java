package me.saurpuss.lemonaid.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
     *
     * @param sender  Command Sender
     * @param command /fly or /fly [target]
     * @param label   fly
     * @param args    target player to toggle fly on
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (sender.hasPermission("lemonaid.fly")) {
            // Toggle fly on command sender
            if (args.length == 0) {
                if (sender instanceof Player) {
                    fly((Player) sender);
                    return true;
                }
                return false; // Console can't do this
            }

            // Toggle fly on a target
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + args[0] + " not found.");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "Toggling fly mode on " + target.getName());
            fly(target);
        }
        return true;
    }

    /**
     * Convenience method to toggle player fly
     *
     * @param player target to toggle flight mode on
     */
    private void fly(Player player) {
        if (!player.isFlying()) {
            player.setAllowFlight(true);
            player.sendMessage(ChatColor.GOLD + "You can now fly!");
            return;
        }

        player.setAllowFlight(false);
        player.setFlying(false);
        player.sendMessage(ChatColor.RED + "You can no longer fly!");
    }
}
