package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Cuff implements CommandExecutor {

    ArrayList<String> cuffLog = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("lemonaid.admin.cuff")) {
                if (args.length == 0) {
                    player.sendMessage(Utils.admin("Usage: /cuff <name> [time] [reason]"));



                }


            } else  {
                player.sendMessage(Utils.error());
            }
        } else {
            if (args.length == 0) {

            }
        }
        return false;
    }
}
