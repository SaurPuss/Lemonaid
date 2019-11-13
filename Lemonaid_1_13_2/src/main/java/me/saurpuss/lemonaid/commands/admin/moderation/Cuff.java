package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.logging.Level;

/**
 *
 */
public class Cuff implements CommandExecutor, PlayerSearch, Styling {

    /**
     * Dependency injection
     */
    private Lemonaid lemonaid;

    /**
     * Corresponding log type in the LogManager
     */
    private final String LOG_TYPE;

    /**
     * Constructor
     * @param plugin
     */
    public Cuff(Lemonaid plugin) {
        lemonaid = plugin;
        LOG_TYPE = lemonaid.getLogManager().CUFF;
    }

    /**
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.cuff")) return true;

        // Not enough arguments
        if (args.length == 0) return false;

        // /cuff subCommand
        else if (args.length == 1) {
            // Retrieve a recap of the last 10 cuffs
            if (args[0].equalsIgnoreCase("list")) {
                Deque<String> list = lemonaid.getLogManager().getList(LOG_TYPE);
                sender.sendMessage(ChatColor.GOLD + "[CUFF LOG] (" + list.size() + "/" +
                        lemonaid.getLogManager().getMaxSize(LOG_TYPE) + "):");
                for (String cuffLog : list)
                    sender.sendMessage(" - " + cuffLog);
                return true;
            }

            // Retrieve cuff help instructions TODO replace with /halp cuff
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
//                cuffHelp(sender, 0);
                sender.sendMessage(ChatColor.RED + "No help is coming to you!");
                return true;
            }

            // Check if args[0] equals to a valid player object
            Player target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + args[0] + "!");
                return false;
            }

            // Target is not allowed to be cuffed
            if (target.hasPermission("lemonaid.exempt.cuff")) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is exempt from being cuffed!");
                return true;
            }

            // Toggle cuff on the target
            cuff(sender, target, "");
            return true;
        }

        // /cuff subCommand args
        else {
            // TODO Get a specific page from /cuff help X, assuming it's a valid number
            // Check if args[0] equals to a valid player object
            Player target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Can't find " + args[0] + "!");
                return false;
            }

            // Target is not allowed to be cuffed
            if (target.hasPermission("lemonaid.exempt.cuff")) {
                sender.sendMessage(ChatColor.RED + target.getName() + " is exempt from being cuffed!");
                return true;
            }

            // Compile the rest of the arguments into a message and log
            String reason = StringUtils.join(args, ' ', 1, args.length);
            cuff(sender, target, color(reason));
            return true;
        }
    }

    /**
     * Cuff action to be implemented on a target
     *
     * @param sender user implementing the cuff
     * @param target target to be cuffed
     * @param reason additional arguments for logging purposes
     */
    private void cuff(CommandSender sender, Player target, String reason) {
        // Retrieve user if exists
        User user = lemonaid.getUserManager().getUser(target.getUniqueId());
        if (user == null) { // This should not happen!
            lemonaid.getLogger().log(Level.WARNING, "Error!! Lemonaid: UserNotFound!");
            sender.sendMessage(ChatColor.RED + "Failed to cuff target!");
            return;
        }

        // Log the occurrence
        String log = ChatColor.BLUE + sender.getName() + (user.isCuffed() ? ChatColor.GREEN +
                " uncuffed " : ChatColor.RED + " cuffed ") + ChatColor.LIGHT_PURPLE
                + target.getName() + ChatColor.GOLD + (reason.equals("") ? "." :
                (", reason: " + reason));

        lemonaid.getLogManager().addLog(LOG_TYPE, log);

        // Update and notify the target
        user.setCuffed(!user.isCuffed());
        lemonaid.getUserManager().updateUser(user);
        target.sendMessage( ChatColor.GOLD + "You are now " + (user.isCuffed() ? ChatColor.RED +
                "cuffed!" : ChatColor.GREEN  + "uncuffed!") + ChatColor.GOLD +
                (reason.equals("") ? "" : " Reason: §r" + color(reason)));
    }



    private void cuffHelp(CommandSender sender, int pageNumber) {
        // TODO get help from file and send it back to the sender
        sender.sendMessage(ChatColor.RED + "There is no help file right now, lol!");
    }
}
