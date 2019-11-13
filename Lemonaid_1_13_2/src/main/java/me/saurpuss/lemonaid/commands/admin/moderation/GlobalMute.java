package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class GlobalMute implements CommandExecutor {

    private Lemonaid lemonaid;

    private final String LOG_TYPE;

    public GlobalMute(Lemonaid plugin) {
        lemonaid = plugin;
        LOG_TYPE = lemonaid.getLogManager().MUTE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.globalmute")) return true;

        if (args.length == 0) {
            lemonaid.toggleGlobalMute();
        } else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
            lemonaid.toggleGlobalMute(args[0].equalsIgnoreCase("on"));
        } else {
            return false;
        }

        sender.sendMessage(ChatColor.DARK_RED + "GlobalMute " + (lemonaid.isGlobalMute() ?
                ChatColor.RED + " ON" : ChatColor.GREEN + " OFF") + ChatColor.DARK_RED +
                " for all players until next restart!");

        String log = ChatColor.BLUE + sender.getName() + (lemonaid.isGlobalMute() ?
                ChatColor.RED + " muted" : ChatColor.GREEN + " unmuted") +
                ChatColor.GOLD + " global chat!";

        lemonaid.getLogManager().addLog(LOG_TYPE, log);
        return true;
    }
}
