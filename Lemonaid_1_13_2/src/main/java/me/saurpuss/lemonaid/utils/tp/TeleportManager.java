package me.saurpuss.lemonaid.utils.tp;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.sql.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;

public class TeleportManager {

    private Lemonaid plugin;
    private static HashSet<Teleport> playerRequests;
    private static HashMap<String, Location> warpManager;

    public TeleportManager(Lemonaid plugin) {
        this.plugin = plugin;
        playerRequests = new HashSet<>();
        warpManager = DatabaseManager.getWarps();
    }

    public static void setWarp(String name, Location location) {
        warpManager.put(name, location);
    }

    public static Location getWarp(String name) {
        return warpManager.get(name);
    }

    public static HashSet<Teleport> retrieveRequest(Player target) {
        // TODO refactor tpDeny

        return new HashSet<>();
    }

    public static HashSet<Teleport> outgoingRequests(Player target) {
        // TODO refactor tpDeny

        return new HashSet<>();
    }

    public static void removeRequest(Teleport tp) {
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

        switch (tp.getTpType()) {
            case TPA:
            case TPAHERE:
            case PTP:
            case PTPHERE:
                addRequest(tp);
                break;
            case BACK:
            case HOME:
            case SPAWN:
            case RANDOM:
            case WARP:
                BukkitRunnable task = new TeleportPlayerTask(plugin, tp);
                task.run();
        }
    }

    /**
     * Add a request to the playerRequests, will remove itself after delay set in config.yml
     * @param tp Teleport with a player to player intent
     */
    void addRequest(Teleport tp) {
        // Make sure this is a player to player request
        if (!tp.getTpType().isP2p()) {
            plugin.getLogger().warning("TeleportToLocationMixup Error!!" +
                    "Please contact the Lemonaid developer!!");
            return;
        }

        // Check for exising requests that might conflict

        // Check if there are no conflicting requests
        for (Teleport t : playerRequests) { // TODO refactor
            // requester already has an outgoing request to this specific player regardless of type
            if (t.getClient() == tp.getClient() && t.getTarget() == tp.getTarget()) {
                // remove existing and break the loop
                playerRequests.remove(t);
                break;
            }
            // requester already has an outgoing request
            // if requester already has incoming requests -> do nothing it will expire if ignored
            if (t.getClient() == tp.getClient() &&
                    (tp.getTpType() == TeleportType.TPA || tp.getTpType() == TeleportType.PTP)) {
                tp.getClient().sendMessage(ChatColor.RED + "You already have an outgoing " +
                        "teleport request pending. Type: " + ChatColor.DARK_AQUA +
                        "/tpacancel" + ChatColor.RED + " remove it.");
                return;
            }
            // target already has an outgoing or incoming request
            if ((t.getClient() == tp.getTarget() && (t.getTpType() == TeleportType.TPA ||
                    t.getTpType() == TeleportType.PTP)) ||
                    (t.getTarget() == tp.getTarget() && (tp.getTpType() == TeleportType.TPAHERE ||
                            tp.getTpType() == TeleportType.PTPHERE))) {
                tp.getClient().sendMessage(ChatColor.RED + tp.getTarget().getName() +
                        " already has a pending teleport request. Try again later.");
                return;
            }
        }

        // Check for cross-world and if it's allowed
        if (tp.getClient().getWorld() != tp.getTarget().getWorld() &&
                !plugin.getConfig().getBoolean("teleport." + tp.getTpType().getName() + ".cross-world")) {
            tp.getClient().sendMessage(ChatColor.RED + "You cannot teleport " +
                    "between worlds! Request canceled.");
            return;
        }

        // align some pretty stuff
        int delay = plugin.getConfig().getInt("teleport." + tp.getTpType().getName() + ".request-timer");
        if (delay == 0) delay = 30;
        String request = tp.getClient().getDisplayName() + " wants to teleport to you.";

        // Adjust if necessary
        switch (tp.getTpType()) {
            case TPAHERE:
            case PTPHERE:
                request = tp.getClient().getDisplayName() + " wants you to teleport to them.";
        }

        // Save request
        playerRequests.add(tp);

        // Confirm request existence to client and target
        tp.getTarget().sendMessage(ChatColor.GOLD + request);
        tp.getClient().sendMessage(ChatColor.GOLD + "Request sent to " + ChatColor.YELLOW + tp.getTarget().getName());

        // schedule removal task
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> playerRequests.remove(tp), delay * 20L);
    }
}
