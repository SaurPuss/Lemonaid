package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.utils.util.Utils;
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



            return true;
        } else {
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
