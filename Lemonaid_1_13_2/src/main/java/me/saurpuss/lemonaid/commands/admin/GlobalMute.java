package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalMute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("lemonaid.admin.globalmute")) {
            sender.sendMessage("&cApplying global mute to all players until next restart!");
            Lemonaid.toggleGlobalMute();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("lemonaid.admin.globalmute") ||
                        (player.hasPermission("lemonaid.admin.notify.globalmute")))
                    player.sendMessage(Utils.color("&c" + sender.getName() + " used Global Mute!"));
            }

            return true;
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }
}
