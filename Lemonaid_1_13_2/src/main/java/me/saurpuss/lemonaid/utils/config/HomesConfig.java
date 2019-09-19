package me.saurpuss.lemonaid.utils.config;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class HomesConfig {
    private final static Lemonaid plugin = Lemonaid.plugin;
    private final static File file = new File(plugin.getDataFolder(), "homes.yml");
    private static FileConfiguration customFile;

    public static void setup() {
        if (!file.exists()) {
            plugin.getLogger().warning("Creating homes.yml to store Lemonaid home data!");
            plugin.getLogger().warning("Use SQL if you need to store a large number of users!");

            try {
                file.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                plugin.getLogger().warning("Error while creating homes.yml");
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
            Bukkit.getServer().getLogger().warning("Error while saving homes.yml");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
