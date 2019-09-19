package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.time.*;
import java.util.*;

public class Cuff implements CommandExecutor {

    private Lemonaid plugin;

    /**
     * Constructor to retrieve the instance of Lemonaid running on the server.
     * @param plugin Lemonaid instance
     */
    public Cuff(Lemonaid plugin) {
        this.plugin = plugin;
    }

    // Logging instances of mutes
    private final File cuffTXT = new File(plugin.getDataFolder(), "cuffs.txt");
    private Deque<String> recap = getRecap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lemonaid.admin.cuff")) {
            sender.sendMessage(Utils.noPermission());
            return true;
        }

        // Not enough arguments
        if (args.length == 0) {
            // TODO add colors to all messages
            sender.sendMessage(Utils.color("Use: /cuff list, or /cuff <player> <reason>"));
            return true;
        }

        // /mute + subcommand
        else if (args.length == 1) {
            // Retrieve a recap of the last 10 mutes
            if (args[0].equalsIgnoreCase("list")) {
                for (String s : recap)
                    sender.sendMessage(Utils.color(" - " + s));
                return true;
            }

            // Retrieve mute help instructions
            if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                cuffHelp(sender, 0);
                return true;
            }

            // Check if args[0] is a player or offline player that requires an infinite mute
            Player target = Utils.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.color("&cUsage: /cuff <player> <reason>, use /cuff help for more information."));
                return true;
            }

            // Toggle cuff on the target
            cuff(sender, target, "");
            return true;
        }

        // Multiple arguments detected
        else {
            // Get a specific page from /cuff help X, assuming it's a valid number
            if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                try {
                    cuffHelp(sender, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Utils.color("&c" + args[1] + " is not a valid number"));
                }
                return true;
            }

            // Try to retrieve an online or offline player from the first argument
            Player target = Utils.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.color("Usage: /cuff <player> <reason>, use /cuff help for more information."));
                return true;
            }

            // Compile the rest of the arguments into a message and log
            String reason = StringUtils.join(args, ' ', 2, args.length);
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
        // Log the occurrence
        Lemon user = new Lemon(target.getUniqueId()).getUser();
        String log = sender.getName() + (user.isCuffed() ? " uncuffed" : " cuffed ")
                + target.getName() + (reason.equals("") ? "." : (", reason: " + reason));

        addRecap(Utils.dateToString(LocalDate.now()) + " " + log);

        // Notify online players with the right permission
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.admin.notify.mute")) {
                p.sendMessage(Utils.color("&c" + log));
            }
        }

        // Update and notify the target
        user.setCuffed(!user.isCuffed());
        user.updateUser();
        target.sendMessage(Utils.color("&cYou are now " + (user.isCuffed() ? "cuffed!" : "uncuffed!") +
                (reason.equals("") ? "" : "Reason: &r" + reason)));
    }

    private void cuffHelp(CommandSender sender, int pageNumber) {
        // TODO get help from file and send it back to the sender

        sender.sendMessage(Utils.color("There is no help file right now, lol!"));
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
                    + ": &fUse &d/cuff <player> <message> &fto cuff someone");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while creating cuffs.txt!");
        }
    }
}
