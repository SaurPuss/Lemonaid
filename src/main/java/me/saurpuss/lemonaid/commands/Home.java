package me.saurpuss.lemonaid.commands;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {




        } else {
            if (args.length == 0) {
                sender.sendMessage(Utils.console("Only players can use this command, use /home <name> to see the homes of a player."));
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                // TODO show homes list for that player
            }
        }



        return false;
    }
}
