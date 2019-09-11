package me.saurpuss.lemonaid.utils.config;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.configuration.file.*;

import java.io.File;

public class PartiesConfig {

    private static Lemonaid plugin = Lemonaid.plugin;
    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(plugin.getDataFolder(), "parties.yml");

        try {
            file.createNewFile();
        } catch (Exception e) {
            plugin.getLogger().warning("Error while creating parties.yml");
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
            plugin.getLogger().warning("Error while saving parties.yml");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
