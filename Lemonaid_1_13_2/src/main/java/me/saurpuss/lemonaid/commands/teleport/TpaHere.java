package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Teleport;
import me.saurpuss.lemonaid.utils.TeleportType;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
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
            // Not enough arguments
            if (args.length == 0) {
                sender.sendMessage("§6Type: §d/tpahere §5<§dname§5> §6to request a teleport.");
                return true;
            }

            Player player = (Player) sender;
            // Compile a list of valid targets
            HashSet<Player> list = new HashSet<>();
            for (String arg : args) {
                Player target = Bukkit.getPlayer(arg);
                if (target == null) {
                    player.sendMessage("§cCan't find " + arg);
                } else {
                    list.add(target);
                }
            }

            // No valid targets
            if (list.isEmpty()) return true;

            // Send tpahere requests to available targets
            for (Player target : list) {
                Lemon user = plugin.getUser(target.getUniqueId());
                if (user.isBusy() || user.isIgnored(player.getUniqueId())) {
                    player.sendMessage("§c" + target.getName() + " is unavailable.");
                } else {
                    Teleport.addRequest(new Teleport(player, target, TeleportType.TPAHERE));
                }
            }
            return true;
        } else {
            // Console can't do this
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}