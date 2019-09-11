package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.utils.teleport.*;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class Tpa implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(Utils.color("&6Type: /tpa <name> to request a teleport."));
            } else {
                Player player = (Player) sender;
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Utils.color("&cCan't find " + args[0]));
                    return true;
                }

                Teleport.addRequest(new Teleport(player, target, TeleportType.TPA));
            }
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
