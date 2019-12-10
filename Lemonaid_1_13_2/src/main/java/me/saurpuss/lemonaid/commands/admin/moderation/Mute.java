package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.DateFormatting;
import me.saurpuss.lemonaid.utils.utility.PlayerSearch;
import org.apache.commons.lang.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Mute implements CommandExecutor, PlayerSearch, DateFormatting {

    // TODO add mute help / help mute
    private Lemonaid lemonaid;
    private final String LOG_TYPE;

    public Mute(Lemonaid plugin) {
        lemonaid = plugin;
        LOG_TYPE = lemonaid.getLogManager().MUTE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.mute")) return true;

        // Not enough arguments
        if (args.length == 0) return false;

        Duration duration;
        if (args.length == 1) {
            // Retrieve a recap of the last mutes
            if (args[0].equalsIgnoreCase("list")) {
                Deque<String> list = lemonaid.getLogManager().getList(LOG_TYPE);
                sender.sendMessage(ChatColor.GOLD + "[MUTE LOG] (" + list.size() + "/" +
                        lemonaid.getLogManager().getMaxSize(LOG_TYPE) + "):");
                for (String muteLog : list)
                    sender.sendMessage(" - " + muteLog);
                return true;
            }

            // Attempt to get a valid user for muting
            Player target = getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + args[0] + " not found!");
                return true;
            }

            // target is exempt
            if (target.hasPermission("lemonaid.exempt.mute")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to mute " + target.getName());
                return true;
            }

            User user = lemonaid.getUserManager().getUser(target.getUniqueId());

            // if user is muted, lift mute
            if (user.isMuted()) {
                user.setMuteEnd(System.currentTimeMillis());
                lemonaid.getUserManager().updateUser(user);

                // Log incident
                String log = ChatColor.BLUE + sender.getName() + ChatColor.GREEN + " unmuted " +
                        ChatColor.LIGHT_PURPLE + target.getName();
                lemonaid.getLogManager().addLog(LOG_TYPE, log);

                // Notify target
                target.sendMessage(ChatColor.GREEN + "Your mute has been lifted! Chat away!");
                return true;
            }

            // Mute the target permanently
            mute(sender, target, Duration.of(1, ChronoUnit.FOREVER), null);
            return true;
        }

        // Try to retrieve a valid player from the first argument
        Player target = getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + args[0] + " not found!");
            return true;
        }

        // Target is exempt
        if (target.hasPermission("lemonaid.exempt.mute")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to mute " + target.getName());
            return true;
        }

        // Check if the second argument is set to zero and create a permanent mute
        if (Integer.parseInt(args[1]) == 0) {
            mute(sender, target, Duration.of(1, ChronoUnit.FOREVER), null);
            return true;
        }

        // Check if the second argument is a valid option
        // (needs to be at least two characters long and last character needs to be a letter)
        else if (args[1].length() < 2 || !Character.isLetter(args[1].charAt(args[1].length() - 1)))
            return false;

        // Try to parse the arguments to a valid mute
        else {
            String s = "";
            ChronoUnit time = ChronoUnit.NANOS;
            // check if last char is s, m, d, y
            for (int c = 0; c < args[1].length(); c++) {
                // Add digits to a string
                if (Character.isDigit(args[1].charAt(c))) {
                    s += c;
                }
                // Convert first letter to a ChronoUnit and break the loop
                else if (Character.isLetter(args[1].charAt(c))) {
                    time = getUnit(args[1].charAt(c));
                    break;
                }
                // Invalid character: break the loop
                else {
                    return false;
                }
            }

            // Set up the variables for the mute and implement
            if (time == ChronoUnit.FOREVER)
                duration = Duration.of(1, time);
            else {
                duration = Duration.of(Integer.parseInt(s), time);
            }

            // Apply mute
            String reason = StringUtils.join(args, ' ', 2, args.length);
            mute(sender, target, duration, reason);
            return true;
        }
    }

    /**
     * Mute action to be implemented on a target
     *
     * @param sender  user implementing the mute
     * @param target  target to be muted
     * @param duration length of the mute in java Duration
     * @param reason  additional arguments for logging purposes
     */
    private void mute(CommandSender sender, Player target, Duration duration, String reason) {
        // Get a user wrapper for the target
        User user = lemonaid.getUserManager().getUser(target.getUniqueId());
        if (user == null) return; // fallback, shouldn't happen at this point

        // Get the time and day for the mute end, check if valid
        ZoneId timeZone = ZoneId.systemDefault();
        LocalDateTime end = LocalDateTime.now(timeZone).plus(duration);
        if (end.compareTo(LocalDateTime.MAX) >= 0) {
            user.setMuteEnd(LocalDateTime.MAX.atZone(timeZone).toInstant().toEpochMilli());
        } else {
            user.setMuteEnd(LocalDateTime.now().atZone(timeZone).toInstant().toEpochMilli()
                    + duration.toMillis());
        }

        user.setMuteReason(reason);
        lemonaid.getUserManager().updateUser(user);

        String log = ChatColor.BLUE + sender.getName() + ChatColor.RED + " muted " +
                ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.BLUE +
                (duration == Duration.of(1, ChronoUnit.FOREVER) ?
                        " indefinitely" : " until " + localDateTimeToString(end)) + "." +
                (reason == null ? "!" : ("! Reason: " + reason));

        lemonaid.getLogManager().addLog(LOG_TYPE, log);

        // Notify the target
        target.sendMessage(ChatColor.RED + "You are muted" +
                (duration == Duration.of(1, ChronoUnit.FOREVER) ?
                " indefinitely" : " until " + localDateTimeToString(end)) +
                (reason == null ? "!" : ("! Reason: " + reason)));
    }

    private void muteHelp(CommandSender sender, int pageNumber) {
        // TODO get help from file and send it back to the sender

        sender.sendMessage(ChatColor.RED + "There is no help file right now, lol!");
    }

    /**
     * Convenience method to translate a character to a valid ChronoUnit
     *
     * @param c preferably s, m, h, d, w, or y
     * @return applicable ChronoUnit
     */
    private ChronoUnit getUnit(char c) {
        switch (c) {
            case 's':
            case 'S':
            default:
                return ChronoUnit.SECONDS;
            case 'm':
            case 'M':
                return ChronoUnit.MINUTES;
            case 'h':
            case 'H':
                return ChronoUnit.HOURS;
            case 'd':
            case 'D':
                return ChronoUnit.DAYS;
            case 'w':
            case 'W':
                return ChronoUnit.WEEKS;
            case 'y':
            case 'Y':
                return ChronoUnit.YEARS;
        }
    }
}
