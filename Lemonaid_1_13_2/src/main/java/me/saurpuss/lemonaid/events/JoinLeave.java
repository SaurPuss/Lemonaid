package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.commands.social.Welcome;
import me.saurpuss.lemonaid.utils.database.OldMySQL;
import me.saurpuss.lemonaid.utils.users.User;
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
        User user = OldMySQL.getUser(player.getUniqueId());
        plugin.mapPlayer(player.getUniqueId(), user);

        // TODO if player is in air and has fly permission, set them to fly automatically
        // TODO if player has silentjoin or vanishjoin permission set them to vanish

        if (player.hasPlayedBefore()) {
            // Send a message to online moderators about the login event
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.notify")) {
                    p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GOLD + " joined the server!");
                }
            }

            // Welcome to the server MOTD
            List<String> motdList = plugin.getConfig().getStringList("player-motd");
            Random random = new Random(motdList.size());
            String motd = motdList.get(random.nextInt()).replace("%player%", player.getDisplayName());

            player.sendMessage(Utils.color(motd));
        } else {
            // Announce new player
            List<String> announceList = plugin.getConfig().getStringList("first-join-announcements");
            Random random = new Random(announceList.size());
            String announcement = announceList.get(random.nextInt()).replaceAll("%player%", player.getDisplayName());
            e.setJoinMessage(Utils.color(announcement));

            // Set lastJoinPlayer in /welcome
            Welcome.lastJoinedPlayer = e.getPlayer().getDisplayName();

            // TODO add first join items from kit
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        // Save player Lemon and remove player from userManager
        User user = plugin.getUser(player.getUniqueId());
        user.saveUser();
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
        User user = plugin.getUser(player.getUniqueId());
        user.saveUser();
        plugin.unmapPlayer(player.getUniqueId());

        // Send a message to online admins about the quit event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " was kicked from the server!");
            }
        }
    }
}
