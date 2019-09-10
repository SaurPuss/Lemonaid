package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.*;
import me.saurpuss.lemonaid.commands.social.*;
import me.saurpuss.lemonaid.commands.social.channels.*;
import me.saurpuss.lemonaid.commands.social.whisper.*;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.JoinLeave;
import me.saurpuss.lemonaid.utils.config.PartiesConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lemonaid extends JavaPlugin {
    private static Lemonaid instance;
    private static Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        setInstance(this);
        // TODO ping DB

        registerConfigs();
        registerCommands();
        registerEvents();
        registerDependencies();

        updateLists();
        // TODO create user wrapper that stores homes, last location, ignored list etc. Get data onJoin etc
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
        getCommand("cuff").setExecutor(new Cuff()); // TODO after mute
        getCommand("mute").setExecutor(new Mute());
        getCommand("recap").setExecutor(new Recap());

        // Social commands
        getCommand("msg").setExecutor(new Msg());
        getCommand("reply").setExecutor(new Reply());
        getCommand("busy").setExecutor(new Busy());
        getCommand("ignore").setExecutor(new Ignore());
        getCommand("localchat").setExecutor(new LocalChat());

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

        // Party make-up config
        PartiesConfig.setup();
        PartiesConfig.get().options().copyDefaults(true);
        PartiesConfig.save();

    }

    // TODO update cuff as a whole
    public static void updateLists() {
        Cuff.cleanCuffList();
    }

    private void registerDependencies() {
        // Set up Vault Economy if available
        if (!setupEconomy()) {
            getLogger().warning("No Vault dependency found! Disabling related functionality!");
            economy = null;
        }

        // Set up WorldGuard if available
        if (true) {
            getCommand("jail").setExecutor(new Jail());
        } else {
            getLogger().warning("No World Guard dependency found! Disabling related functionality!");
        }

    }

    public static Economy getEconomy() { return economy; }

    private static void setInstance(Lemonaid instance) { Lemonaid.instance = instance; }
    public static Lemonaid getInstance() { return instance; }

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
}
