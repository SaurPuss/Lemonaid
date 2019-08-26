package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OnJoin implements Listener {

    Lemonaid plugin = Lemonaid.getInstance();

    @EventHandler
    public void messageOfTheDay(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPlayedBefore()) {
            // Welcome to the server MOTD
            List<String> motdList = plugin.getConfig().getStringList("message-of-the-day");
            Random random = new Random(motdList.size());
            String motd = motdList.get(random.nextInt());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', motd));
        } else {
            // Announce new player
            List<String> annnounceList = plugin.getConfig().getStringList("first-join-announcements");
            Random random = new Random(annnounceList.size());
            String announcement = annnounceList.get(random.nextInt());
            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', announcement));

            // Check how many first join items are in the config
            List<String> itemList = plugin.getConfig().getStringList("first-join-items");

            for (String i : itemList) {
                // Get the data from the config
                String itemName = plugin.getConfig().getString("first-join-items.item");
                int itemSlot = plugin.getConfig().getInt("first-join-items.item-slot");
                int itemAmount = plugin.getConfig().getInt("first-join-items.item-amount");
                String itemTitle = plugin.getConfig().getString("first-join-items.item-title");
                List<String> itemDescription = plugin.getConfig().getStringList("first-join-items.item-description");

                ItemStack item = new ItemStack(Material.getMaterial(itemName), itemAmount);
                if (item != null) {
                    ItemMeta meta = item.getItemMeta();

                    meta.setDisplayName(itemTitle);
                    ArrayList<String> lore = new ArrayList<>();
                    for (String s : itemDescription) {
                        lore.add(s);
                    }
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    item.setItemMeta(meta);

                    player.getInventory().setItem(itemSlot, item);
                }
            }
        }

        // Send a message to online admins about the login event
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.admin.notify.login")) {
                p.sendMessage(Utils.admin(player.getName() + " joined the server."));
            }
        }

    }
}
