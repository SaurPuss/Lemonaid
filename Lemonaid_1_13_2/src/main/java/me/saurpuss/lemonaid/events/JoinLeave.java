package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PermissionMessages;
import me.saurpuss.lemonaid.utils.utility.Styling;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;

public class JoinLeave implements Listener, Styling, PermissionMessages {

    private Lemonaid lemonaid;
    private FileConfiguration config;
    public JoinLeave(Lemonaid plugin) {
        lemonaid = plugin;
        config = lemonaid.getConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());

        if (player.hasPermission("lemonaid.silentjoin")) {
            // TODO set them to vanish
        }

        if (player.hasPermission("lemonaid.fly")) {
            // TODO set them to fly automatically if in the air

        }

        if (player.hasPlayedBefore()) {
            // Send a message to online moderators about the login event
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.notify")) {
                    p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GOLD + " joined the server!");
                }
            }

            // Welcome to the server MOTD
            List<String> motdList = config.getStringList("player-motd");
            Random random = new Random(motdList.size());
            String motd = motdList.get(random.nextInt()).replace("%player%", player.getDisplayName());

            player.sendMessage(color(motd));
        }
        // First time join
        else {
            // Announce new player
            List<String> announceList = config.getStringList("first-join-announcements");
            Random random = new Random(announceList.size());
            String announcement = announceList.get(random.nextInt()).replaceAll("%player%", player.getDisplayName());
            e.setJoinMessage(color(announcement));

            // Set lastJoinPlayer for /welcome
            lemonaid.getUserManager().setLastJoinedPlayer(e.getPlayer().getDisplayName());

            // TODO add first join items from kit
        }

        String notification = ChatColor.YELLOW + player.getDisplayName() + ChatColor.GOLD +
                " joined the server!";
        adminNotification(notification);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        // Save player Lemon and remove player from userManager
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());
        lemonaid.getUserManager().updateUser(user);
        lemonaid.getUserManager().removeUser(user);

        String notification = ChatColor.YELLOW + player.getDisplayName() + ChatColor.GOLD +
                " quit the server!";
        adminNotification(notification);

    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        // Save player Lemon and remove player from userManager
        User user = lemonaid.getUserManager().getUser(player.getUniqueId());
        lemonaid.getUserManager().updateUser(user);
        lemonaid.getUserManager().removeUser(user);

        String notification = ChatColor.YELLOW + player.getDisplayName() + ChatColor.GOLD +
                " was kicked from the server!";
        adminNotification(notification);

    }
}
