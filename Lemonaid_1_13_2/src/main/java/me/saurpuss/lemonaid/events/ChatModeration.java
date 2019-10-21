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
    public void chatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        // Check if global mute or master cuff is true
        if ((plugin.isMasterCuff() || plugin.isGlobalMute()) &&
                !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        Lemon user = plugin.getUser(player.getUniqueId());
        // This player is not allowed to talk
        if (user.isMuted() || user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
            return;
        }

        // Remove this message from chat for players that have this person ignored
        // Don't do this for players with the ignore exempt permission, unless they are
        // allowed to ignore in the config
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("lemonaid.ignoreexempt") &&
                    !plugin.getConfig().getBoolean("moderator-allow-ignore")) {
                break;
            }

            if (!p.hasPermission("lemonaid.ignoreexempt")) {
                Lemon u = new Lemon(p.getUniqueId()).getUser();
                if (u.isIgnored(player.getUniqueId()))
                    e.getRecipients().remove(p);
            }
        }

        // Translate chat colors for those with the permission
        if (player.hasPermission("lemonaid.chat.color")) {
            String message = e.getMessage();
            e.setMessage(Utils.color(message));
        }
    }

    @EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        // Check if global mute or master cuff is true
        if ((plugin.isMasterCuff() || plugin.isGlobalMute()) &&
                !player.hasPermission("lemonaid.exempt")) {
            e.setCancelled(true);
            return;
        }

        // Get player from userManager
        Lemon user = plugin.getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }
    }
}
