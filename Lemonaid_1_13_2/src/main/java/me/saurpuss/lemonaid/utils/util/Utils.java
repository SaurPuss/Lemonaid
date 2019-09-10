package me.saurpuss.lemonaid.utils.util;

import org.bukkit.ChatColor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {
    // 2 AUG 2019
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String noPermission() {
        return ChatColor.translateAlternateColorCodes('&',
                "&cYou don't have permission to do this!" );
    }

    public static String playerOnly() {
        return "Only players can use this command!";
    }

    public static String dateToString(LocalDate date) { return date.format(formatter); }
    public static LocalDate stringToDate(String date) { return LocalDate.parse(date, formatter); }


}
