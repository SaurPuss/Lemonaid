package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Information hand-off in a small server side package
 */
public class Recap implements CommandExecutor {

    private Lemonaid lemonaid;
    private final String LOG_TYPE;

    /**
     * Constructor for internal use by the main class
     * @param plugin Get the instance of Lemonaid running on the server
     */
    public Recap(Lemonaid plugin) {
        lemonaid = plugin;
        LOG_TYPE = lemonaid.getLogManager().RECAP;
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

        // print last x recaps
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("list"))) {
            Deque<String> list = lemonaid.getLogManager().getList(LOG_TYPE);
            sender.sendMessage(ChatColor.GOLD + "[RECAP LOG] (" + list.size() + "/" +
                    lemonaid.getLogManager().getMaxSize(LOG_TYPE) + "):");
            for (String recapLog : list)
                sender.sendMessage(" - " + recapLog);
            return true;
        }

        // add new recap
        String message = StringUtils.join(args, ' ', 0, args.length);
        lemonaid.getLogManager().addLog(LOG_TYPE, ChatColor.BLUE + sender.getName() +
                ChatColor.GOLD + " - " + ChatColor.RESET + message);
        return true;
    }
}