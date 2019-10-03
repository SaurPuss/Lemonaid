package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.sql.MySQLDatabase;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.util.*;

public class JoinLeave implements Listener {

    private Lemonaid plugin;
    public JoinLeave(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Lemon user;

        if (player.hasPlayedBefore()) {
            // Get Lemon from the database TODO create new record if no existing one is found
            user = MySQLDatabase.getUser(player.getUniqueId());

            // Welcome to the server MOTD
            List<String> motdList = plugin.getConfig().getStringList("message-of-the-day");
            Random random = new Random(motdList.size());
            String motd = motdList.get(random.nextInt()).replace("%player%", player.getDisplayName());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', motd));

        } else {
            // Create a new Lemon for the database
            user = new Lemon(player.getUniqueId());

            // Announce new player
            List<String> announceList = plugin.getConfig().getStringList("first-join-announcements");
            Random random = new Random(announceList.size());
            String announcement = announceList.get(random.nextInt()).replaceAll("%player%", player.getDisplayName());
            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', announcement));


           // TODO add first join items from kit
        }

        // Map the Lemon to userManager for this session
        plugin.mapPlayer(player.getUniqueId(), user);

        // Send a message to online moderators about the login event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage("§e" + player.getName() + " §6joined the server!");
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        // Save player Lemon and remove player from userManager
        Lemon user = plugin.getUser(player.getUniqueId());
        user.updateUser();
        plugin.unmapPlayer(player.getUniqueId());

        // Send a message to online admins about the quit event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage("§e" + player.getName() + " §6quit the server!");
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        // Save player Lemon and remove player from userManager
        Lemon user = plugin.getUser(player.getUniqueId());
        user.updateUser();
        plugin.unmapPlayer(player.getUniqueId());

        // Send a message to online admins about the quit event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage("§e" + player.getName() + " §cwas kicked from the server!");
            }
        }
    }
}
