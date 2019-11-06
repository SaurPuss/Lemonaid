package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.*;
import me.saurpuss.lemonaid.commands.admin.moderation.*;
import me.saurpuss.lemonaid.commands.social.*;
import me.saurpuss.lemonaid.commands.social.channels.*;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.*;
import me.saurpuss.lemonaid.utils.database.LogManager;
import me.saurpuss.lemonaid.utils.teleport.TeleportManager;
import me.saurpuss.lemonaid.utils.users.UserManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lemonaid extends JavaPlugin {

    // This
    public static Lemonaid instance;

    // Plugin Managers
    private static UserManager userManager;
    private static TeleportManager teleportManager;
    private static LogManager logManager;

    // Moderation settings
    private volatile boolean masterCuff = false;
    private volatile boolean globalMute = false;

    // Dependencies
    private static Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        instance = this;

        // Managers
        userManager = new UserManager(this);
        teleportManager = new TeleportManager(this);
        logManager = new LogManager(this);

        // TODO test connection to DB and set up tables etc

        // Register elements
        registerConfigs();
        registerCommands();
        registerEvents();
        registerDependencies();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        getLogger().info(Utils.console("Plugin shutdown"));

        // Save all remaining Lemons to DB & Delete any listed homes or ignores mapped from DB
        userManager.saveUserManager();
        teleportManager.saveWarpManager();
    }

    private void registerCommands() {
        // Admin level command
        getCommand("fly").setExecutor(new Fly());
        getCommand("broadcast").setExecutor(new Broadcast());
        getCommand("recap").setExecutor(new Recap(this));
        getCommand("mute").setExecutor(new Mute(this));
        getCommand("cuff").setExecutor(new Cuff(this));
        getCommand("mastercuff").setExecutor(new MasterCuff(this));
        getCommand("globalmute").setExecutor(new GlobalMute(this));
        getCommand("forceignore").setExecutor(new ForceIgnore(this));

        // Social commands
        getCommand("msg").setExecutor(new Msg(this));
        getCommand("reply").setExecutor(new Reply(this));
        getCommand("busy").setExecutor(new Busy(this));
        getCommand("ignore").setExecutor(new Ignore(this));

        getCommand("localchat").setExecutor(new LocalChat(this));

        // Player to player Teleport commands
        getCommand("tpa").setExecutor(new Tpa(this));
        getCommand("tpahere").setExecutor(new TpaHere(this));
        getCommand("tpaccept").setExecutor(new TpAccept(this));
        getCommand("tpdeny").setExecutor(new TpDeny(this)); // also tpacancel
        // Player to location Teleport commands
        getCommand("back").setExecutor(new Back(this));
        getCommand("home").setExecutor(new Home(this));

        // Teleport utility commands

    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new JoinLeave(this), this);
        pm.registerEvents(new ChatModeration(this), this);
        pm.registerEvents(new ActionEvents(this), this);
        pm.registerEvents(new WorldEvents(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
    }

    private void registerConfigs() {
        // Default config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

    }

    private void registerDependencies() {
        // Set up Vault Economy if available
        if (!setupEconomy()) {
            getLogger().warning("No Vault dependency found! Disabling related functionality!");
            economy = null;
        }

        // TODO Set up WorldGuard if available
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
//            getCommand("jail").setExecutor(new Jail());
        } else {
            getLogger().warning("No World Guard found! Disabling related functionality!");
        }
    }

    // Static getter just in case
    public static Lemonaid getInstance() { return instance; }

    // Admin level stuff
    public void toggleMasterCuff() { masterCuff = !masterCuff; }
    public void toggleMasterCuff(boolean isOn) { masterCuff = isOn; }
    public boolean isMasterCuff() { return masterCuff; }
    public void toggleGlobalMute() { globalMute = !globalMute; }
    public boolean isGlobalMute() { return globalMute; }


    // Get the Managers
    public TeleportManager getTeleportManager() { return teleportManager; }
    public UserManager getUserManager() { return userManager; }
    public LogManager getLogManager() { return logManager; }

    // Vault Stuff
    public Economy getEconomy() { return economy; }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;

        economy = rsp.getProvider();
        return economy != null;
    }

    // WorldGuard Stuff
}
