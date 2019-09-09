package me.saurpuss.lemonaid.utils.util;

import org.bukkit.ChatColor;

public class Utils {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String noPermission() {
        return ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to do this!" );
    }

    public static String admin(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&c[ADMIN] &e" + message);
    }

    public static String console() {
        return "Only players can use this command";
    }

    public static String tpa(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&e[TPA]&b " + message);
    }

}
