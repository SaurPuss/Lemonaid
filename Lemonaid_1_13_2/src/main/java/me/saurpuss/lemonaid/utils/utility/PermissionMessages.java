package me.saurpuss.lemonaid.utils.utility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public interface PermissionMessages {
    // No permission errors
    default String noPermission() {
        return ChatColor.RED + "You don't have permission to do this!";
    }
    default String playerOnly() {
        return ChatColor.RED + "Only players can use this command!";
    }

    default void adminNotification(String message) {
        String alert = ChatColor.DARK_RED + "[MOD ALERT] " + ChatColor.RESET + message;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage(alert);
            }
        }

        Bukkit.getServer().getLogger().log(Level.INFO, alert);
    }
}
