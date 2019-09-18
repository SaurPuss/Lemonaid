package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.Date;

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
        if (user.isMuted()) {
            player.sendMessage(Utils.color("c&You are muted! You can't speak until " + new Date(user.getMuteEnd()).toString()));
            e.setCancelled(true);
        }

        // Remove this message from chat for players that have this person ignored
        for (Player p : Bukkit.getOnlinePlayers()) {
            Lemon u = new Lemon(p.getUniqueId()).getUser();
            if (u.isIgnored(player.getUniqueId()))
                e.getRecipients().remove(p);
        }
    }

    @EventHandler
    public void commandEvent(PlayerCommandSendEvent e) {

    }

    @EventHandler
    public void swapItemsEvent(PlayerSwapHandItemsEvent e) {

    }

}
