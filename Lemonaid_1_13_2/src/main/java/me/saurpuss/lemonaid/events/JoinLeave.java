package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.sql.DatabaseManager;
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
        Lemon user = DatabaseManager.getUser(player.getUniqueId());
        plugin.mapPlayer(player.getUniqueId(), user);

        if (player.hasPlayedBefore()) {
            // Welcome to the server MOTD
            List<String> motdList = plugin.getConfig().getStringList("message-of-the-day");
            Random random = new Random(motdList.size());
            String motd = motdList.get(random.nextInt()).replace("%player%", player.getDisplayName());

            player.sendMessage(Utils.color(motd));
        } else {
            // Announce new player
            List<String> announceList = plugin.getConfig().getStringList("first-join-announcements");
            Random random = new Random(announceList.size());
            String announcement = announceList.get(random.nextInt()).replaceAll("%player%", player.getDisplayName());
            e.setJoinMessage(Utils.color(announcement));

            // TODO add first join items from kit
        }



        // Send a message to online moderators about the login event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GOLD + " joined the server!");
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
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GOLD +" quit the server!");
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
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " was kicked from the server!");
            }
        }
    }
}
