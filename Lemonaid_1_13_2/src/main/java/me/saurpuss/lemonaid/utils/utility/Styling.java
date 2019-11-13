package me.saurpuss.lemonaid.utils.utility;

import org.bukkit.ChatColor;

public interface Styling {

    // Translate Chat Colors with & instead of §
    default String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
