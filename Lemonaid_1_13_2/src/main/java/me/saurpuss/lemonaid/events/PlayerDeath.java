package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    private Lemonaid lemonaid;
    public PlayerDeath(Lemonaid plugin) {
        lemonaid = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Set last location to death location
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());
        user.setLastLocation(player.getLocation());
        lemonaid.getUserManager().updateUser(user);
        
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Use " + ChatColor.DARK_PURPLE +
                "/back" + ChatColor.LIGHT_PURPLE + " to return to the place of your death.");


    }
}
