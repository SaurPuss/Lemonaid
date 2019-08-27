package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class Teleport {
    static Lemonaid plugin = Lemonaid.getInstance();

    // Object fields
    private Player client;
    private Player target;
    private TeleportType tpType;
    // TODO set counter/cooldown in config file
    private static int counter = 15;
    private static int id;

    private static HashSet<Teleport> teleports = new HashSet<>();
    private static HashMap<Player, Location> lastLocation = new HashMap<>();

    Teleport() {}

    public Teleport(Player client, Player target, TeleportType tpType) {
        this.client = client;
        this.target = target;
        this.tpType = tpType;
    }

    public static boolean addTeleport(Teleport tp) {
        // THis person doesn't have any pending tp requests
        if (!teleports.contains(tp.client)) {
            teleports.add(tp);
            // TODO add exipiration?
            return true;
        }
        // A person can have multiple incoming teleports
        else if (tp.tpType == TeleportType.TPAHERE) {
            // TODO continue to log the request, also add PTPHERE
            teleports.add(tp);
            // TODO add exipiration?
            return true;
        } else {
            tp.client.sendMessage(Utils.tpa("You already have a pending teleportation request"));
            return false;
        }
    }

    // TODO let teleportEvent handle this
    public static void back(Player player) {
        // Set current location to last location
        Location origin = player.getLocation();
        Location destination = getLastLocation(player);

        if (destination == null) {
            player.sendMessage(Utils.tpa("You have no location to return to!"));
            setLastLocation(player, origin);
            return;
        }

        player.sendMessage(Utils.tpa("Returning to your last location!"));
        // TODO delay
        // new Teleport(player, null, BACK)

        player.teleport(destination);
        setLastLocation(player, origin);
    }

    public static void teleportEvent(Teleport tp) {
        Economy economy = Lemonaid.getEconomy();

        if (economy.isEnabled()) {
            // Attempt to charge the client for the teleport
            String path = "";
            switch (tp.tpType) {
                case TPA: case TPAHERE:
                    path = "config-path-to-tpa-cost"; break;
                case PTP: case PTPHERE:
                    path = "config-path-to-ptp-cost"; break;
                case BACK:
                    path = "config-path-to-tpback-cost"; break;
            }
            EconomyResponse response = economy.withdrawPlayer(tp.client, plugin.getConfig().getDouble(path));

            if (!response.transactionSuccess()) {
                tp.client.sendMessage(Utils.tpa("Balance too low! Teleportation request canceled!"));
                teleports.remove(tp);
                return;
            }
        }
        activateTp(tp);
    }

    private static void setLastLocation(Player player, Location location) {
        lastLocation.put(player, location);
    }

    private static Location getLastLocation(Player player) {
        if (lastLocation.containsKey(player)) {
            return lastLocation.get(player);
        }
        // TODO get from config

        return null;
    }

    // TODO method to populate lastlocation map on start-up
    // TODO method to (periodically) save lastlocation map to config


    private static void activateTp(Teleport tp) {
        // Fallback incase this ends up being a /back somehow
        // TODO Move this into the scheduler and allow activateTp to handle /back too for the timer and location
        // activateTp(new Teleport(player, null, player.getLocation, player.getLastLocation, BACK))
        if (tp.tpType == TeleportType.BACK) {
            back(tp.client);
            teleports.remove(tp);
            return;
        }

        // Create the task based on the tp type
        // TODO create convenience method for inside the switch statement, and combine this with the initial economy check method
        switch (tp.tpType) {
            case TPA:
            case PTP:
                // client -> target
                tp.client.sendMessage(Utils.tpa("Teleportation commencing in " + tp.counter + " seconds! Do not move!"));
                // update Teleport() origin
                Location clientLoc = tp.client.getLocation();
                int clientX = clientLoc.getBlockX();
                int clientY = clientLoc.getBlockY();
                int clientZ = clientLoc.getBlockZ();

                // Create a check every second to make sure the player doesn't move
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    // Check if the player hasn't moved from x/y/z
                    if ((clientX == tp.client.getLocation().getBlockX()) &&
                            (clientY == tp.client.getLocation().getBlockY()) &&
                            (clientZ == tp.client.getLocation().getBlockZ())) {
                        // client has not moved
                        tp.counter--;
                        // send client countdown messages
                        if (tp.counter == 10) {
                            tp.client.sendMessage(Utils.chat(tp.counter + " seconds until teleport!"));
                        } else if (tp.counter <= 5) {
                            tp.client.sendMessage(Utils.chat(tp.counter + "!"));
                        }
                    } else {
                        // client has moved! cancel task!
                        tp.client.sendMessage(Utils.chat("Teleportation canceled!"));
                        teleports.remove(tp);
                        Bukkit.getScheduler().cancelTask(id);
                    }
                    // teleport client & update lastLocation / teleports / task
                    tp.client.teleport(tp.target.getLocation());
                    setLastLocation(tp.client, clientLoc);
                    teleports.remove(tp);
                    Bukkit.getScheduler().cancelTask(id);
                }, 0L, 20L);
                break;
            case TPAHERE:
            case PTPHERE:
                // target -> client
                tp.target.sendMessage(Utils.tpa("Teleportation commencing in " + tp.counter + " seconds! Do not move!"));
                // update Teleport() origin
                Location targetLoc = tp.target.getLocation();
                int targetX = targetLoc.getBlockX();
                int targetY = targetLoc.getBlockY();
                int targetZ = targetLoc.getBlockZ();

                // Create a check every second to make sure the player doesn't move
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    // Check if the player hasn't moved from x/y/z
                    if ((targetX == tp.target.getLocation().getBlockX()) &&
                            (targetY == tp.target.getLocation().getBlockY()) &&
                            (targetZ == tp.target.getLocation().getBlockZ())) {
                        // client has not moved
                        tp.counter--;
                        // send client countdown messages
                        if (tp.counter == 10) {
                            tp.target.sendMessage(Utils.chat(tp.counter + " seconds until teleport!"));
                        } else if (tp.counter <= 5) {
                            tp.target.sendMessage(Utils.chat(tp.counter + "!"));
                        }
                    } else {
                        // client has moved! cancel task!
                        tp.target.sendMessage(Utils.chat("Teleportation canceled!"));
                        teleports.remove(tp);
                        Bukkit.getScheduler().cancelTask(id);
                    }
                    // teleport client & update lastLocation / teleports / task
                    tp.target.teleport(tp.target.getLocation());
                    setLastLocation(tp.target, targetLoc);
                    teleports.remove(tp);
                    Bukkit.getScheduler().cancelTask(id);
                }, 0L, 20L);
                break;
            case BACK:
            default:
                // client -> lastLocation
                tp.client.sendMessage(Utils.tpa("Teleportation commencing in " + tp.counter + " seconds! Do not move!"));
                // update Teleport() origin
                Location location = tp.client.getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();

                // Create a check every second to make sure the player doesn't move
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    // Check if the player hasn't moved from x/y/z
                    if ((x == tp.client.getLocation().getBlockX()) &&
                            (y == tp.client.getLocation().getBlockY()) &&
                            (z == tp.client.getLocation().getBlockZ())) {
                        // client has not moved
                        tp.counter--;
                        // send client countdown messages
                        if (tp.counter == 10) {
                            tp.target.sendMessage(Utils.chat(tp.counter + " seconds until teleport!"));
                        } else if (tp.counter <= 5) {
                            tp.target.sendMessage(Utils.chat(tp.counter + "!"));
                        }
                    } else {
                        // client has moved! cancel task!
                        tp.client.sendMessage(Utils.chat("Teleportation canceled!"));
                        teleports.remove(tp);
                        Bukkit.getScheduler().cancelTask(id);
                    }
                    // teleport client & update lastLocation / teleports / task
                    tp.client.teleport(getLastLocation(tp.client));
                    setLastLocation(tp.client, location);
                    teleports.remove(tp);
                    Bukkit.getScheduler().cancelTask(id);
                }, 0L, 20L);
        }
    }
}