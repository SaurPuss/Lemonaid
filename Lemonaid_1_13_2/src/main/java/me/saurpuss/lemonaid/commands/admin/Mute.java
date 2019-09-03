package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class Mute implements CommandExecutor {

    private static Lemonaid plugin = Lemonaid.getInstance();
    private File file = new File(plugin.getDataFolder(), "mutes.txt");
    private static RandomAccessFile active;
    public static HashSet<UUID> activeMutes; // = getMutes();
    private ArrayList<String> muteLog = getLog();
    private Deque<String> muteRecap = getRecap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.mute"))) {
            if (args.length == 0) {
                sender.sendMessage(Utils.admin("Usage: /mute <player> <time> <reason>"));
                return true;
            }
            // Get player from the first argument
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                // If there is no valid online player, check offline players
                OfflinePlayer[] list = Bukkit.getOfflinePlayers();
                for (OfflinePlayer p : list) {
                    if (p.getPlayer().getName().equalsIgnoreCase(args[0])) {
                        target = p.getPlayer();
                    }
                }
                // Still haven't found a match
                if (target == null) {
                    sender.sendMessage(Utils.admin(args[0] + " not found."));
                    return true;
                }
            }
            // We now have a target
            if (args.length == 1) {
                if (activeMutes.contains(target.getUniqueId())) {
                    activeMutes.remove(target.getUniqueId());
                    // TODO log unmute
                    return true;
                } else {
                    return mute(sender, target, args[1], null);
                }
            }
            // If time is infinite (/mute player 0 reason)
            else if (args[1].equals("0")) {
                String reason = null;
                if (args.length >= 3)
                    reason = StringUtils.join(args, ' ', 2, args.length);
                return mute(sender, target, args[1], reason);
            } else {



            }


            return true;
        } else {
            sender.sendMessage(Utils.error());
            return true;
        }
    }



    private boolean mute(CommandSender admin, Player player, String time, String reason) {
        if (!activeMutes.contains(player.getUniqueId())) {
            activeMutes.add(player.getUniqueId());
        }

        // decode time

        // Schedule the unmute

        // Log mute

        // Notify online players with the notify perm

        return true;
    }

    private ArrayList<String> getLog() {
        if (!file.exists())
            makeLog();

        ArrayList<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), Charset.defaultCharset()))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
//    private static HashSet<UUID> getMutes() {
//        if (!active.exists())
//            makeMutes();
//
//        HashSet<UUID> list = new HashSet<>();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//                new FileInputStream(active), Charset.defaultCharset()))) {
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                list.add(UUID.fromString(line));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }

    private static void saveActiveMutes() {
//        if (!active.exists())
//            makeMutes();
        try {
            active = new RandomAccessFile(plugin.getDataFolder() + "activemutes.txt", "rw");

            for (UUID uuid : activeMutes) {
//                writer.println(uuid.toString());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to the new activemutes.txt!");
        }



    }

    private Deque<String> getRecap() {
        Deque<String> list = new LinkedList<>();
        for (int i = muteLog.size() - 1; i > muteLog.size() - 11; i--) {
            list.addFirst(muteLog.get(i));
        }
        return list;
    }

    private void makeLog() {
        try {
            plugin.getLogger().info("Creating new mutes.txt!");
            file.getParentFile().mkdirs();
            file.createNewFile();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
                writer.println(sdf.format("&c" + new Date()) + ": &fUse &d/mute <player> <time> <reason> &fto mute a player.");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to write to the new mutes.txt!");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create mutes.txt!");
        }
    }

    private static void makeMutes() {
        try {
            active = new RandomAccessFile(plugin.getDataFolder() + "activemutes.txt", "rw");
            plugin.getLogger().info("Creating new activemutes.txt!");
//            active.getParentFile().mkdirs();
//            active.createNewFile();
//            try (PrintWriter writer = new PrintWriter(new FileWriter(active, true), true)) {
//                writer.println("UUID");
//            } catch (IOException e) {
//                plugin.getLogger().warning("Failed to write to the new activemutes.txt!");
//            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create activemutes.txt!");
        }
    }
}
