package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class GlobalMute implements CommandExecutor {

    private Lemonaid plugin;
    private final File mutesTXT = new File(plugin.getDataFolder(), "mutes.txt");
    public GlobalMute(Lemonaid plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (sender.hasPermission("lemonaid.globalmute")) {
            sender.sendMessage(ChatColor.RED + "Applying GlobalMute to all players until next restart!");


            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("lemonaid.notify"))
                    player.sendMessage(ChatColor.RED + sender.getName() + " used GlobalMute!");
            }

            plugin.toggleGlobalMute();
            addRecap(ChatColor.RED + Utils.dateToString(LocalDate.now()) + ": " +
                    sender.getName() + " used GlobalMute!");
        }

        return true;
    }


    private void addRecap(String message) {
        if (!mutesTXT.exists())
            makeLog();

        try (PrintWriter writer = new PrintWriter(new FileWriter(mutesTXT, true), true)) {
            writer.println(message);
            plugin.getLogger().info("[GLOBALMUTE] " + message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to the new mutes.txt!");
        }
    }

    private void makeLog() {
        try {
            // Make file and directories
            plugin.getLogger().info("Creating new mutes.txt!");
            mutesTXT.getParentFile().mkdirs();
            mutesTXT.createNewFile();

            // Try to write to the file
            PrintWriter writer = new PrintWriter(new FileWriter(mutesTXT, true), true);
            writer.println(ChatColor.RED + Utils.dateToString(LocalDate.now())
                    + ": §fUse §d/mute <player> <time> <message> §fto mute a player");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while creating mutes.txt!");
        }
    }
}
