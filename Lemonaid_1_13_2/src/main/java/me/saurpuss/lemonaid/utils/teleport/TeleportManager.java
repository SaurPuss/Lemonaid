package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.sql.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Managing class to keep track of player to player teleport request,
 * initiate teleport tasks and manage public warps.
 */
public class TeleportManager {

    /**
     * Dependency injection of the plugin's current runtime.
     */
    private Lemonaid plugin;

    /**
     * Static Set that keeps track of current pending player to player
     * teleportation requests in the form of Teleport objects.
     */
    private static HashSet<Teleport> playerRequests;

    /**
     * Static Map that keeps track of the current name and location
     * of public warps.
     */
    private static HashMap<String, Location> warpManager;

    /**
     * Teleport Manager constructor initialized in Main Lemonaid class.
     * @param plugin Dependency injection of the runtime instance of the
     *               Lemonaid plugin.
     */
    public TeleportManager(Lemonaid plugin) {
        this.plugin = plugin;
        playerRequests = new HashSet<>();
        warpManager = DatabaseManager.getWarps();
    }

    /**
     * Add a public warp to the warpManager and save or update a copy
     * in the MySQL database.
     * @param name Warp name, converted to lowercase to prevent duplicates.
     * @param location In-game location set as the warp destination.
     */
    public void setWarp(String name, Location location) {
        warpManager.put(name.toLowerCase(), location);
        // TODO add to DB
    }

    /**
     * Retrieve a public warp destination.
     * @param name Name of the warp to look up in the warpManager.
     * @return Location destination that corresponds with the request input.
     */
    public Location getWarp(String name) {
        return warpManager.get(name.toLowerCase());
    }

    /**
     * Delete an existing warp name and destination from the warpManager
     * and the MySQL database.
     * @param name Name of the warp to be deleted.
     */
    public void deleteWarp(String name) {
        warpManager.remove(name.toLowerCase());
        // TODO remove from DB
    }

    /**
     * Retrieve a HashSet of Teleports that have a client that matches
     * the given player object.
     * @param target Requesting player to match to the tpClient.
     * @return Set of Teleport objects that contain the input target.
     */
    public HashSet<Teleport> retrieveRequest(Player target) {
        // TODO refactor tpDeny

        return new HashSet<>();
    }

    /**
     * Retrieve a HashSet of Teleports that have a target that matches
     * the given player object.
     * @param target Requesting player to match tot the tpTarget
     * @return Set of Teleport objects that contain the input target.
     */
    public HashSet<Teleport> outgoingRequests(Player target) {
        // TODO refactor tpDeny

        return new HashSet<>();
    }

    /**
     * Manually remove a pending Teleport from playerRequests.
     * @param tp Teleport object to remove
     */
    public void removeRequest(Teleport tp) {
        playerRequests.remove(tp);
    }

    /**
     * Teleportation event start. If Vault is active withdraw from balance
     * and initiate requested tp.
     *
     * @param tp Teleportation request object
     */
    public void teleportEvent(Teleport tp) {
        Economy economy = plugin.getEconomy();
        if (economy.isEnabled()) {
            double cost = plugin.getConfig().getDouble("teleport." + tp.getTpType().getName() + ".cost");

            // Attempt to charge the client for the teleport
            EconomyResponse response = economy.withdrawPlayer(tp.getClient(), cost);
            if (!response.transactionSuccess()) {
                tp.getClient().sendMessage(ChatColor.RED + "Balance too low! " +
                        "Teleportation request canceled!");
                return;
            }
        }

        if (tp.getTpType().isP2p() && !playerRequests.contains(tp)) {
            tp.getClient().sendMessage(ChatColor.RED + "Your teleport request has expired!");
            return;
        }

        BukkitTask tpTask = new TeleportTask(plugin, tp).runTaskTimer(plugin, 0, 20L);
    }

    /**
     * Add a request to the playerRequests, will remove itself after delay set in the Lemonaid
     * config.yml. Checks if there is a pre-existing request stored that conflicts with the
     * new Teleport, or if there is a cross-world conflict.
     * If Teleport is a valid instance it will schedule an automatic removal from the
     * playerRequest HashSet with a the Bukkit Scheduler to be activated upon expiration defined
     * inside the Lemonaid config.yml.
     * @param tp Teleport with a player to player intent
     */
    void addRequest(Teleport tp) {
        // Make sure this is a player to player request
        if (!tp.getTpType().isP2p()) {
            plugin.getLogger().warning("Error TeleportToLocationMixup! " +
                    "More info in plugin readme.txt!");
            return;
        }

        // Check if there are no conflicting requests
        Player tpClient = tp.getClient();
        Player tpTarget = tp.getTarget();
        for (Teleport t : playerRequests) {
            // requester already has an outgoing request to this specific player regardless of type
            if (t.getClient() == tpClient && t.getTarget() == tpTarget) {
                // remove existing and try to add new below
                playerRequests.remove(t);
                break;
            }
            // requester already has an outgoing request to tp to another player
            if (t.getClient() == tpClient &&
                    (t.getTpType() == TeleportType.TPA || t.getTpType() == TeleportType.PTP)) {
                // give warning and abort
                tpClient.sendMessage(ChatColor.RED + "You already have an outgoing " +
                        "teleport request to another player pending. " +
                        "Type: " + ChatColor.DARK_AQUA + "/tpacancel" +
                        ChatColor.RED + " remove it, or wait for it to expire.");
                return;
            }
            if ((t.getClient() == tpTarget && // target has an outgoing request to tp to a player
                    (t.getTpType() == TeleportType.TPA || t.getTpType() == TeleportType.PTP)) ||
                (t.getTarget() == tpTarget && // target needs to handle existing incoming request first
                    (tp.getTpType() == TeleportType.TPAHERE || tp.getTpType() == TeleportType.PTPHERE))) {
                // give requester a warning to try again later and abort
                tpClient.sendMessage(ChatColor.RED + tp.getTarget().getName() +
                        " already has a pending teleport request. Try again later.");
                tpTarget.sendMessage(ChatColor.YELLOW + tpClient.getName() + " tried to send you a " +
                        "teleport request, but you have conflicting request pending." );
                return;
            }
        }

        // Check for cross-world and if it's allowed
        if (tpClient.getWorld() != tpTarget.getWorld() &&
                !plugin.getConfig().getBoolean("teleport." + tp.getTpType().getName() + ".cross-world")) {
            tpClient.sendMessage(ChatColor.RED + "You cannot teleport " +
                    "between worlds! Request canceled.");
            return;
        }

        // align some pretty stuff
        int delay = plugin.getConfig().getInt("teleport." + tp.getTpType().getName() + ".request-timer");
        if (delay == 0) delay = 30;
        String request = tpClient.getName() + " wants to teleport to you.";

        // Adjust if necessary
        switch (tp.getTpType()) {
            case TPAHERE:
            case PTPHERE:
                request = tpClient.getName() + " wants you to teleport to them.";
        }

        // Save request
        playerRequests.add(tp);

        // Confirm request existence to client and target
        tpTarget.sendMessage(ChatColor.GOLD + request);
        tpClient.sendMessage(ChatColor.GOLD + "Request sent to " + ChatColor.YELLOW + tpTarget.getName());

        // schedule removal task
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            tpClient.sendMessage(ChatColor.RED + "Teleport request expired!");
            playerRequests.remove(tp);
        }, delay * 20L);
    }
}