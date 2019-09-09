package me.saurpuss.lemonaid.utils.config;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PartiesConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Lemonaid")
                .getDataFolder(), "parties.yml");

        try {
            file.createNewFile();
        } catch (Exception e) {
            Bukkit.getServer().getLogger().info(Utils.playerOnly("Error while creating parties.yml"));
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
            Bukkit.getServer().getLogger().info(Utils.playerOnly("Error while saving parties.yml"));
//            e.printStackTrace();
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
