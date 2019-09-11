package me.saurpuss.lemonaid;

import me.saurpuss.lemonaid.commands.admin.*;
import me.saurpuss.lemonaid.commands.social.*;
import me.saurpuss.lemonaid.commands.social.channels.*;
import me.saurpuss.lemonaid.commands.social.whisper.*;
import me.saurpuss.lemonaid.commands.teleport.*;
import me.saurpuss.lemonaid.events.ChatModeration;
import me.saurpuss.lemonaid.events.JoinLeave;
import me.saurpuss.lemonaid.utils.config.PartiesConfig;
import me.saurpuss.lemonaid.utils.players.Lemon;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Lemonaid extends JavaPlugin {
    public static Lemonaid plugin;
    private static Economy economy;
    private HashMap<UUID, Lemon> userManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
//        getLogger().info(Utils.console("Plugin startup"));
        plugin = this;
        userManager = new HashMap<>();
        // TODO ping DB

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
        getCommand("cuff").setExecutor(new Cuff()); // TODO after mute
        getCommand("mute").setExecutor(new Mute(this));
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

        pm.registerEvents(new JoinLeave(this), this);
        pm.registerEvents(new ChatModeration(this), this);
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

        // TODO Set up WorldGuard if available
        if (true) {
            getCommand("jail").setExecutor(new Jail());
        } else {
            getLogger().warning("No World Guard found! Disabling related functionality!");
        }

    }

    public void mapPlayer(UUID uuid, Lemon user) {
        userManager.put(uuid, user);
    }
    public void unmapPlayer(UUID uuid) {
        userManager.remove(uuid);
    }

    public Lemon getUser(UUID uuid) {
        return userManager.get(uuid);
    }

    public static Economy getEconomy() { return economy; }

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
