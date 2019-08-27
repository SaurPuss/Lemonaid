package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpaHere implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(Utils.tpa("Type: /tpahere <name> to request a teleport."));
                return true;
            } else {
                // Compile a list of valid targets
                HashSet<Player> list = new HashSet<>();
                for (String arg : args) {
                    Player target = Bukkit.getPlayer(arg);
                    if (target == null) {
                        player.sendMessage(Utils.tpa("Can't find " + arg));
                    } else {
                        list.add(target);
                    }
                }
                // No valid targets
                if (list.isEmpty()) {
                    return true;
                }

                // Send out requests
                for (Player target : list)
                    Teleport.addRequest(new Teleport(player, target, TeleportType.TPAHERE));
                return true;
            }
        } else {
            return Teleport.isConsole(sender);
        }
    }
}