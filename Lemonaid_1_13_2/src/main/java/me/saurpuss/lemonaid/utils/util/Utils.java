package me.saurpuss.lemonaid.utils.util;

import org.bukkit.ChatColor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {

    // Translate Chat Colors with & instead of §
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // No permission errors
    public static String noPermission() { return "§cYou don't have permission to do this!"; }
    public static String playerOnly() {
        return "§cOnly players can use this command!";
    }

    // Formatting LocalDate, ex: AUG 02
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
    public static String dateToString(LocalDate date) { return date.format(formatter); }
    public static LocalDate stringToDate(String date) { return LocalDate.parse(date, formatter); }
}
