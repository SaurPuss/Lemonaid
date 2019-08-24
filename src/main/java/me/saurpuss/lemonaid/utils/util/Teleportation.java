package me.saurpuss.lemonaid.utils.util;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Helper class to facilitate teleportation events requested by the TPA commands
 */
public class Teleportation {

    // Map all pending Tpa and TpaHere requests
    private static HashMap<Player, Player> pendingTPA = new HashMap<>();

    // Add or Override pending Tp request
    public static void request(Player player, Player target) {
        pendingTPA.put(player, target);
    }

    // Remove pending Tp request
    public static void remove(Player player) {
        pendingTPA.remove(player);
    }

    // Check if player has pending Tp requests
    public static boolean isPendingPlayer(Player player) {
        return pendingTPA.containsKey(player);
    }

    // Check if target has pending Tp requests
    public static boolean isPendingTarget(Player target) {
        return pendingTPA.containsValue(target);
    }

    // Add each pending requester to list
    public static HashSet<String> pendingRequests(Player target) {
        HashSet<String> list = new HashSet<>();

        for (HashMap.Entry<Player, Player> entry : pendingTPA.entrySet()) {
            if (entry.getValue() == target) {
                list.add(entry.getKey().toString());
            }
        }

        return list;
    }

    public static Player getSource(Player target) {
        for (HashMap.Entry<Player, Player> entry : pendingTPA.entrySet()) {
            if (entry.getValue() == target) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static Player getTarget(Player source) {
        return pendingTPA.get(source);
    }

    // Execute teleportation
    public static void teleportationEvent(Player source, Player focus) {
        source.sendMessage(Utils.chat("Teleportation commencing do not move!"));

        Location origin = source.getLocation();
        Location destination = focus.getLocation();

        // Start timer
        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Lemonaid.class), () -> {
            if (source.getLocation() == origin) {
                source.sendMessage("Teleportation commencing!");
                focus.sendMessage("Incoming teleportation!");
                source.teleport(destination);
            } else {
                source.sendMessage(Utils.chat("Teleportation request canceled."));
            }

            // TODO add remove here?

        }, 100l);
    }


    public static void updateRequests(Player source, Player focus, boolean accept) {
        if (pendingTPA.containsKey(source)) {
            if (accept) {
                teleportationEvent(source, focus);
            }
            remove(source);
        } else {
            focus.sendMessage(Utils.chat("No pending teleportation requests."));
        }
    }

    public static boolean isConsole(CommandSender sender) {
        sender.sendMessage(Utils.console("Only players can use this command."));
        return true;
    }
}
