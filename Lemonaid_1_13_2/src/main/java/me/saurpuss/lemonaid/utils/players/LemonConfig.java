package me.saurpuss.lemonaid.utils.players;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.util.UUID;

public class LemonConfig {

    private static Lemonaid plugin = Lemonaid.plugin;
    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(plugin.getDataFolder(), "users.yml");

        try {
            file.createNewFile();
        } catch (Exception e) {
            plugin.getLogger().warning("Error while creating users.yml");
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

    public static Lemon getUser(UUID uuid) {


        return new Lemon(uuid);
    }

    static boolean saveUser(Lemon user) {

        return true;
    }
}
