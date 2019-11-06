package me.saurpuss.lemonaid.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.apache.commons.lang.StringUtils;

/**
 * Bring the message of the gods to all who can hear
 */
public class Broadcast implements CommandExecutor {

    /**
     * Broadcast a message to the entire server
     * @param sender Executor of the broadcast command
     * @param command Command sent
     * @param label Label of the command sent
     * @param args Message to be broadcast to all online players and the console
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        // Broadcast the message to all players and the console
        String message = StringUtils.join(args, ' ', 0, args.length);
        Bukkit.broadcastMessage("§c[§eBROADCAST§c]§6 " + Utils.color(message));
        return true;
    }
}
