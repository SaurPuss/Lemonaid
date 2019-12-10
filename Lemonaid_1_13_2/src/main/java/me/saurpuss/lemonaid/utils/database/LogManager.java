package me.saurpuss.lemonaid.utils.database;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.utility.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

public class LogManager implements DateFormatting, PermissionMessages, Styling {

    private Lemonaid lemonaid;

    // files
    private File cuffFile;
    private File muteFile;
    private File recapFile;
    private final FileConfiguration config;
    private final String path;
//    log:
//      append: true
//      to-recap: true
//      size:
//          cuff: 10
//          mute: 10
//          recap: 15
//      date-format: 'MMM dd'

    // constants
    public final String CUFF = "CUFF";
    public final String MUTE = "MUTE";
    public final String RECAP = "RECAP";

    // queueueueueueue
    private Deque<String> cuffList;
    private Deque<String> muteList;
    private Deque<String> recapList;

    public LogManager(Lemonaid plugin) {
        lemonaid = plugin;
        config = lemonaid.getConfig();
        path = lemonaid.getDataFolder() + "log/"; // TODO check if valid

        // Set up (pre-existing) logs
        cuffFile = new File(path, "cuffLog.txt");
        muteFile = new File(path, "muteLog.txt");
        recapFile = new File(path, "recapLog.txt");
        // TODO add mkdirs!!

        cuffList = readLog(CUFF);
        muteList = readLog(MUTE);
        recapList = readLog(RECAP);
    }

    public Deque<String> getList(String choice) {
        switch (choice) {
            case CUFF:
                if (cuffList == null || cuffList.isEmpty()) readLog(CUFF);
                return cuffList;
            case MUTE:
                if (muteList == null || muteList.isEmpty()) readLog(MUTE);
                return muteList;
            case RECAP:
                if (recapList == null || recapList.isEmpty()) readLog(RECAP);
                return recapList;
            default:
                return null;
        }
    }

    public int getMaxSize(String choice) {
        int size = 5;

        switch (choice) {
            case CUFF:
                size = config.getInt("log.size.cuff");
                if (size < 5) size = 5;
                break;
            case MUTE:
                size = config.getInt("log.size.mute");
                if (size < 5) size = 5;
                break;
            case RECAP:
                size = config.getInt("log.size.recap");
                if (size < 10) size = 10;
        }

        return size;
    }

    public boolean addLog(String choice, String logMessage) {
        File file;
        Deque<String> list;
        int maxSize = getMaxSize(choice);
        String dateFormat = config.getString("log.date-format");
        String log = ChatColor.DARK_AQUA + dateToString(LocalDate.now(), dateFormat) +
                ChatColor.GOLD + ": " + color(logMessage);

        switch (choice.toUpperCase()) {
            case CUFF:
                file = cuffFile;
                list = cuffList;
                break;
            case MUTE:
                file = muteFile;
                list = muteList;
                break;
            case RECAP:
                file = recapFile;
                list = recapList;
                break;
            default:
                return false;
        }

        // Update linked list
        list.addFirst(log);
        if (list.size() > maxSize)
            list.removeLast();

        if (config.getBoolean("log.append")) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
                writer.println(log);
            } catch (IOException ex) {
                lemonaid.getLogger().log(Level.SEVERE, "Could not write to " + file, ex);
                return false;
            }
        } else {
            saveLog(choice);
        }

        // Also recursively add to recap
        if (!choice.equals(RECAP))
            return addLog(RECAP, logMessage);
        else
            adminNotification(logMessage);

        return true;
    }

    private Deque<String> readLog(String choice) {
        File file;
        String newFileMessage;
        int maxEntries = getMaxSize(choice);
        String dateFormat = config.getString("log.date-format");

        switch (choice) {
            case CUFF:
                file = cuffFile;
                newFileMessage = ChatColor.DARK_AQUA + dateToString(LocalDate.now(), dateFormat) +
                        ChatColor.GOLD + ": " + ChatColor.BLUE + "Start of cuff log! Use " +
                        ChatColor.LIGHT_PURPLE + "/cuff <player> [reason...] " +
                        ChatColor.BLUE + "to add a cuff to the log!";
                break;
            case MUTE:
                file = muteFile;
                newFileMessage = ChatColor.DARK_AQUA + dateToString(LocalDate.now(), dateFormat) +
                        ChatColor.GOLD + ": " + ChatColor.BLUE + "Start of mute log! Use" +
                        ChatColor.LIGHT_PURPLE + "Use §d/mute <player> <time> [reason...]" +
                        ChatColor.BLUE + " to apply a mute and add it to the log!";
                break;
            case RECAP:
                file = recapFile;
                newFileMessage = ChatColor.DARK_AQUA + dateToString(LocalDate.now(), dateFormat) +
                        ChatColor.GOLD + ": " + ChatColor.BLUE + "Start of recap log! Use " +
                        ChatColor.LIGHT_PURPLE + "/recap [message...]" +
                        ChatColor.BLUE + " to apply a mute and add it to the log!";
                break;
            default:
                return null;
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                lemonaid.getLogger().log(Level.SEVERE, "Could not write to " + file, ex);
                return null;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file, false), true)) {
                // Aug 02: Start of cuff log! Use /cuff <player> [reason...] to add a cuff to the Log!
                writer.println(newFileMessage);
            } catch (IOException ex) {
                lemonaid.getLogger().log(Level.SEVERE, "Could not write to " + cuffFile, ex);
                return null;
            }
        }

        ArrayList<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null)
                list.add(line);
        } catch (IOException ex) {
            lemonaid.getLogger().log(Level.SEVERE, "Failed to read " + file, ex);
            return null;
        }

        // Reverse order of logs if append is on
        if (config.getBoolean("log.append"))
            list.sort(Collections.reverseOrder());

        // Read the last x entries from the ArrayList into a LinkedList
        Deque<String> log = new LinkedList<>();
        for (int i = 0; i < maxEntries && i < list.size(); i++)
            log.add(list.get(i));

        return log;
    }

    public void saveLogs() {
        saveLog(CUFF);
        saveLog(MUTE);
        saveLog(RECAP);
    }

    private void saveLog(String choice) {
        if (config.getBoolean("log.append")) return;

        File file;
        Deque<String> list;
        switch (choice) {
            case CUFF:
                file = cuffFile;
                list = cuffList;
                break;
            case MUTE:
                file = muteFile;
                list = muteList;
                break;
            case RECAP:
                file = recapFile;
                list = recapList;
                break;
            default:
                return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false), true)) {
            list.forEach(writer::println);
        } catch (IOException ex) {
            lemonaid.getLogger().log(Level.SEVERE, "Could not write to " + file, ex);
        }
    }
}
