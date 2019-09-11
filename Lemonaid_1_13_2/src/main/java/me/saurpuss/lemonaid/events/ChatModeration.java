package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.players.Lemon;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

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
        Lemon user = Lemon.getUser(player.getUniqueId());

        if (user.isMuted()) {
            player.sendMessage(Utils.color("&cYou do not have permission to talk right now!"));
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
