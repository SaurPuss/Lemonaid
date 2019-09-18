package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class Busy implements CommandExecutor {

    Lemonaid plugin;

    public Busy(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Lemon user = plugin.getUser(((Player) sender).getUniqueId());
            if (user.isBusy()) {
                user.setBusy(false);
                sender.sendMessage("You can now receive whispers and teleport requests.");
            } else {
                user.setBusy(true);
                sender.sendMessage("You will no longer receive whispers and teleport requests.");
            }
            user.updateUser();
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
