package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.Teleport;
import me.saurpuss.lemonaid.utils.TeleportType;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TpaHere implements CommandExecutor {

    Lemonaid plugin;

    public TpaHere(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                // TODO add colors to command bit
                sender.sendMessage(Utils.color("&6Type: /tpahere <name> to request a teleport."));
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
                for (Player target : list) {
                    Lemon user = plugin.getUser(target.getUniqueId());
                    if (user.isBusy()) {
                        player.sendMessage(Utils.color(target.getName() + " is busy right now."));
                        return true;
                    }
                    if (user.isIgnored(player.getUniqueId()))
                        return true;

                    Teleport.addRequest(new Teleport(player, target, TeleportType.TPAHERE));
                }
            }
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}