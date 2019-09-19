package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ActionEvents implements Listener {

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (Lemonaid.isMasterCuff()) {
            if (!player.hasPermission("lemonaid.admin.mastercuff") ||
                    !player.hasPermission("lemonaid.admin.notify.mastercuff")) {
                e.setCancelled(true);
                return;
            }
        }


        Lemon user = new Lemon(player.getUniqueId()).getUser();
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void swapItemsEvent(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        if (Lemonaid.isMasterCuff()) {
            if (!player.hasPermission("lemonaid.admin.mastercuff") ||
                    !player.hasPermission("lemonaid.admin.notify.mastercuff")) {
                e.setCancelled(true);
                return;
            }
        }

        Lemon user = new Lemon(player.getUniqueId()).getUser();
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (Lemonaid.isMasterCuff()) {
            if (!player.hasPermission("lemonaid.admin.mastercuff") ||
                    !player.hasPermission("lemonaid.admin.notify.mastercuff")) {
                e.setCancelled(true);
                return;
            }
        }

        Lemon user = new Lemon(player.getUniqueId()).getUser();
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }
    }
}
