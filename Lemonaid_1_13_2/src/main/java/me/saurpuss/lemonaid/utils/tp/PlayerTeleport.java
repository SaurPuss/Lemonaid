package me.saurpuss.lemonaid.utils.tp;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import net.milkbowl.vault.economy.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerTeleport {

    private static transient Lemonaid plugin = Lemonaid.getPlugin(Lemonaid.class);
    private static transient HashSet<PlayerTeleport> pendingRequests = new HashSet<>();

    // Teleport()
    private Player client;
    private Player target;
    private TeleportType tpType;
    private static transient int id;

    PlayerTeleport() {}
    public PlayerTeleport(Player client, Player target, TeleportType tpType) {
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
    public static void teleportEvent(PlayerTeleport tp) {
        Economy economy = plugin.getEconomy();
        if (economy.isEnabled()) {
            // Attempt to charge the client for the teleport
            EconomyResponse response = economy.withdrawPlayer(tp.client,
                    plugin.getConfig().getDouble("teleport." + tp.tpType.name + ".cost"));
            if (!response.transactionSuccess()) {
                tp.client.sendMessage(ChatColor.RED + "Balance too low! " +
                        "Teleportation request canceled!");
                return;
            }
        }

        activateTp(tp);
    }

    public static boolean addRequest(PlayerTeleport tp) {
        // Check if there are no conflicting requests
        for (PlayerTeleport t : pendingRequests) {
            // requester already has an outgoing request to this specific player regardless of type
            if (t.client == tp.client && t.target == tp.target) {
                // remove existing and break the loop
                pendingRequests.remove(t);
                break; }
            // requester already has an outgoing request
            // if requester already has incoming requests -> do nothing it will expire if ignored
            if (t.client == tp.client &&
                    (tp.tpType == TeleportType.TPA || tp.tpType == TeleportType.PTP)) {
                tp.client.sendMessage(ChatColor.RED + "You already have an outgoing " +
                        "teleport request pending. Type: " + ChatColor.DARK_AQUA +
                        "/tpacancel" + ChatColor.RED + " remove it.");
                return false; }
            // target already has an outgoing or incoming request
            if ((t.client == tp.target &&
                    (t.tpType == TeleportType.TPA || t.tpType == TeleportType.PTP)) ||
                (t.target == tp.target &&
                    (tp.tpType == TeleportType.TPAHERE || tp.tpType == TeleportType.PTPHERE))) {
                tp.client.sendMessage(ChatColor.RED + tp.target.getName() +
                        " already has a pending teleport request. Try again later.");
                return false; }
        }

        // Check for cross-world and if it's allowed
        if (tp.client.getWorld() != tp.target.getWorld()) {
            if (!plugin.getConfig().getBoolean("teleport." + tp.tpType.name + ".cross-world")) {
                tp.client.sendMessage(ChatColor.RED + "You cannot teleport between worlds! Request canceled.");
                return false;
            }
        }

        int delay = plugin.getConfig().getInt("teleport.request-timer");
        String request = "";
        switch (tp.tpType) {
            case TPA: case PTP:
                request = tp.client.getDisplayName() + " wants to teleport to you."; break;
            case TPAHERE: case PTPHERE:
                request = tp.client.getDisplayName() + " wants you to teleport to them."; break;
            case BACK: case HOME: case SPAWN: case RANDOM: case WARP: default: // TODO Refactor based on new TP types
                plugin.getLogger().warning("Unexpected TeleportType tried to get listed in " +
                        "pending teleport requests!");
                return false;
        }

        tp.target.sendMessage(ChatColor.GOLD + request);
        tp.client.sendMessage(ChatColor.GOLD + "Request sent to " + ChatColor.YELLOW + tp.target.getName());
        pendingRequests.add(tp);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> removeRequest(tp), delay * 20L);
        return true;
    }

    public static void removeRequest(PlayerTeleport tp) { pendingRequests.remove(tp); }

    public static HashSet<PlayerTeleport> retrieveRequest(Player target) {
        HashSet<PlayerTeleport> requests = new HashSet<>();
        for (PlayerTeleport t : pendingRequests)
            if (t.target == target)
                requests.add(t);

        return requests;
    }

    public static HashSet<PlayerTeleport> outgoingRequests(Player player) {
        HashSet<PlayerTeleport> requests = new HashSet<>();
        for (PlayerTeleport t : pendingRequests)
            if (t.client == player)
                requests.add(t);

        return requests;
    }

    private static void activateTp(PlayerTeleport tp) {
        removeRequest(tp);
        teleportTask(tp.client, tp.target, plugin.getConfig().getInt("teleport." + tp.tpType.name + ".timer"));
    }

    private static void teleportTask(Player player, Player target, int timer) {
        // player -> target
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Location location = player.getLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            int counter = timer;
            player.sendMessage(ChatColor.GOLD + "Teleportation commencing in " +
                    ChatColor.DARK_AQUA + counter + ChatColor.GOLD + " seconds! Do not move!");
            if (counter == 0) {
                // teleport client & update lastLocation / teleports / task
                Lemon user = plugin.getUser(player.getUniqueId());
                if (target != null)
                    player.teleport(target.getLocation()); // this is a P2P tp type
                else {
                    player.teleport(user.getLastLocation()); // this is a BACK tp type
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
                    player.sendMessage("§3" + counter + ChatColor.GOLD + " seconds until teleport!");
                } else if (counter <= 5) {
                    player.sendMessage("§3" + counter + ChatColor.GOLD + "!");
                }
            } else {
                // client has moved! cancel task!
                player.sendMessage(ChatColor.RED + "Teleportation canceled!");
                Bukkit.getScheduler().cancelTask(id);
            }
        }, 0L, 20L);
    }
}