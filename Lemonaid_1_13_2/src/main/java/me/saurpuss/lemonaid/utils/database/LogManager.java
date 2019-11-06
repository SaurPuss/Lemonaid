package me.saurpuss.lemonaid.utils.database;

import me.saurpuss.lemonaid.Lemonaid;

import java.io.File;

public class LogManager {

    private Lemonaid plugin;
    private File cuffLog;
    private File muteLog;
    private boolean appendLogs;

    // TODO maybe work with stacks but fifo? instead of dequeue?

    public LogManager(Lemonaid pl) {
        this.plugin = pl;

        cuffLog = new File(plugin.getDataFolder(), "cuffLog.txt");
        muteLog = new File(plugin.getDataFolder(), "muteLog.txt");

        appendLogs = plugin.getConfig().getBoolean("append-logs");
    }











}
