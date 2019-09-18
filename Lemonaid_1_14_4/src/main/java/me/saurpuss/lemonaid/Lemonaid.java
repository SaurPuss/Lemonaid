package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.Broadcast;
import me.saurpuss.lemonaid.commands.admin.Fly;
import me.saurpuss.lemonaid.commands.admin.Mute;
import me.saurpuss.lemonaid.commands.admin.Recap;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.ChatEvents;
import me.saurpuss.lemonaid.events.JoinLeave;
import me.saurpuss.lemonaid.utils.config.HomesConfig;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Lemonaid extends JavaPlugin {
    private static Lemonaid instance;
    private HashMap<UUID, Lemon> userManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        setInstance(this);

        registerConfigs();
        registerCommands();
        registerEvents();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        getLogger().info(Utils.console("Plugin shutdown"));
    }

    private void registerCommands() {
        // Admin level command
        getCommand("fly").setExecutor(new Fly());
        getCommand("recap").setExecutor(new Recap(this));
        getCommand("mute").setExecutor(new Mute(this));
        getCommand("broadcast").setExecutor(new Broadcast());

        // Util commands
        getCommand("tpa").setExecutor(new Tpa());
        getCommand("tpahere").setExecutor(new TpaHere());
        getCommand("tpaccept").setExecutor(new TpAccept());
        getCommand("tpdeny").setExecutor(new TpDeny()); // also tpacancel
        getCommand("home").setExecutor(new Home());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new JoinLeave(), this);
        pm.registerEvents(new ChatEvents(), this);
    }

    private void registerConfigs() {
        // Default config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Homes config
        HomesConfig.setup();
        HomesConfig.get().options().copyDefaults(true);
        HomesConfig.save();

    }

    // Keep track of Lemons
    public void mapPlayer(UUID uuid, Lemon user) { userManager.put(uuid, user); }
    public void unmapPlayer(UUID uuid) { userManager.remove(uuid); }
    public Lemon getUser(UUID uuid) { return userManager.get(uuid); }

    // Plugin instance for use in other classes
    private static void setInstance(Lemonaid instance) {
        Lemonaid.instance = instance;
    }
    public static Lemonaid getInstance() {
        return instance;
    }
}
