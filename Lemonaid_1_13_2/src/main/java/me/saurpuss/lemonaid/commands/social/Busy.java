package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
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
            Lemon user = plugin.getUser(((Player) sender).getUniqueId());
            if (user.isBusy()) {
                user.setBusy(false);
                sender.sendMessage("You are no longer set to Do Not Disturb.");
            } else {
                user.setBusy(true);
                sender.sendMessage("You are set to Do Not Disturb. You will no longer receive whispers and teleport requests.");
            }
            user.updateUser();
            return true;
        } else {
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
