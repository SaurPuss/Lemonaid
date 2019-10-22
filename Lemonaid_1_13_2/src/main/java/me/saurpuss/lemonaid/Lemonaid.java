package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.*;
import me.saurpuss.lemonaid.commands.admin.moderation.*;
import me.saurpuss.lemonaid.commands.social.*;
import me.saurpuss.lemonaid.commands.social.channels.*;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.*;
import me.saurpuss.lemonaid.utils.sql.DatabaseManager;
import me.saurpuss.lemonaid.utils.tp.TeleportManager;
import me.saurpuss.lemonaid.utils.users.Lemon;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Lemonaid extends JavaPlugin {
    public static Lemonaid plugin;

    // Managers etc
    private static Economy economy;
    private static HashMap<UUID, Lemon> userManager;
    private static TeleportManager teleportManager;

    // Moderation settings
    private static boolean masterCuff = false;
    private static boolean globalMute = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        plugin = this;
        userManager = new HashMap<>();
        teleportManager = new TeleportManager(this);

        // TODO test connection to DB and set up tables etc

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
        saveUserManager();
        saveWarpManager();
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

        // Teleport commands
        getCommand("tpa").setExecutor(new Tpa(this));
        getCommand("tpahere").setExecutor(new TpaHere(this));
        getCommand("tpaccept").setExecutor(new TpAccept(this));
        getCommand("tpdeny").setExecutor(new TpDeny()); // also tpacancel
        getCommand("back").setExecutor(new Back(this));

    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new JoinLeave(this), this);
        pm.registerEvents(new ChatModeration(this), this);
        pm.registerEvents(new ActionEvents(this), this);
        pm.registerEvents(new WorldEvents(this), this);
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

    // Admin level stuff
    public void toggleMasterCuff() { masterCuff = !masterCuff; }
    public void toggleMasterCuff(boolean isOn) { masterCuff = isOn; }
    public boolean isMasterCuff() { return masterCuff; }
    public void toggleGlobalMute() { globalMute = !globalMute; }
    public boolean isGlobalMute() { return globalMute; }

    // Keep track of Lemons
    public void mapPlayer(UUID uuid, Lemon user) { userManager.put(uuid, user); }
    public void unmapPlayer(UUID uuid) { userManager.remove(uuid); }
    public Lemon getUser(UUID uuid) { return userManager.get(uuid); }

    public TeleportManager getTeleportManager() { return teleportManager; }

    // trigger these during world save event && onDisable
    public void saveUserManager() {
        userManager.forEach((uuid, user) -> user.saveUser());
        DatabaseManager.deleteRemovalRecords();
    }
    public void saveWarpManager() {
        // TODO create stuff in DB-manager
    }

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
