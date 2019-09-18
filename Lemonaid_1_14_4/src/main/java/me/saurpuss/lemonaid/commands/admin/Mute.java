package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.apache.commons.lang.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Mute implements CommandExecutor {

    private Lemonaid plugin;
    /**
     * Constructor to retrieve the instance of Lemonaid running on the server.
     * @param plugin Lemonaid instance
     */
    public Mute(Lemonaid plugin) {
        this.plugin = plugin;
    }

    // Logging instances of mutes
    private final File mutesTXT = new File(plugin.getDataFolder(), "mutes.txt");
    private Deque<String> recap = getRecap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lemonaid.admin.mute")) {
            sender.sendMessage(Utils.noPermission());
            return true;
        }

        // Variables needed to create mutes
        Player target;
        Duration duration;
        long endMute;
        String reason;

        // Not enough arguments
        if (args.length == 0) {
            // TODO add colors to all messages
            sender.sendMessage(Utils.color("Use: /mute list, or /mute <player> <time> <reason>"));
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
            else if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                muteHelp(sender, 0);
                return true;
            }

            // Check if args[0] is a player or offline player that requires an infinite mute
            else {
                target = getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Utils.color("&cUsage: /mute <player> <time> <reason>, use /mute help for more information."));
                    return true;
                }

                // get the user wrapper
                Lemon user = new Lemon(target.getUniqueId()).getUser();
                if (user.isMuted()) {
                    // Update existing mute status
                    user.setMuteEnd(0);
                    user.updateUser();

                    // Log un-mute action
                    String log = sender.getName() + " unmuted " + target.getName();
                    addRecap(Utils.dateToString(LocalDate.now()) + " " + log);

                    // Notify online users of the action
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("lemonaid.admin.notify.mute")) {
                            p.sendMessage(Utils.color("&c" + log));
                        }
                    }

                    // Notify target
                    target.sendMessage(Utils.color("&6Your mute has been lifted! Chat away!"));
                    return true;
                }

                // Mute the target permanently
                duration = Duration.of(1, ChronoUnit.FOREVER);
                mute(sender, target, duration.toMillis(), "");
                return true;
            }
        }

        // Multiple arguments detected
        else {
            // Get a specific page from /mute help X, assuming it's a valid number
            if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                try {
                    muteHelp(sender, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(Utils.color("&c" + args[1] + " is not a valid number"));
                }
                return true;
            }

            // Try to retrieve an online or offline player from the first argument
            target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                return true;
            }

            // Check if the second argument is set to zero and create a permanent mute
            if (Integer.parseInt(args[1]) == 0) {
                duration = Duration.of(1, ChronoUnit.FOREVER);
                mute(sender, target, duration.toMillis(), "");
                return true;
            }

            // Check if the second argument is an invalid option
            else if ((args[1].length() < 2) || (!Character.isLetter(args[1].charAt(args[1].length() - 1)))) {
                sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                return true;
            }

            // Try to parse the arguments to a valid mute
            else {
                String s = "";
                ChronoUnit time = ChronoUnit.NANOS;
                // check if last char is s, m, d, y
                for (char c = 0; c < args[1].length(); c++) {
                    // Add digits to a string
                    if (Character.isDigit(c)) {
                        s += c;
                    }
                    // Convert first letter to a ChronoUnit and break the loop
                    else if (Character.isLetter(c)) {
                        time = getUnit(c);
                        break;
                    }
                    // Invalid character, return to break the loop
                    else {
                        sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                        return true;
                    }
                }

                // Set up the variables for the mute and implement
                duration = Duration.of(Integer.valueOf(s), time);
                endMute = System.currentTimeMillis() + duration.toMillis();
                reason = StringUtils.join(args, ' ', 2, args.length);

                mute(sender, target, endMute, reason);
                return true;
            }
        }
    }

    /**
     * Mute action to be implemented on a target
     * @param sender user implementing the mute
     * @param target target to be muted
     * @param endMute end date of the mute in millis
     * @param reason additional arguments for logging purposes
     */
    private void mute(CommandSender sender, Player target, long endMute, String reason) {
        // Get a user wrapper for the target
        Lemon user = new Lemon(target.getUniqueId()).getUser();
        user.setMuteEnd(endMute);
        user.updateUser();

        // Format the endMute millis for readable logging purposes
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(endMute), ZoneId.systemDefault());
        String log = sender.getName() + " muted " + target.getName() + " until " +
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date) +
                (reason.equals("") ? "." : (", reason: " + reason));

        // Add today to the recap and log the event
        addRecap(Utils.dateToString(LocalDate.now()) + " " + log);

        // Notify online players with the right permission
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.admin.notify.mute")) {
                p.sendMessage(Utils.color("&c" + log));
            }
        }
    }

    private void muteHelp(CommandSender sender, int pageNumber) {
        // TODO get help from file and send it back to the sender

        sender.sendMessage(Utils.color("There is no help file right now, lol!"));
    }

    /**
     * Add a recap to the Deque and mutes.txt for logging purposes
     * @param message log of the mute event
     */
    private void addRecap(String message) {
        // Make space if the limit is reached
        if (recap.size() == 10)
            recap.removeLast();

        // log as the most recent entry and try to write to the file
        recap.addFirst(message);
        try (PrintWriter writer = new PrintWriter(new FileWriter(mutesTXT, true), true)) {
            writer.println(message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to the new mutes.txt!");
        }
    }

    /**
     * Retrieve the last 10 mute events from mutes.txt
     * @return Linked list with the last 10 mutes logged
     */
    private LinkedList<String> getRecap() {
        // Check for file
        if (!mutesTXT.exists())
            makeLog();

        // Read file into an ArrayList
        List<String> log = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(mutesTXT), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.add(line);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read mutes.txt");
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
            plugin.getLogger().info("Creating new mutes.txt!");
            mutesTXT.getParentFile().mkdirs();
            mutesTXT.createNewFile();

            // Try to write to the file
            PrintWriter writer = new PrintWriter(new FileWriter(mutesTXT, true), true);
            writer.println("&c" + Utils.dateToString(LocalDate.now())
                    + ": &fUse &d/mute <player> <time> <message> &fto mute someone");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while creating mutes.txt!");
        }
    }

    /**
     * Convenience method to look for a viable payer to mute. If there is no online player
     * check if the name matches an offline player
     * @param name player IGN to check against
     * @return player object that matches the name
     */
    private Player getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return player;
        }

        // Try to get an offline player
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                player = p.getPlayer();
                return player;
            }
        }
        return null;
    }

    /**
     * Convenience method to translate a character to a valid ChronoUnit
     * @param c preferably s, m, h, d, w, or y
     * @return applicable ChronoUnit
     */
    private ChronoUnit getUnit(char c) {
        ChronoUnit unit;
        switch (c) {
            case 's': case 'S': unit = ChronoUnit.SECONDS; break;
            case 'm': case 'M': unit = ChronoUnit.MINUTES; break;
            case 'h': case 'H': unit = ChronoUnit.HOURS; break;
            case 'd': case 'D': unit = ChronoUnit.DAYS; break;
            case 'w': case 'W': unit = ChronoUnit.WEEKS; break;
            case 'y': case 'Y': unit = ChronoUnit.YEARS; break;
            default: unit = ChronoUnit.FOREVER;
        }
        return unit;
    }
}
