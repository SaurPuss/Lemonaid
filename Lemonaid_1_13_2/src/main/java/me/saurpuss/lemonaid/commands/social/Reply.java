package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import me.saurpuss.lemonaid.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
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
            Lemon p = plugin.getUser(player.getUniqueId());
            Player target = Bukkit.getPlayer(p.getLastMessage());

            if (target == null) {
                player.sendMessage(Utils.color("&cCan't find " + args[0] + "."));
                return true;
            }

            Lemon t = plugin.getUser(target.getUniqueId());
            if (t.isBusy()) {
                player.sendMessage(Utils.color("&cCan't find " + args[0] + "."));
                return true;
            }

            String message = StringUtils.join(args, ' ', 0, args.length);

            // TODO color perm nodes
            target.sendMessage(Utils.color("&6[MSG]&c[&6" + player.getName() + "&c >> &6me&c]&f " + message));
            player.sendMessage(Utils.color("&6[MSG]&c[&6me &c >> &6" + target.getName() + "&c]&f " + message));
            t.setLastMessage(player.getUniqueId());
            t.updateUser();
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
