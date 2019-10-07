package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.tp.Teleport;
import me.saurpuss.lemonaid.utils.tp.TeleportType;
import me.saurpuss.lemonaid.utils.users.Lemon;
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
            // not enough arguments
            if (args.length == 0) {
                sender.sendMessage("§6Type: §d/tpa §5<§dname§5> §6to request a teleport.");
                return true;
            }
            Player player = (Player) sender;
            // Attempt to get a teleport target
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cCan't find " + args[0]);
                return true;
            }

            // Check if target is /busy or has player /ignored
            Lemon user = plugin.getUser(target.getUniqueId());
            if (user.isBusy() || user.isIgnored(player.getUniqueId())) {
                player.sendMessage("§c" + target.getName() + " is unavailable.");
                return true;
            }

            // Start teleportation request
            Teleport.addRequest(new Teleport(player, target, TeleportType.TPA));
            return true;
        } else { // Console can't teleport
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
