package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatEvents implements Listener {

    Lemonaid plugin = Lemonaid.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        boolean automute = plugin.getConfig().getBoolean("monitor-bad-words");
        boolean isMute = false;


        // TODO turn this into auto mute
        if (automute) {
            autoMute(player, message);
        }

        if (isMute) {
            event.setCancelled(true);
        }


        // TODO control mute and cooldown here
        //  (https://www.youtube.com/watch?v=1n6oCFdZ_Yk&list=PLdnyVeMcpY79SNiA3tCvgTQh0xDzyOSZ5&index=2)
    }


    private void autoMute(Player player, String message) {
        int incidents = plugin.getConfig().getInt("incidents." + player.getUniqueId().toString());
        List<String> badList = plugin.getConfig().getStringList("bad-words");

        for (String word : badList) {
            if (message.toLowerCase().contains(word)) {
                if (incidents != 0) {
                    incidents++;
                    player.setFireTicks(20);
                    plugin.getConfig().set("incidents." + player.getUniqueId().toString(), incidents);
                    plugin.saveConfig();
                } else {
                    player.setFireTicks(10);
                    plugin.getConfig().set("incidents." + player.getUniqueId().toString(), 1);
                    plugin.saveConfig();
                }
            }
        }

    }
}
