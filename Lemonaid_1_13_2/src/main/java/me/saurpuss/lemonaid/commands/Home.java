package me.saurpuss.lemonaid.commands;

import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;


            return true;
        } else {
            if (args.length == 0) {
                sender.sendMessage("Only players can use this command, use /home <name> to see the homes of a player.");
                return true;
            } else {
                Player target = Utils.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage("Can't find " + args[0]);
                    return true;
                }

                // TODO show homes list for that player


                return true;
            }
        }
    }
}
