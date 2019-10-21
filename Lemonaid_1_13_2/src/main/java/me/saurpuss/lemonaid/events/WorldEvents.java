package me.saurpuss.lemonaid.events;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldEvents implements Listener {

    private Lemonaid plugin;
    public WorldEvents(Lemonaid plugin) { this.plugin = plugin; }

    @EventHandler
    public void worldSave(WorldSaveEvent e) {
        plugin.saveUserManager();
    }
}
