package me.saurpuss.lemonaid.utils.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class HomesConfig {
    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Lemonaid")
                .getDataFolder(), "homes.yml");

        try {
            file.createNewFile();
        } catch (Exception e) {
            Bukkit.getServer().getLogger().warning("Error while creating homes.yml");
//            e.printStackTrace();
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
            Bukkit.getServer().getLogger().warning("Error while saving parties.yml");
//            e.printStackTrace();
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
