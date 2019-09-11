package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.players.Lemon;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Mute implements CommandExecutor {

    private Lemonaid plugin;
    private File mutesTXT = new File(plugin.getDataFolder(), "mutes.txt");
    private Deque<String> recap = getRecap();

    public Mute(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.mute"))) {
            // /mute
            if (args.length == 0) {
                // TODO add colors to all messages
                sender.sendMessage(Utils.color("Use: /mute list, or /mute <player> <time> <reason>"));
                return true;
            }
            // /mute <list|help|player>
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    for (String s : recap)
                        sender.sendMessage(Utils.color(" - " + s));
                    return true;
                } else if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                    // TODO explain time units for this command

                    return true;
                } else {
                    Player target = getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                        return true;
                    }
                    Lemon user = Lemon.getUser(target.getUniqueId());
                    if (user.isMuted()) {
                        // TODO toggle mute to off
                        user.setMuteEnd(0);
                        user.updateUser();
                        // TODO let sender know the player has been unmuted
                        return true;
                    }

                    Duration duration = Duration.of(1, ChronoUnit.FOREVER);

                    // TODO infinite mute action
                    return true;
                }
            }
            // /mute <player> <specifics>
            else {
                Player target = getPlayer(args[0]);
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
                    ChronoUnit time = ChronoUnit.NANOS;
                    // check if last char is s, m, d, y
                    for (char c = 0; c < args[1].length(); c++) {
                        if (Character.isDigit(c)) {
                            s += c;
                        } else if (Character.isLetter(c)) {
                            time = getUnit(c);
                            break;
                        } else {
                            sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                            return true;
                        }
                    }
                    Duration duration = Duration.of(Integer.valueOf(s), time);
                    String reason = StringUtils.join(args, ' ', 2, args.length);

                    // TODO mute action mute(sender, target, duration, reason);
                    return true;
                }
            }
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }

    private Player getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            // Try to get an offline player
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    player = p.getPlayer();
                    break;
                }
            }
        }
        return player;
    }

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
