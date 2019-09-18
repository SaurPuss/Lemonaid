package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Teleport {
    private static Lemonaid plugin = Lemonaid.getPlugin(Lemonaid.class);
    private static HashSet<Teleport> pendingRequests = new HashSet<>();

    // Teleport()
    // TODO replace player with UUID to save space?
    private Player client;
    private Player target;
    private TeleportType tpType;
    private static transient int id;

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
     * @param tp Teleportation request object
     */
    public static void teleportEvent(Teleport tp) {
//        Economy economy = Lemonaid.getEconomy();
//        if (economy.isEnabled()) {
//            // Attempt to charge the client for the teleport
//            EconomyResponse response = economy.withdrawPlayer(tp.client,
//                    plugin.getConfig().getDouble("teleport." + tp.tpType.name + ".cost"));
//            if (!response.transactionSuccess()) {
//                tp.client.sendMessage(Utils.color("&cBalance too low! Teleportation request canceled!"));
//                return;
//            }
//        }

        activateTp(tp);
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

        // Check for cross-world and if it's allowed
        if (!plugin.getConfig().getBoolean("teleport." + tp.tpType.name + ".cross-world")) {
            tp.client.sendMessage(Utils.color("You cannot teleport between worlds. Request canceled."));
            return false;
        }

        int delay = plugin.getConfig().getInt("teleport.request-timer");
        String request = "";
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> removeRequest(tp), delay * 20L);
        return true;
    }

    public static void removeRequest(Teleport tp) { pendingRequests.remove(tp); }

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

    private static void activateTp(Teleport tp) {
        removeRequest(tp);
        teleportTask(tp.client, tp.target, plugin.getConfig().getInt("teleport." + tp.tpType.name + ".timer"));
    }

    private static void teleportTask(Player player, Player target, int timer) {
        // player -> target
        // TODO replace with non bukkit task scheduler
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Location location = player.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            int counter = timer;
            player.sendMessage(Utils.color("&6Teleportation commencing in &6" + counter + "&6 seconds! Do not move!"));
            if (counter == 0) {
                // teleport client & update lastLocation / teleports / task
                Lemon user = plugin.getUser(player.getUniqueId());
                if (target != null)
                    player.teleport(target.getLocation());
                else {
                    player.teleport(user.getLastLocation());
                }
                user.setLastLocation(location);
                user.updateUser();
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
        sender.sendMessage(Utils.playerOnly());
        return true;
    }
}