package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.Broadcast;
import me.saurpuss.lemonaid.commands.admin.Fly;
import me.saurpuss.lemonaid.commands.social.channels.LocalChat;
import me.saurpuss.lemonaid.commands.social.whisper.Msg;
import me.saurpuss.lemonaid.commands.teleportation.*;
import me.saurpuss.lemonaid.events.OnJoin;
import me.saurpuss.lemonaid.events.OnQuit;
import me.saurpuss.lemonaid.utils.config.PartiesConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lemonaid extends JavaPlugin {
    private static Lemonaid instance;
    private static Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        setInstance(this);

        registerConfigs();
        registerCommands();
        registerEvents();
        registerDependencies();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        getLogger().info(Utils.console("Plugin shutdown"));
    }

    private void registerCommands() {
        // Admin level command
        getCommand("fly").setExecutor(new Fly());
        getCommand("broadcast").setExecutor(new Broadcast());

        // Social commands
        getCommand("msg").setExecutor(new Msg());
        getCommand("localchat").setExecutor(new LocalChat());

        // Util commands
        getCommand("tpa").setExecutor(new Tpa());
        getCommand("tpahere").setExecutor(new TpaHere());
        getCommand("tpaccept").setExecutor(new TpAccept());
        getCommand("tpdeny").setExecutor(new TpDeny()); // also tpacancel

    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new OnJoin(), this);
        pm.registerEvents(new OnQuit(), this);
    }

    private void registerConfigs() {
        // Default config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Party make-up config
        PartiesConfig.setup();
        PartiesConfig.get().options().copyDefaults(true);
        PartiesConfig.save();

    }

    private void registerDependencies() {
        // Set up Vault Economy if available
        if (!setupEconomy()) {
            getLogger().warning("No Vault dependency found! Disabling related functionality!");
            economy = null;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }

    // Plugin instance for use in other classes
    private static void setInstance(Lemonaid instance) {
        Lemonaid.instance = instance;
    }

    public static Lemonaid getInstance() {
        return instance;
    }
}
