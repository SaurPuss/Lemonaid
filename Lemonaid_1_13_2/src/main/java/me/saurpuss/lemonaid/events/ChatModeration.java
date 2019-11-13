package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PermissionMessages;
import me.saurpuss.lemonaid.utils.utility.Styling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class ChatModeration implements Listener, Styling, PermissionMessages {
    private Lemonaid plugin;
    public ChatModeration(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        // Check if global mute or master cuff is true
        if ((plugin.isMasterCuff() || plugin.isGlobalMute()) &&
                !player.hasPermission("lemonaid.exempt")) {
            event.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = plugin.getUserManager().getUser(player.getUniqueId());
        // This player is not allowed to talk
        if (user.isMuted() || user.isCuffed()) {
            player.sendMessage(noPermission());
            event.setCancelled(true);
            return;
        }

        // Remove this message from chat for players that have this person ignored
        // Don't do this for players with the ignore exempt permission, unless they are
        // allowed to ignore in the config
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("lemonaid.ignoreexempt")) {
                // TODO double check this
                User u = plugin.getUserManager().getUser(p.getUniqueId());
                if (u.isIgnored(player.getUniqueId()))
                    event.getRecipients().remove(p);
            } else {
                if (!plugin.getConfig().getBoolean("moderator-allow-ignore"))
                    break;
            }
        }

        // Translate chat colors for those with the permission
        if (player.hasPermission("lemonaid.chat.color")) {
            String message = event.getMessage();
            event.setMessage(color(message));
        }
    }

    @EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        // Check if global mute or master cuff is true
        if ((plugin.isMasterCuff() || plugin.isGlobalMute()) &&
                !player.hasPermission("lemonaid.exempt")) {
            event.setCancelled(true);
            return;
        }

        // Get player from userManager
        User user = plugin.getUserManager().getUser(player.getUniqueId());
        if (user.isCuffed()) {
            player.sendMessage(noPermission());
            event.setCancelled(true);
        }
    }
}
