package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.commands.admin.Mute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class ChatModeration implements Listener {

    @EventHandler
    public void channelEvent(PlayerChannelEvent e) {

    }

    public void chatEvent(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (Mute.activeMutes.contains(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void commandEvent(PlayerCommandSendEvent e) {

    }

    @EventHandler
    public void swapItemsEvent(PlayerSwapHandItemsEvent e) {

    }

}
