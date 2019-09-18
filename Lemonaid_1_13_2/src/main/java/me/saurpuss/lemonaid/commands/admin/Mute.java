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
import java.time.format.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Mute implements CommandExecutor {
    private final Lemonaid plugin;
    public Mute(Lemonaid plugin) {
        this.plugin = plugin;
    }

    private File mutesTXT;
    private Deque<String> recap = getRecap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.mute"))) {
            Player target;
            Duration duration;
            long endMute;
            String reason;
            if (args.length == 0) {
                // TODO add colors to all messages
                sender.sendMessage(Utils.color("Use: /mute list, or /mute <player> <time> <reason>"));
                return true;
            }
            else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    for (String s : recap)
                        sender.sendMessage(Utils.color(" - " + s));
                    return true;
                } else if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                    muteHelp(sender, 0);
                    return true;
                } else {
                    target = getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                        return true;
                    }
                    Lemon user = new Lemon(target.getUniqueId()).getUser();
                    if (user.isMuted()) {
                        user.setMuteEnd(0);
                        user.updateUser();

                        String log = sender.getName() + " unmuted " + target.getName();
                        addRecap(Utils.dateToString(LocalDate.now()) + " " + log);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.hasPermission("lemonaid.admin.notify.mute")) {
                                p.sendMessage(Utils.color("&c" + log));
                            }
                        }

                        return true;
                    }

                    duration = Duration.of(1, ChronoUnit.FOREVER);
                    endMute = duration.toMillis();
                    reason = "";
                }
            }
            else {
                if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("?"))) {
                    try {
                        muteHelp(sender, Integer.parseInt(args[1]));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Utils.color("&c" + args[1] + " is not a valid number"));
                    }
                    return true;
                }
                target = getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Utils.color("Usage: /mute <player> <time> <reason>, use /mute help for more information."));
                    return true;
                }

                if (Integer.parseInt(args[1]) == 0) {
                    duration = Duration.of(1, ChronoUnit.FOREVER);
                    endMute = duration.toMillis();
                    reason = "";
                } else if (!Character.isLetter(args[1].charAt(args[1].length() - 1))) {
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
                    duration = Duration.of(Integer.valueOf(s), time);
                    endMute = System.currentTimeMillis() + duration.toMillis();
                    reason = StringUtils.join(args, ' ', 2, args.length);
                }
            }
            mute(sender, target, endMute, reason);
            return true;
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }

    private void mute(CommandSender sender, Player target, long endMute, String reason) {
        Lemon user = new Lemon(target.getUniqueId()).getUser();
        user.setMuteEnd(endMute);
        user.updateUser();

        LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(endMute), ZoneId.systemDefault());
        String log = sender.getName() + " muted " + target.getName() + " until " +
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(date) +
                (reason.equals("") ? "." : (", reason: " + reason));

        addRecap(Utils.dateToString(LocalDate.now()) + " " + log);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("lemonaid.admin.notify.mute")) {
                p.sendMessage(Utils.color("&c" + log));
            }
        }
    }

    private void muteHelp(CommandSender sender, int pageNumber) {
        // TODO get help from file and send it back to the sender
    }

    private void addRecap(String message) {
        if (recap.size() == 10)
            recap.removeLast();

        recap.addFirst(message);
        try (PrintWriter writer = new PrintWriter(new FileWriter(mutesTXT, true), true)) {
            writer.println(message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to the new mutes.txt!");
        }
    }

    private LinkedList<String> getRecap() {
        mutesTXT = new File(plugin.getDataFolder(), "mutes.txt");
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
        mutesTXT = new File(plugin.getDataFolder(), "mutes.txt");
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
}
