package me.saurpuss.lemonaid.commands.social.channels;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChat implements CommandExecutor {

    Lemonaid plugin;
    public LocalChat(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int radius = plugin.getConfig().getInt("local-chat-radius");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();

            // TODO log to console for stuff like prism


            return true;
        }

        // Sender is the console
        else {
            if (args.length <= 1) {
                sender.sendMessage("Use /l <target> <message> to send a message in the area occupied by your target.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(args[0] + " is not online.");
                return true;
            }

            Location location = target.getLocation();





            return true;
        }
    }
}
