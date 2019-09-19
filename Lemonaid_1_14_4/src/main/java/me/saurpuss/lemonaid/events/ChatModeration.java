package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


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
    }

    @EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        Lemon user = new Lemon(player.getUniqueId()).getUser();

        if (user.isCuffed()) {
            player.sendMessage(Utils.noPermission());
            e.setCancelled(true);
        }

    }



}
