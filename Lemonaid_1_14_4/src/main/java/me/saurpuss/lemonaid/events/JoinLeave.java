package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Random;

public class JoinLeave implements Listener {
    Lemonaid plugin = Lemonaid.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> list;
        if (!player.hasPlayedBefore()) {
            list = plugin.getConfig().getStringList("motd");
        } else {
            list = plugin.getConfig().getStringList("motd-first");
        }

        Random random = new Random(list.size());
        event.setJoinMessage(list.get(random.nextInt()).replaceAll("%name%", player.getDisplayName()));
    }
}
