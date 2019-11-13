package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MasterCuff implements CommandExecutor {

    private Lemonaid lemonaid;

    private final String LOG_TYPE;

    public MasterCuff(Lemonaid plugin) {
        lemonaid = plugin;
        LOG_TYPE = lemonaid.getLogManager().CUFF;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.globalcuff")) return true;

        lemonaid.toggleMasterCuff();
        sender.sendMessage(ChatColor.DARK_RED + "MasterCuff " + (lemonaid.isMasterCuff() ?
                ChatColor.RED + " ON" : ChatColor.GREEN + " OFF") + ChatColor.DARK_RED +
                " for all players until next restart!");

        String log = ChatColor.BLUE + sender.getName() + (lemonaid.isMasterCuff() ?
                ChatColor.RED + " muted" : ChatColor.GREEN + " uncuffed") +
                ChatColor.GOLD + " global chat!";

        lemonaid.getLogManager().addLog(LOG_TYPE, log);

        return true;
    }
}
