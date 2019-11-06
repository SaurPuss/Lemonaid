package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PlayerSearch;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.time.*;
import java.util.*;

public class Cuff implements CommandExecutor, PlayerSearch {

    private Lemonaid plugin;
    private final File cuffTXT = new File(plugin.getDataFolder(), "cuffs.txt");
    private Deque<String> recap = getRecap();

    public Cuff(Lemonaid plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.cuff")) return true;

        // Not enough arguments
        if (args.length == 0) return false;

        // /cuff + subcommand
        else if (args.length == 1) {
            // Retrieve a recap of the last 10 cuffs
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GOLD + "Listing last 10 cuff events:");
                for (String s : recap)
                    sender.sendMessage(Utils.color(" - " + s));
                return true;
            }

            // Retrieve cuff help instructions
            if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                cuffHelp(sender, 0);
                return true;
            }

            // Check if args[0] is a player or offline player
            Player target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + args[0] + "! Use " +
                        ChatColor.BLUE + "/cuff"+ ChatColor.RED + " to view available command options.");
                return true;
            }

            // Target is not allowed to be cuffed
            if (target.hasPermission("lemonaid.exempt")) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is exempt from being cuffed!");
                return true;
            }

            // Toggle cuff on the target
            cuff(sender, target, "");
            return true;
        }

        // Multiple arguments detected
        else {
            // Get a specific page from /cuff help X, assuming it's a valid number
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                try {
                    cuffHelp(sender, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number");
                }
                return true;
            }

            // Try to retrieve an online or offline player from the first argument
            Player target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + args[0] + "! Use " +
                        ChatColor.BLUE + "/cuff"+ ChatColor.RED + " to view available command options.");
                return true;
            }

            // Target is not allowed to be cuffed
            if (target.hasPermission("lemonaid.exempt")) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is exempt from being cuffed!");
                return true;
            }

            // Compile the rest of the arguments into a message and log
            String reason = StringUtils.join(args, ' ', 1, args.length);
            cuff(sender, target, reason);
            return true;
        }
    }

    /**
     * Cuff action to be implemented on a target
     *
     * @param sender user implementing the cuff
     * @param target target to be cuffed
     * @param reason additional arguments for logging purposes
     */
    private void cuff(CommandSender sender, Player target, String reason) {
        // Retrieve user if exists
        User user = plugin.getUserManager().getUser(target.getUniqueId());
        if (user == null) { // This should not happen!
            sender.sendMessage(ChatColor.RED + "Error: Lemonaid:UserNotFound! Please contact an administrator!");
            return;
        }

        // Log the occurrence
        String log = sender.getName() + (user.isCuffed() ? " uncuffed " : " cuffed ")
                + target.getName() + (reason.equals("") ? "." : (", reason: " + reason));
        addRecap(Utils.dateToString(LocalDate.now()) + " " + log);

        // Notify online players with the right permission
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.notify")) {
                p.sendMessage(Utils.color("&c"+ log));
            }
        }

        // Update and notify the target
        user.setCuffed(!user.isCuffed());
        plugin.getUserManager().updateUser(user);
        target.sendMessage("§cYou are now " + (user.isCuffed() ? "cuffed!" : "uncuffed!") +
                (reason.equals("") ? "" : " Reason: §r" + Utils.color(reason)));
        plugin.getLogger().info("[CUFF] " + log);
    }



    private void cuffHelp(CommandSender sender, int pageNumber) {
        // TODO get help from file and send it back to the sender

        sender.sendMessage(ChatColor.RED + "There is no help file right now, lol!");
    }

    /**
     * Add a recap to the Deque and cuffs.txt for logging purposes
     *
     * @param message log of the cuff event
     */
    private void addRecap(String message) {
        // Make space if the limit is reached
        if (recap.size() == 10)
            recap.removeLast();

        // log as the most recent entry and try to write to the file
        recap.addFirst(message);

        if (!cuffTXT.exists())
            makeLog();

        try (PrintWriter writer = new PrintWriter(new FileWriter(cuffTXT, true), true)) {
            writer.println(message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to the new cuffs.txt!");
        }
    }

    /**
     * Retrieve the last 10 mute events from cuffs.txt
     *
     * @return Linked list with the last 10 cuffs logged
     */
    private LinkedList<String> getRecap() {
        // Check for file
        if (!cuffTXT.exists())
            makeLog();

        // Read file into an ArrayList
        List<String> log = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(cuffTXT), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.add(line);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read cuffs.txt");
            return null;
        }

        // Get the last 10 entries from the list in reverse order
        LinkedList<String> list = new LinkedList<>();
        for (int i = log.size() - 1; i > log.size() - 11; i--) {
            list.add(log.get(i));
        }

        return list;
    }

    /**
     * Create a logging file in the plugin folder.
     */
    private void makeLog() {
        try {
            // Make file and directories
            plugin.getLogger().info("Creating new cuffs.txt!");
            cuffTXT.getParentFile().mkdirs();
            cuffTXT.createNewFile();

            // Try to write to the file
            PrintWriter writer = new PrintWriter(new FileWriter(cuffTXT, true), true);
            writer.println("&c" + Utils.dateToString(LocalDate.now())
                    + ": &fUse &d/cuff &e<&dplayer&e> <&dmessage&e> &fto cuff someone");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while creating cuffs.txt!");
        }
    }
}
