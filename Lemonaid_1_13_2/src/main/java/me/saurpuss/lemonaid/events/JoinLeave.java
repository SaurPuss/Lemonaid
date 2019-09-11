package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.players.Lemon;
import me.saurpuss.lemonaid.utils.util.Utils;
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
        plugin.mapPlayer(player.getUniqueId(), Lemon.getUser(player.getUniqueId()));





        // TODO add to wrapper in map and retrieve custom attributes

        if (player.hasPlayedBefore()) {
            // Welcome to the server MOTD
            List<String> motdList = plugin.getConfig().getStringList("message-of-the-day");
            Random random = new Random(motdList.size());
            String motd = motdList.get(random.nextInt()).replace("%player%", player.getDisplayName());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', motd));
        } else {
            // Announce new player
            List<String> announceList = plugin.getConfig().getStringList("first-join-announcements");
            Random random = new Random(announceList.size());
            String announcement = announceList.get(random.nextInt()).replaceAll("%player%", player.getDisplayName());
            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', announcement));

           // TODO add first join items from kit
        }

        // Send a message to online admins about the login event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.admin.notify.login")) {
                p.sendMessage(Utils.color(player.getName() + " joined the server."));
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
            if (p.hasPermission("lemonaid.admin.notify.logout")) {
                p.sendMessage(Utils.color(player.getName() + " quit the server."));
            }
        }


        // TODO add msg hashmap remove entry
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        Lemon user = plugin.getUser(player.getUniqueId());
        user.updateUser();
        plugin.unmapPlayer(player.getUniqueId());

        // Send a message to online admins about the quit event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.admin.notify.kick")) {
                p.sendMessage(Utils.color(player.getName() + " was kicked from the server."));
            }
        }
    }
}
