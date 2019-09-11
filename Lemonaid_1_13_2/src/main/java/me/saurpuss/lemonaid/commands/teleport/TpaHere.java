package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.teleport.*;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpaHere implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                // TODO add colors to command bit
                sender.sendMessage(Utils.color("&6Type: /tpahere <name> to request a teleport."));
                return true;
            } else {
                Player player = (Player) sender;
                // Compile a list of valid targets
                HashSet<Player> list = new HashSet<>();
                for (String arg : args) {
                    Player target = Bukkit.getPlayer(arg);
                    if (target == null) {
                        player.sendMessage(Utils.color("&cCan't find " + arg));
                    } else {
                        list.add(target);
                    }
                }
                // No valid targets
                if (list.isEmpty())
                    return true;

                // Send tpahere requests
                for (Player target : list)
                    Teleport.addRequest(new Teleport(player, target, TeleportType.TPAHERE));
            }
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}