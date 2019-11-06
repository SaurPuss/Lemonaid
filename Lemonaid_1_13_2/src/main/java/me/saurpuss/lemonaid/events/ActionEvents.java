package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ActionEvents implements Listener {

    private Lemonaid plugin;
    public ActionEvents(Lemonaid plugin) { this.plugin = plugin; }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        // Check if master cuff is true
        if (plugin.isMasterCuff() && !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = plugin.getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void swapItemsEvent(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        // Check if master cuff is true
        if (plugin.isMasterCuff() && !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = plugin.getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        // Check if master cuff is true
        if (plugin.isMasterCuff() && !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = plugin.getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }
    }
}
