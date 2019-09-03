package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class Recap implements CommandExecutor {

    private Lemonaid plugin = Lemonaid.getInstance();
    private File file = new File(plugin.getDataFolder(), "recap.txt");
    private ArrayList<String> log = getLog();
    private Deque<String> recap = getRecap();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.recap"))) {
            if (args.length == 0) {
                // print last 10 recaps
                for (String s : recap) {
                    sender.sendMessage(s);
                }
            } else {
                String s = StringUtils.join(args, ' ', 0, args.length);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', addRecap(s)));
            }
            return true;
        } else {
            sender.sendMessage(Utils.error());
            return true;
        }
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

    private Deque<String> getRecap() {
        Deque<String> list = new LinkedList<>();
        for (int i = log.size() - 1; i > log.size() - 11; i--) {
            list.addFirst(log.get(i));
        }
        return list;
    }

    private String addRecap(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        if (recap.size() == 10)
            recap.removeLast();

        String s = sdf.format("&c" + new Date()) + ": &f" + message;
        recap.addFirst(s);
        addLog(s);
        plugin.getLogger().info("[RECAP] " + s);

        return s;
    }

    private void addLog(String message) {
        if (!file.exists())
            makeLog();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
            writer.println(message);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to log recap to recap.txt!");
        }
    }

    private void makeLog() {
        try {
            plugin.getLogger().info("Creating new recap.txt!");
            file.getParentFile().mkdirs();
            file.createNewFile();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
                writer.println(sdf.format("&c" + new Date()) + ": &fUse &d/recap <message> &fto add a recap");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to write to the new recap.txt!");
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create recap.txt!");
        }
    }
}
