package me.saurpuss.lemonaid.utils.players;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.util.*;

public class LemonConfig {

    private final static Lemonaid plugin = Lemonaid.plugin;
    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(plugin.getDataFolder(), "users.yml");

        if (!file.exists()) {
            plugin.getLogger().warning("Creating users.yml to store Lemonaid user data!");
            plugin.getLogger().warning("Use SQL if you need to store a large number of users!");
            try {
                file.createNewFile();
            } catch (Exception e) {
                plugin.getLogger().warning("Error while creating users.yml");
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().warning("Error while saving users.yml");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }


    // TODO fix this all to make sure it's correct and add individual options for each Lemon attribute
    public static Lemon getUser(UUID uuid) {
        ConfigurationSection config = get().getConfigurationSection("users." + uuid.toString());
        int muteEnd = config.getInt(".mute-end");
        String nickname = config.getString(".nickname");
        World world = Bukkit.getWorld(config.getString(".world"));
        double x = config.getDouble(".last-location.x");
        double y = config.getDouble(".last-location.y");
        double z = config.getDouble(".last-location.z");
        Location lastLocation = new Location(world, x, y, z);
        UUID lastMessage = UUID.fromString(config.getString(".last-message"));
        boolean busy = config.getBoolean(".busy");
        HashMap<String, Location> homes = new HashMap<>();
        int maxHomes = config.getInt(".maxHomes");
        HashSet<UUID> ignored = new HashSet<>();

        return new Lemon(uuid, muteEnd, nickname, lastLocation, lastMessage,
                busy, homes, maxHomes, ignored);
    }

    static boolean saveUser(Lemon user) {
        ConfigurationSection config = get().getConfigurationSection("users." + user.getUuid().toString());
        if (!get().getConfigurationSection("users.").contains(user.getUuid().toString())) {
            get().createSection(get().getConfigurationSection("users.") + user.getUuid().toString());
        }

        Location lastLoc = user.getLastLocation();
        get().set(config + ".mute-end", user.getMuteEnd());
        get().set(config + ".nickname", user.getNickname());
        get().set(config + ".last-location.world", lastLoc.getWorld().getName());
        get().set(config + ".last-location.x", lastLoc.getX());
        get().set(config + ".last-location.y", lastLoc.getY());
        get().set(config + ".last-location.z", lastLoc.getZ());
        get().set(config + ".last-message", user.getLastMessage().toString());
        get().set(config + ".busy", user.isBusy());
        user.getHomes().forEach((home, location) -> {
            get().set(config + "." + home + ".world", location.getWorld().getName());
            get().set(config + "." + home + ".x", location.getX());
            get().set(config + "." + home + ".y", location.getY());
            get().set(config + "." + home + ".z", location.getZ());
        });
        get().set(config + ".maxHomes", user.getMaxHomes());
        user.getIgnored().forEach(uuid -> get().set(config + "ignored.", uuid.toString()));

        return true;
    }
}
