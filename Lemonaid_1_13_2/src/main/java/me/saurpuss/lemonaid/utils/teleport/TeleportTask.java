package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Custom BukkitTask to handle Teleportation events for players.
 */
public class TeleportTask extends BukkitRunnable {

    /**
     * Dependency injection of the current plugin instance.
     */
    private Lemonaid lemonaid;

    /**
     * Teleport object with the information needed to execute the
     * teleportation event on the client or target.
     */
    private Teleport tp;

    /**
     * Countdown amount that determines how many seconds a task needs
     * to repeat before initiating the actual teleport on the player.
     */
    private int timer;

    /**
     * Boolean set in the Lemonaid config.yml, if set to true the task
     * will compare the player's current x, y, and z coordinates to the
     * ones the player is on at the start of the task.
     */
    private boolean cancelCountdown;

    /**
     * Task object to activate for Teleportation execution.
     * @param plugin Dependency injection of the current Lemonaid instance.
     * @param t Teleport object containing the required teleportation
     *          information.
     */
    TeleportTask(Lemonaid plugin, Teleport t) {
        lemonaid = plugin;
        tp = t;
        timer = lemonaid.getTeleportManager().getTimer(t);
        cancelCountdown = lemonaid.getTeleportManager().getCancelCooldown();
        if (timer < 1) timer = 1; // fallback
    }

    /**
     * Implemention of self canceling TeleportTask.
     */
    @Override
    public void run() {
        // Set location and player variables
        Location destination = null;
        Player player = tp.getClient(); // Target player to teleport
        switch (tp.getTpType()) {
            // client to target
            case TPA:
            case PTP:
                destination = tp.getTarget().getLocation();
                break;
            // target to client
            case TPAHERE:
            case PTPHERE:
                player = tp.getTarget(); // Update player
                destination = tp.getClient().getLocation();
                break;
            // client to location
            case BACK:
            case WARP:
            case HOME:
            case RANDOM:
            case SPAWN:
                destination = tp.getLocation();
        }

        // Get the location of the player to be teleported
        Location origin = player.getLocation();
        int x = origin.getBlockX();
        int y = origin.getBlockY();
        int z = origin.getBlockZ();

        // Let the player know the teleport countdown is starting (and not to
        // move if cancelCountdown is true
        player.sendMessage(ChatColor.GOLD + "Teleportation commencing in " +
                ChatColor.DARK_AQUA + timer + ChatColor.GOLD + " seconds!" +
                (cancelCountdown ? " Do not move!" : ""));
        // Countdown is unfinished
        if (timer > 0) {
            // Client is not allowed to move during countdown
            if (cancelCountdown &&
                    (x != player.getLocation().getBlockX() &&
                     y != player.getLocation().getBlockY() &&
                     z != player.getLocation().getBlockZ())) {
                // client has moved! cancel task!
                player.sendMessage(ChatColor.RED + "Teleportation canceled!");
                // TODO refund?
                this.cancel();
            }
            // Countdown alerts
            else if (timer == 10) {
                player.sendMessage("§3" + timer + ChatColor.GOLD + " seconds until teleport!");
            } else if (timer <= 5) {
                player.sendMessage("§3" + timer + ChatColor.GOLD + "!");
            }
            timer--;
        }
        // Successful completion of task
        else {
            // Save player data in Lemon wrapper
            User user = lemonaid.getUserManager().getUser(player.getUniqueId());
            user.setLastLocation(origin);
            lemonaid.getUserManager().updateUser(user);
            // Can't perform if we don't know where to go
            if (destination != null)
                player.teleport(destination);
            else
                lemonaid.getLogger().warning("Error DestinationUnknown! More info in plugin " +
                        "readme.txt!");
            // Finish the task and remove from scheduler
            this.cancel();
        }
    }
}
