package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Teleport {
    private static Lemonaid plugin = Lemonaid.getInstance();
    private static HashSet<Teleport> pendingRequests = new HashSet<>();
    // TODO remove the hash map and get location straight from config
    private static HashMap<Player, Location> lastLocation = new HashMap<>();

    // Teleport()
    private Player client;
    private Player target;
    private TeleportType tpType;
    private static int id;

    Teleport() {}
    public Teleport(Player client, Player target, TeleportType tpType) {
        this.client = client;
        this.target = target;
        this.tpType = tpType;
    }

    public Player getClient() {
        return client;
    }
    public Player getTarget() {
        return target;
    }

    /**
     * Teleportation event start. If Vault is active withdraw from balance
     * and initiate requested tp.
     * // TODO add vault dependency stuff to Lemonaid.java
     * @param tp Teleportation request object
     */
    public static void teleportEvent(Teleport tp) {
//        Economy economy = Lemonaid.getEconomy();
//        if (economy.isEnabled()) {
//            // Attempt to charge the client for the teleport
//            String path = "";
//            switch (tp.tpType) {
//                case TPA:
//                case TPAHERE:
//                    path = "teleport.tpa.cost";
//                    break;
//                case PTP:
//                case PTPHERE:
//                    path = "teleport.ptp.cost";
//                    break;
//                case BACK:
//                    path = "teleport.back.cost";
//                    break;
//            }
//
//            EconomyResponse response = economy.withdrawPlayer(tp.client, plugin.getConfig().getDouble(path));
//            if (!response.transactionSuccess()) {
//                tp.client.sendMessage(Utils.color("&cBalance too low! Teleportation request canceled!"));
//                return;
//            }
//        }

        activateTp(tp);
    }

    private static void setLastLocation(Player player, Location location) {
        lastLocation.put(player, location);
        // TODO save to config
    }

    private static Location getLastLocation(Player player) {
        if (lastLocation.containsKey(player)) {
            // TODO get from config
            return lastLocation.get(player);
        } else {
            // save and return this location
            setLastLocation(player, player.getLocation());
            return player.getLocation();
        }
    }

    public static boolean addRequest(Teleport tp) {
        // Check if there are no conflicting requests
        for (Teleport t : pendingRequests) {
            // requester already has an outgoing request to this specific player regardless of type
            if ((t.client == tp.client) && (t.target == tp.target)) {
                // remove existing and break the loop
                pendingRequests.remove(t);
                break; }
            // requester already has an outgoing request
            // if requester already has incoming requests -> do nothing it will expire if ignored
            if ((t.client == tp.client) && ((tp.tpType == TeleportType.TPA)) || (tp.tpType == TeleportType.PTP)) {
                tp.client.sendMessage(Utils.color("&cYou have a teleport request pending. Type: /tpacancel remove it."));
                return false; }
            // target already has an outgoing request
            if (((t.client == tp.target) && ((t.tpType == TeleportType.TPA)) || (t.tpType == TeleportType.PTP))) {
                tp.client.sendMessage(Utils.color("&c" + tp.target.getName() + " already has a pending teleport request."));
                return false; }
            // target already has an incoming request
            if ((t.target == tp.target) && ((tp.tpType == TeleportType.TPAHERE)) || (tp.tpType == TeleportType.PTPHERE)) {
                tp.client.sendMessage(Utils.color("&c" + tp.target.getName() + " already has a pending teleport request."));
                return false; }
        }

        int delay = plugin.getConfig().getInt("teleport.available-timer");
        String request = "";
        // TODO genders?
        switch (tp.tpType) {
            case TPA: case PTP:
                request = tp.client.getDisplayName() + " wants to teleport to you."; break;
            case TPAHERE: case PTPHERE:
                request = tp.client.getDisplayName() + " wants you to teleport to them."; break;
            case BACK: default:
                plugin.getLogger().warning("Unexpected TeleportType tried to get listed in pending teleport requests!");
                return false;
        }
        tp.target.sendMessage(Utils.color("&6" + request));
        tp.client.sendMessage(Utils.color("&6Request sent to " + tp.target.getName()));
        pendingRequests.add(tp);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            removeRequest(tp);
        }, delay * 20L);
        return true;
    }

    public static HashSet<Teleport> retrieveRequest(Player target) {
        HashSet<Teleport> requests = new HashSet<>();
        for (Teleport t : pendingRequests)
            if (t.target == target)
                requests.add(t);

        return requests;
    }

    public static HashSet<Teleport> outgoingRequests(Player player) {
        HashSet<Teleport> requests = new HashSet<>();
        for (Teleport t : pendingRequests)
            if (t.client == player)
                requests.add(t);

        return requests;
    }


    public static void removeRequest(Teleport tp) {
        pendingRequests.remove(tp);
    }

    private static void activateTp(Teleport tp) {
        removeRequest(tp);
        switch (tp.tpType) {
            // client -> target
            case TPA: case PTP:
                teleportTask(tp.client, tp.target, plugin.getConfig().getInt("teleport.tpa.timer")); break;
            // target -> client
            case TPAHERE: case PTPHERE:
                teleportTask(tp.target, tp.client, plugin.getConfig().getInt("teleport.tpa.timer")); break;
            // client -> lastLocation
            case BACK: default:
                // Basically teleportTask but just different enough to not warrant an overloaded method
                // TODO if I set the target to location instead of a player I can combine this
                tp.client.sendMessage(Utils.color("&6Teleportation commencing in &5" +
                        plugin.getConfig().getInt("teleport.back.timer") + "&6 seconds! Do not move!"));
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    Location location = tp.client.getLocation();
                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();
                    int counter = plugin.getConfig().getInt("teleport.back.timer");
                    // Check if the player hasn't moved from x/y/z
                    if (counter == 0) {
                        // teleport client & update lastLocation / teleports / task
                        tp.client.teleport(getLastLocation(tp.client));
                        setLastLocation(tp.client, location);
                        Bukkit.getScheduler().cancelTask(id);
                    } else if ((x == tp.client.getLocation().getBlockX()) &&
                            (y == tp.client.getLocation().getBlockY()) &&
                            (z == tp.client.getLocation().getBlockZ())) {
                        // client has not moved
                        counter--;
                        // send client countdown messages
                        if (counter == 10) {
                            tp.target.sendMessage(Utils.color("&5" + counter + "&6 seconds until teleport!"));
                        } else if (counter <= 5) {
                            tp.target.sendMessage(Utils.color("&5"+ counter + "&6!"));
                        }
                    } else {
                        // client has moved! cancel task!
                        tp.client.sendMessage(Utils.color("&cTeleportation canceled!"));
                        Bukkit.getScheduler().cancelTask(id);
                    }
                }, 0L, 20L);
        }
    }

    private static void teleportTask(Player player, Player target, int timer) {
        // player -> target
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Location location = player.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            int counter = timer;
            player.sendMessage(Utils.color("&6Teleportation commencing in &6" + counter + "&6 seconds! Do not move!"));
            if (counter == 0) {
                // teleport client & update lastLocation / teleports / task
                player.teleport(target.getLocation());
                setLastLocation(player, location);
                Bukkit.getScheduler().cancelTask(id);
            } else if ((x == player.getLocation().getBlockX()) &&
                    (y == player.getLocation().getBlockY()) &&
                    (z == player.getLocation().getBlockZ())) {
                // client has not moved
                counter--;
                // send client countdown messages
                if (counter == 10) {
                    player.sendMessage(Utils.color("&5" + counter + "&6 seconds until teleport!"));
                } else if (counter <= 5) {
                    player.sendMessage(Utils.color("&5" + counter + "&6!"));
                }
            } else {
                // client has moved! cancel task!
                player.sendMessage(Utils.color("&cTeleportation canceled!"));
                Bukkit.getScheduler().cancelTask(id);
            }
        }, 0L, 20L);
    }

    public static boolean isConsole(CommandSender sender) {
        sender.sendMessage(Utils.console());
        return true;
    }
}