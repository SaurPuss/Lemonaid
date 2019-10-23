package me.saurpuss.lemonaid.utils.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private Lemonaid plugin;
    private Teleport tp;
    private int timer;
    private boolean cancelCountdown;

    TeleportTask(Lemonaid pl, Teleport t) {
        plugin = pl;
        tp = t;
        timer = plugin.getConfig().getInt("teleport." + tp.getTpType().getName() + ".timer");
        cancelCountdown = this.plugin.getConfig().getBoolean("teleport.cancel-countdown");
        if (timer < 1) timer = 1;
    }

    @Override
    public void run() {
        // Set location and player variables
        Location destination = null;
        Player player = tp.getClient(); // Target player to teleport
        switch (tp.getTpType()) {
            case TPA:
            case PTP:
                destination = tp.getTarget().getLocation();
                break;
            case TPAHERE:
            case PTPHERE:
                player = tp.getTarget(); // Update player
                destination = tp.getClient().getLocation();
                break;
            case BACK:
            case WARP:
            case HOME:
            case RANDOM:
            case SPAWN:
                destination = tp.getLocation();
        }

        Location origin = player.getLocation();
        int x = origin.getBlockX();
        int y = origin.getBlockY();
        int z = origin.getBlockZ();
        player.sendMessage(ChatColor.GOLD + "Teleportation commencing in " +
                ChatColor.DARK_AQUA + timer + ChatColor.GOLD + " seconds!" +
                (cancelCountdown ? " Do not move!" : ""));
        // Countdown is unfinished
        if (timer > 0) {
            // Client is not allowed to move during countdown
            if (cancelCountdown &&
                    (x != tp.getClient().getLocation().getBlockX() &&
                     y != tp.getClient().getLocation().getBlockY() &&
                     z != tp.getClient().getLocation().getBlockZ())) {
                // client has moved! cancel task!
                player.sendMessage(ChatColor.RED + "Teleportation canceled!");
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
            Lemon user = plugin.getUser(player.getUniqueId());
            user.setLastLocation(origin);
            user.updateUser();
            // Can't perform if we don't know where to go
            if (destination != null) player.teleport(destination);
            else plugin.getLogger().warning("Error DestinationUnknown! More info in plugin readme.txt!");
            this.cancel();
        }
    }
}
