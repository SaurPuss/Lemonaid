package me.saurpuss.lemonaid.commands;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Home implements CommandExecutor {

    HashMap<Player, HomeSets> homes = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;






        } else {
            if (args.length == 0) {
                sender.sendMessage(Utils.playerOnly("Only players can use this command, use /home <name> to see the homes of a player."));
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                // TODO show homes list for that player
            }
        }



        return false;
    }

    class HomeSets {

        Player player;
        int limit;
        ArrayList<Location> homes;


        HomeSets() {

        }


    }
}
