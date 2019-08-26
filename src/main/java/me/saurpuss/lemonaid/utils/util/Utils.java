package me.saurpuss.lemonaid.utils.util;

import org.bukkit.ChatColor;

public class Utils {

    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&e[Lemonaid]&b " + message);
    }

    public static String error() {
        return ChatColor.translateAlternateColorCodes('&', "&e[Lemonaid]&c You don't have permission to do this!" );
    }

    public static String admin(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&c[ADMIN] &e" + message);
    }

    public static String console(String message) {
        return "[Lemonaid] " + message;
    }

    public static String announce(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String dm(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&e[MSG]&b " + message);
    }

    public static String tpa(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&e[TPA]&b " + message);
    }

    public static String broadcast(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&c[&eBROADCAST&c]&b " + message);
    }
}
