package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.Recap;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.JoinLeave;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lemonaid extends JavaPlugin {
    private static Lemonaid instance;

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
        getCommand("recap").setExecutor(new Recap());

        // Util commands
        getCommand("tpa").setExecutor(new Tpa());
        getCommand("tpahere").setExecutor(new TpaHere());
        getCommand("tpaccept").setExecutor(new TpAccept());
        getCommand("tpdeny").setExecutor(new TpDeny()); // also tpacancel

    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new JoinLeave(), this);
    }

    private void registerConfigs() {
        // Default config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Homes config
//        PartiesConfig.setup();
//        PartiesConfig.get().options().copyDefaults(true);
//        PartiesConfig.save();

    }

    // Plugin instance for use in other classes
    private static void setInstance(Lemonaid instance) {
        Lemonaid.instance = instance;
    }
    public static Lemonaid getInstance() {
        return instance;
    }
}
