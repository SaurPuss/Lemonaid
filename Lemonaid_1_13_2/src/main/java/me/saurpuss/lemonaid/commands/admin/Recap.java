package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Information hand-off in a small server side package
 */
public class Recap implements CommandExecutor {

    private Lemonaid plugin;
    private final File file = new File(plugin.getDataFolder(), "recap.txt");
    private Deque<String> recap = getRecap();

    /**
     * Constructor for internal use by the main class
     * @param plugin Get the instance of Lemonaid running on the server
     */
    public Recap(Lemonaid plugin) {
        this.plugin = plugin;
    }

    /**
     * Command executing /recap or /recap [message].
     * @param sender Console or a player with admin level permission
     * @param command /recap
     * @param label recap
     * @param args message
     * @return true - recap results
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lemonaid.recap")) return true;

        // print last 10 recaps
        if (args.length == 0) {
            recap.forEach((message) -> sender.sendMessage(Utils.color(message)));
            return true;
        }

        // add new recap
        String message = StringUtils.join(args, ' ', 0, args.length);
        sender.sendMessage(Utils.color(addRecap(message)));
        return true;
    }

    /**
     * Get last 10 logs from recap.txt and add them to the log Deque
     * @return Linked List with String recaps
     */
    private Deque<String> getRecap() {
        // Make sure the file exists
        if (!file.exists())
            makeLog();

        // Read recap.txt and add to an ArrayList
        ArrayList<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read recap.txt!");
        }

        // Read the last 10 entries from the ArrayList
        Deque<String> log = new LinkedList<>();
        for (int i = log.size() - 1; i > log.size() - 11; i--) {
            log.addFirst(list.get(i));
        }

        // Return results
        return log;
    }

    /**
     * Add a recap to the logs and recap.txt in the plugin folder.
     * @param message Recap worthy String input
     * @return "MONTH XX: Recap worthy String input"
     */
    private String addRecap(String message) {
        // Check for file
        if (!file.exists())
            makeLog();

        // Check current recap
        if (recap.size() == 10)
            recap.removeLast();

        // Add today to the recap before processing
        String s = "§c" + Utils.dateToString(LocalDate.now()) + "§f: " + Utils.color(message);

        // Add recap to deque and recap.txt
        recap.addFirst(s);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
            writer.println(s);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to log recap to recap.txt!");
        }

        // Log to the console and return the formatted recap message
        plugin.getLogger().info("[RECAP] " + s);
        return s;
    }

    /**
     * Create a recap.txt in the config folder for saving recaps.
     */
    private void makeLog() {
        try {
            // Make file and required directories
            plugin.getLogger().info("Creating new recap.txt in the plugin folder!");
            file.getParentFile().mkdirs();
            file.createNewFile();

            // Try to write to the newly created file
            PrintWriter writer = new PrintWriter(new FileWriter(file, true), true);
            writer.println("§c" + Utils.dateToString(LocalDate.now()) + "§f: Use §d/recap <message> §fto add a recap");
        } catch (IOException e) {
            plugin.getLogger().warning("Error while creating recap.txt!!");
        }
    }
}