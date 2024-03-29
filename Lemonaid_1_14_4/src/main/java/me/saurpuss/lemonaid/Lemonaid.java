package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.*;
import me.saurpuss.lemonaid.commands.social.Busy;
import me.saurpuss.lemonaid.commands.social.Ignore;
import me.saurpuss.lemonaid.commands.social.Msg;
import me.saurpuss.lemonaid.commands.social.Reply;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.ActionEvents;
import me.saurpuss.lemonaid.events.ChatModeration;
import me.saurpuss.lemonaid.events.JoinLeave;
import me.saurpuss.lemonaid.utils.users.Lemon;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Lemonaid extends JavaPlugin {
    public static Lemonaid plugin;
    private static Economy economy;
    private HashMap<UUID, Lemon> userManager;

    private static boolean masterCuff = false;
    private static boolean globalMute = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        plugin = this;
        userManager = new HashMap<>();

        registerConfigs();
        registerCommands();
        registerEvents();
        registerDependencies();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
//        getLogger().info(Utils.console("Plugin shutdown"));

        // Save all remaining Lemons to DB
        userManager.forEach((uuid, user) -> user.updateUser());
    }

    private void registerCommands() {
        // Admin level command
        getCommand("fly").setExecutor(new Fly());
        getCommand("broadcast").setExecutor(new Broadcast());
        getCommand("recap").setExecutor(new Recap(this));
        getCommand("mute").setExecutor(new Mute(this));
        getCommand("cuff").setExecutor(new Cuff());
        getCommand("mastercuff").setExecutor(new MasterCuff());
//        getCommand("globalmute").setExecutor(new GlobalMute());


        // Social commands
        getCommand("msg").setExecutor(new Msg(this));
        getCommand("reply").setExecutor(new Reply(this));
        getCommand("busy").setExecutor(new Busy(this));
        getCommand("ignore").setExecutor(new Ignore(this));

//        getCommand("localchat").setExecutor(new LocalChat(this));

        // Teleport commands
        getCommand("tpa").setExecutor(new Tpa(this));
        getCommand("tpahere").setExecutor(new TpaHere(this));
        getCommand("tpaccept").setExecutor(new TpAccept());
        getCommand("tpdeny").setExecutor(new TpDeny()); // also tpacancel
        getCommand("back").setExecutor(new Back());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new JoinLeave(this), this);
        pm.registerEvents(new ChatModeration(this), this);
        pm.registerEvents(new ActionEvents(), this);
    }

    private void registerConfigs() {
        // Default config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Save user data in SQL or Config
        if (getConfig().getBoolean("mysql")) {
//            MySQLDatabase.pingTables();
        } else {
//            LemonConfig.setup();
//            LemonConfig.get().createSection("users");
//            LemonConfig.get().options().copyDefaults(true);
//            LemonConfig.save();
        }

        // TODO add this for the parties bits too

        // Party make-up config
//        PartiesConfig.setup();
//        PartiesConfig.get().options().copyDefaults(true);
//        PartiesConfig.save();

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
    public static void toggleMasterCuff() { masterCuff = !masterCuff; }
    public static boolean isMasterCuff() { return masterCuff; }
    public static void toggleGlobalMute() { globalMute = !globalMute; }
    public static boolean isGlobalMute() { return globalMute; }

    // Keep track of Lemons
    public void mapPlayer(UUID uuid, Lemon user) { userManager.put(uuid, user); }
    public void unmapPlayer(UUID uuid) { userManager.remove(uuid); }
    public Lemon getUser(UUID uuid) { return userManager.get(uuid); }

    public static Economy getEconomy() { return economy; }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;

        economy = rsp.getProvider();
        return economy != null;
    }
}
