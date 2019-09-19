package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class ChatModeration implements Listener {
    private Lemonaid plugin;
    public ChatModeration(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void channelEvent(PlayerChannelEvent e) {
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (Lemonaid.isMasterCuff()) {
            if (!player.hasPermission("lemonaid.admin.mastercuff") ||
                    !player.hasPermission("lemonaid.admin.notify.mastercuff")) {
                e.setCancelled(true);
                return;
            }
        }
        if (Lemonaid.isGlobalMute()) {
            if (!player.hasPermission("lemonaid.admin.globalmute") ||
                    !player.hasPermission("lemonaid.admin.notify.globalmute")) {
                e.setCancelled(true);
                return;
            }
        }

        Lemon user = new Lemon(player.getUniqueId()).getUser();
        // This player is not allowed to talk
        if (user.isMuted() || user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
            return;
        }

        // Remove this message from chat for players that have this person ignored
        for (Player p : Bukkit.getOnlinePlayers()) {
            Lemon u = new Lemon(p.getUniqueId()).getUser();
            if (u.isIgnored(player.getUniqueId()))
                e.getRecipients().remove(p);
        }

        // Translate chat colors for those with the permission
        if (player.hasPermission("lemonaid.chat.colors")) {
            String message = e.getMessage();
            e.setMessage(Utils.color(message));
        }
    }

    @EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (Lemonaid.isMasterCuff()) {
            if (!player.hasPermission("lemonaid.admin.mastercuff") ||
                    !player.hasPermission("lemonaid.admin.notify.mastercuff")) {
                e.setCancelled(true);
                return;
            }
        }
        if (Lemonaid.isGlobalMute()) {
            if (!player.hasPermission("lemonaid.admin.globalmute") ||
                    !player.hasPermission("lemonaid.admin.notify.globalmute")) {
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
