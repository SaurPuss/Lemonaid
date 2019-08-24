package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

    Lemonaid plugin = Lemonaid.getInstance();

    @EventHandler
    public void messageOfTheDay(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // TODO grab message of the day or first join options from config
        e.setJoinMessage(Utils.announce("Insert message from config.yml"));
        player.sendMessage(Utils.chat("Insert message from config.yml"));



        // TODO add admin join notification
    }
}
