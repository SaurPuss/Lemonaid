package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Back implements CommandExecutor {

    private Lemonaid plugin;

    public Back(Lemonaid plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Lemon user = plugin.getUser(player.getUniqueId());
            if (user.getLastLocation() == null) {
                // TODO update with legit error

                return false;
            }
            plugin.getTeleportManager().teleportEvent(new Teleport(player, null, user.getLastLocation(), TeleportType.BACK));
        } else {
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
