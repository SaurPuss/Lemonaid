package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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

    // TODO remove the hash map and get location straight from config
    private static HashMap<Player, Location> lastLocation = new HashMap<>();
    private static HashSet<Teleport> pendingRequests = new HashSet<>();

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

    public static void teleportEvent(Teleport tp) {
        Economy economy = Lemonaid.getEconomy();

        if (economy.isEnabled()) {
            // Attempt to charge the client for the teleport
            String path = "";
            switch (tp.tpType) {
                case TPA:
                case TPAHERE:
                    path = "config-path-to-tpa-cost";
                    break;
                case PTP:
                case PTPHERE:
                    path = "config-path-to-ptp-cost";
                    break;
                case BACK:
                    path = "config-path-to-tpback-cost";
                    break;
            }
            EconomyResponse response = economy.withdrawPlayer(tp.client, plugin.getConfig().getDouble(path));

            if (!response.transactionSuccess()) {
                tp.client.sendMessage(Utils.tpa("Balance too low! Teleportation request canceled!"));
                return;
            }
        }
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

    // TODO method to populate lastlocation map on start-up
    // TODO method to (periodically) save lastlocation map to config

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
                tp.client.sendMessage(Utils.tpa("You have a teleport request pending. Type: /tpacancel remove it."));
                return false; }
            // target already has an outgoing request
            if (((t.client == tp.target) && ((t.tpType == TeleportType.TPA)) || (t.tpType == TeleportType.PTP))) {
                tp.client.sendMessage(Utils.tpa(tp.target.getName() + " already has a pending teleport request."));
                return false; }
            // target already has an incoming request
            if ((t.target == tp.target) && ((tp.tpType == TeleportType.TPAHERE)) || (tp.tpType == TeleportType.PTPHERE)) {
                tp.client.sendMessage(Utils.tpa(tp.target.getName() + " already has a pending teleport request."));
                return false; }
        }

        // TODO config file delay timer
        int delay = 30; // seconds
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
        tp.target.sendMessage(Utils.tpa(request));
        tp.client.sendMessage(Utils.tpa("Request sent to " + tp.target.getName()));
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
                teleportTask(tp.client, tp.target, counter, tp); break;
            // target -> client
            case TPAHERE: case PTPHERE:
                teleportTask(tp.target, tp.client, counter, tp); break;
            // client -> lastLocation
            case BACK: default:
                // Basically teleportTask but just different enough to not warrant an overloaded method
                tp.client.sendMessage(Utils.tpa("Teleportation commencing in " + tp.counter + " seconds! Do not move!"));
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    Location location = tp.client.getLocation();
                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();
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
                        Bukkit.getScheduler().cancelTask(id);
                    }
                }, 0L, 20L);
        }
    }

    private static void teleportTask(Player player, Player target, int timer, Teleport tp) {
        // player -> target
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Location location = player.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            int counter = timer;
            player.sendMessage(Utils.tpa("Teleportation commencing in " + counter + " seconds! Do not move!"));
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
                    player.sendMessage(Utils.chat(counter + " seconds until teleport!"));
                } else if (counter <= 5) {
                    player.sendMessage(Utils.chat(counter + "!"));
                }
            } else {
                // client has moved! cancel task!
                player.sendMessage(Utils.chat("Teleportation canceled!"));
                Bukkit.getScheduler().cancelTask(id);
            }
        }, 0L, 20L);
    }

    public static boolean isConsole(CommandSender sender) {
        sender.sendMessage(Utils.console("Only players can use this command."));
        return true;
    }
}