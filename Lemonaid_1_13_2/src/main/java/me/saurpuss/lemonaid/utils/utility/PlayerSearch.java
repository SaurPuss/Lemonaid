package me.saurpuss.lemonaid.utils.utility;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PlayerSearch {

    // Get an online player, or offline if necessary
    default Player getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null)
            return player;

        // Try to get an offline player
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                player = p.getPlayer();
                return player;
            }
        }

        return null;
    }
}
