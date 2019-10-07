package me.saurpuss.lemonaid.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringJoiner;

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

    // Get an online player, or offline if necessary
    public static Player getPlayer(String name) {
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

    // Location from String for database usage
    public static Location locationFromString(String s) {
        String[] loc = s.split("\\|");
        return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]),
                Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
    }
    public static String stringToLocation(Location location) {
        StringJoiner string = new StringJoiner("|");
        string.add(Objects.requireNonNull(location.getWorld()).toString());
        string.add(String.valueOf(location.getBlockX()));
        string.add(String.valueOf(location.getBlockY()));
        string.add(String.valueOf(location.getBlockZ()));

        return string.toString();
    }
}
