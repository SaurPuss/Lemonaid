package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PermissionMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ActionEvents implements Listener, PermissionMessages {

    private Lemonaid lemonaid;
    public ActionEvents(Lemonaid plugin) {
        lemonaid = plugin;
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        // Check if master cuff is true
        if (lemonaid.isMasterCuff() && !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(noPermission());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void swapItemsEvent(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        // Check if master cuff is true
        if (lemonaid.isMasterCuff() && !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(noPermission());
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        // Check if master cuff is true
        if (lemonaid.isMasterCuff() && !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(noPermission());
            e.setCancelled(true);
        }
    }
}
