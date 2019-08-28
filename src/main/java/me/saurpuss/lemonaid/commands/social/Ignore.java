package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Ignore implements CommandExecutor {

    public static HashMap<Player, Player> ignoredList = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {




        } else {
            if (args.length == 0) {
                sender.sendMessage(Utils.console("Only players can use this command, use /ignore <name> to see the ignore list of a player."));
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                // TODO show homes list for that player
            }
        }



        return false;
    }
}
