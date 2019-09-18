package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tpa implements CommandExecutor {

    Lemonaid plugin;
    public Tpa(Lemonaid plugin) {
        this.plugin = plugin;
    }

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
                Lemon user = plugin.getUser(target.getUniqueId());

                if (user.isBusy()) {
                    player.sendMessage(Utils.color(target.getName() + " is busy right now."));
                    return true;
                }

                if (user.isIgnored(player.getUniqueId()))
                    return true;

                Teleport.addRequest(new Teleport(player, target, TeleportType.TPA));
            }
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
