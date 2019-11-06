package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Busy implements CommandExecutor {

    Lemonaid plugin;
    public Busy(Lemonaid plugin) {
        this.plugin = plugin;
    }

    /**
     * Allow a user to toggle their DnD mode
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Retrieve matching user from userManager
            User user = plugin.getUser(player.getUniqueId());
            if (user.isBusy()) {
                user.setBusy(false);
                sender.sendMessage(ChatColor.DARK_PURPLE + "You are no longer set to Do Not Disturb.");
            } else {
                user.setBusy(true);
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "You are now set to Do Not Disturb!");
            }

            user.updateUser();
            return true;
        }

        sender.sendMessage(Utils.playerOnly());
        return true;
    }
}
