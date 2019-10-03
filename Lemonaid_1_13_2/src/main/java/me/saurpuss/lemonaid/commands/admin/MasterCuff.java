package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class MasterCuff implements CommandExecutor {

    private Lemonaid plugin;
    private final File cuffTXT = new File(plugin.getDataFolder(), "cuffs.txt");
    public MasterCuff(Lemonaid plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.mastercuff")) return true;

        // toggle mastercuff
        if (args.length == 0) {
            // Notify all relevant parties
            sender.sendMessage("&cApplying MasterCuff to all players until next restart!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("lemonaid.notify"))
                    player.sendMessage("§c" + sender.getName() + " used MasterCuff!");
            }

            plugin.toggleMasterCuff();
            addRecap("§c" + Utils.dateToString(LocalDate.now()) + ": " +
                    sender.getName() + " used MasterCuff!");
            return true;
        }

        // Specify mastercuff toggle
        else if (args[0].equalsIgnoreCase("on") ||
                args[0].equalsIgnoreCase("off")) {
            boolean on = args[0].equalsIgnoreCase("on"); // on == true, off == false
            // Notify all relevant parties
            sender.sendMessage("§c" + (on ? "Cuffed" : "Uncuffed") + " all online players!!!");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("lemonaid.notify"))
                    player.sendMessage("§c" + sender.getName() + " used MasterCuff " + args[0] + "!");
            }

            plugin.toggleMasterCuff(on);
            addRecap("§c" + Utils.dateToString(LocalDate.now()) + ": " +
                    sender.getName() + " used MasterCuff " + on + "!");
            return true;
        }

        return false;
    }

    private void addRecap(String message) {
        if (!cuffTXT.exists())
            makeLog();

        try (PrintWriter writer = new PrintWriter(new FileWriter(cuffTXT, true), true)) {
            writer.println(message);
            plugin.getLogger().info("[MASTERCUFF] " + message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to the new cuffs.txt!");
        }
    }

    private void makeLog() {
        try {
            // Make file and directories
            plugin.getLogger().info("Creating new cuffs.txt!");
            cuffTXT.getParentFile().mkdirs();
            cuffTXT.createNewFile();

            // Try to write to the file
            PrintWriter writer = new PrintWriter(new FileWriter(cuffTXT, true), true);
            writer.println("§c" + Utils.dateToString(LocalDate.now())
                    + ": §fUse §d/cuff §e<§dplayer§e> <§dmessage§e> §fto cuff someone");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while creating cuffs.txt!");
        }
    }
}
