package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reply implements CommandExecutor {

    private Lemonaid plugin;
    public Reply(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Get the player from the last incoming message
            Lemon p = plugin.getUser(player.getUniqueId());
            Player target = Bukkit.getPlayer(p.getLastMessage());
            // Only works for online players
            if (target == null) {
                player.sendMessage(Utils.color("&cCan't find " + args[0] + "."));
                return true;
            }

            Lemon t = plugin.getUser(target.getUniqueId());
            if (t.isBusy()) {
                player.sendMessage(Utils.color("&c" + args[0] + " is unavailable right now."));
                return true;
            }

            // Compile the message and update users
            String message = StringUtils.join(args, ' ', 0, args.length);
            target.sendMessage(Utils.color("&6[MSG]&c[&6" + player.getName() + "&c >> &6me&c]&f " + message));
            player.sendMessage(Utils.color("&6[MSG]&c[&6me &c >> &6" + target.getName() + "&c]&f " + message));
            t.setLastMessage(player.getUniqueId());
            t.updateUser();
            return true;
        } else {
            sender.sendMessage(Utils.playerOnly());
            return true;
        }
    }
}
