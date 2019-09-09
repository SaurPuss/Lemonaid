package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.config.HomesConfig;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Home implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                // Check if there are homes available
                List<String> homes;






            }


            if (label.equalsIgnoreCase("sethome")) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Type /sethome <home>");
                }

            } else if (label.equalsIgnoreCase("delhome")) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Type /delhome <home>");
                    return true;
                }

            } else {

            }
            return true;
        } else {
            // TODO set and delete homes for players
            sender.sendMessage(Utils.console("Only players can use this command"));
            return true;
        }
    }
}
