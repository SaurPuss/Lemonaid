package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;

public class Mute implements CommandExecutor {

    private Lemonaid plugin = Lemonaid.getInstance();
    private File mutesTXT = new File(plugin.getDataFolder(), "mutes.txt");
    private Deque<String> recap = getRecap();
    // TODO add sql option for tracking active mutes, for now use config to store active mutes
    private HashMap<UUID, Long> activeMutes = new HashMap<>();

    // TODO hasMute(player) for join & quit
    // TODO Use duration, if duration is less than 24 hours, start a runnable to auto remove the mute


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.mute"))) {
            if (args.length == 0) {
                // TODO add colors to all messages
                sender.sendMessage(Utils.color("Use: /mute list, or /mute <player> <time> <reason>"));
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (recap == null) {
                        sender.sendMessage("&cFailed to retrieve mutes log!");
                        return true;
                    }

                    for (String s : recap)
                        sender.sendMessage(Utils.color(" - " + s));
                    return true;
                } else if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                    // TODO explain time units for this command

                    return true;
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        // TODO add offline player option with a conversation?
                        sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                        return true;
                    } else {
                        // TODO infinite mute action
                        return true;
                    }
                }
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                    return true;
                }

                if (Integer.parseInt(args[1]) == 0) {
                    // TODO mute infinite action & return

                    return true;
                }
                // Check time unit
                if (!Character.isLetter(args[1].charAt(args[1].length() - 1))) {
                    sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                    return true;
                } else {
                    String s = "";
                    long time = 0;
                    // check if last char is s, m, d, y
                    for (char c = 0; c < args[1].length(); c++) {
                        if (Character.isDigit(c)) {
                            s += c;
                        } else if (Character.isLetter(c)) {
                            time = getSecondsFromChar(c);
                            break;
                        } else {
                            sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                            return true;
                        }
                    }
                    long amount = Long.parseLong(s) * time; // mute duration in seconds
                    String reason = StringUtils.join(args, ' ', 2, args.length);

                    // TODO mute action mute(sender, target, amount, args[1], reason);
                    return true;
                }
            }
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }

    private long getSecondsFromChar(char c) {
        long unit;
        switch (c) {
            case 's': case 'S': unit = 1; break;
            case 'm': case 'M': unit = 60; break;
            case 'h': case 'H': unit = 3_600; break;
            case 'd': case 'D': unit = 86_400; break;
            case 'y': case 'Y': unit = 31_356_000; break; // TODO just us forever instead?
            default: unit = 0; // 0 equals forever, no unmute scheduled
        }
        return unit;
    }


    private void addRecap(String message) {
        if (recap.size() == 10)
            recap.removeLast();
        recap.addFirst(Utils.dateToString(LocalDate.now()) + " " + message);

        // TODO add to mutes.txt
    }

    private LinkedList<String> getRecap() {
        if (!mutesTXT.exists()) {
            makeLog();
        }

        List<String> log = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(mutesTXT), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.add(line);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read mutes.txt");
            return null;
        }

        // Get the last 10 entries from the log in reverse order
        LinkedList<String> list = new LinkedList<>();
        for (int i = log.size() - 1; i > log.size() - 11; i--) {
            list.add(log.get(i));
        }
        return list;
    }

    private void makeLog() {
        try {
            plugin.getLogger().info("Creating new mutes.txt!");
            mutesTXT.getParentFile().mkdirs();
            mutesTXT.createNewFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(mutesTXT, true), true)) {
                writer.println("&c" + Utils.dateToString(LocalDate.now()) + ": &fUse &d/mute <player> <time> <message> &fto mute someone");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to write to the new mutes.txt!");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create mutes.txt!");
        }
    }
}
