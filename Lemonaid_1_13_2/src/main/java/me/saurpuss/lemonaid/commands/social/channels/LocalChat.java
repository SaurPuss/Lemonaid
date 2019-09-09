package me.saurpuss.lemonaid.commands.social.channels;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChat implements CommandExecutor {

    Lemonaid plugin = Lemonaid.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Sender is a player
        if (sender instanceof Player) {
            Player player = (Player) sender;

            World world = player.getWorld();
            Location location = player.getLocation();
            int radius = 80; // TODO get from config

            // TODO log to console for stuff like prism



        }
        // Sender is the console
        else {
            if (args.length <= 1) {
                sender.sendMessage(Utils.playerOnly("Use /l <target> <message> to send a message in the area occupied by your target."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Utils.playerOnly(args[0] + " is not online."));
                return true;
            }

            World world = target.getWorld();
            Location location = target.getLocation();
            int radius = 80; // TODO get from config




        }


        return false;
    }
}
