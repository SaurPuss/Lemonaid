package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /back Command available to to Players, requires lemonaid.teleport.back permission node.
 */
public class Back implements CommandExecutor {

    /**
     * Dependency injection of the current plugin instance.
     */
    private Lemonaid plugin;

    /**
     * Command Constructor.
     * @param plugin dependency injection of the current plugin instance.
     */
    public Back(Lemonaid plugin) {
        this.plugin = plugin;
    }

    /**
     * Attempt to add a Teleport event to the last saved location for the Command Sender.
     * If no location is saved, save the sender's current location instead.
     * @param sender Player
     * @param command back
     * @param label none
     * @param args Arguments are ignored
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Lemon user = plugin.getUser(player.getUniqueId());
            if (user.getLastLocation() == null) {
                user.setLastLocation(player.getLocation()); // save current location
                player.sendMessage(ChatColor.RED + "You don't have anywhere to go " +
                        ChatColor.GOLD + "/back " + ChatColor.RED + "to!");
                return true;
            }

            // Task the teleportManager with the BACK Teleport
            plugin.getTeleportManager().teleportEvent(
                    new Teleport(player, null, user.getLastLocation(), TeleportType.BACK));
        } else {
            // The console doesn't have a physical location in game to go back to
            sender.sendMessage(Utils.playerOnly());
        }
        return true;
    }
}
