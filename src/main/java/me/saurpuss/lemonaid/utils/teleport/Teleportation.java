package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Helper class to facilitate teleportation events requested by the TPA commands
 */
public class Teleportation {
    static Lemonaid plugin = Lemonaid.getInstance();
    static Economy economy = Lemonaid.getEconomy();
    private static int counter;
    private static int id;

    // TODO make this cross-world
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
    public static void removeTarget(Player target) {
        for (HashMap.Entry<Player, Player> entry : pendingTPA.entrySet()) {
            if (entry.getValue() == target) {
                pendingTPA.remove(entry.getKey());
            }
        }
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
        // TODO bool tpa/tpahere, or case
        source.sendMessage(Utils.tpa("Teleportation commencing in " + counter + " seconds do not move!"));

        Location destination = focus.getLocation();
        int oX = source.getLocation().getBlockX();
        int oY = source.getLocation().getBlockY();
        int oZ = source.getLocation().getBlockZ();
        counter = 15;

        // Start repeating timer
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if ((oX == source.getLocation().getBlockX()) && (oY == source.getLocation().getBlockY()) &&
                (oZ == source.getLocation().getBlockZ()) && counter >= 0) {
                if ((counter == 10) || (counter <= 5))
                    source.sendMessage(Utils.tpa(counter + "!"));

                if (counter == 0) {
                    if (economy.isEnabled()) {
                        // TODO make sure this money is taken from the initial requester
                        EconomyResponse response = economy.withdrawPlayer(
                                source, plugin.getConfig().getDouble("tp-cost"));
                        if (!response.transactionSuccess()) {
                            source.sendMessage(Utils.tpa("Balance too low! Teleportation request canceled!"));
                        } else {
                            source.sendMessage(Utils.tpa("Teleportation commencing!"));
                            focus.sendMessage(Utils.tpa("Incoming teleportation!"));
                            source.teleport(destination);
                        }
                    } else {
                        source.sendMessage(Utils.tpa("Teleportation commencing!"));
                        focus.sendMessage(Utils.tpa("Incoming teleportation!"));
                        source.teleport(destination);
                    }
                    // TODO end task
                    pendingTPA.remove(source);
                    Bukkit.getScheduler().cancelTask(id);
                }
                counter--;
            } else {
                source.sendMessage(Utils.tpa("Teleportation request canceled!"));
                pendingTPA.remove(source);
                Bukkit.getScheduler().cancelTask(id);
            }
        },0L, 20l);
    }

    public static boolean isConsole(CommandSender sender) {
        sender.sendMessage(Utils.console("Only players can use this command."));
        return true;
    }
}
