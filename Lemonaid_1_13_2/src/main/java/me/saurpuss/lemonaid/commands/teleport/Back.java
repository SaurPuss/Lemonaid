package me.saurpuss.lemonaid.commands.teleport;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.teleport.Teleport;
import me.saurpuss.lemonaid.utils.teleport.TeleportType;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PermissionMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /back Command available to to Players, requires lemonaid.teleport.back permission node.
 */
public class Back implements CommandExecutor, PermissionMessages {

    /**
     * Dependency injection of the current plugin instance.
     */
    private Lemonaid lemonaid;

    /**
     * Command Constructor.
     * @param plugin dependency injection of the current plugin instance.
     */
    public Back(Lemonaid plugin) {
        lemonaid = plugin;
    }

    /**
     * Attempt to add a Teleport event with the last saved location for the CommandSender.
     * If no location is saved, save the sender's current location instead.
     * @param sender Player issuing the command
     * @param command /back
     * @param label none
     * @param args Arguments are ignored
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = lemonaid.getUserManager().getUser(player.getUniqueId());

            if (user.getLastLocation() == null) {
                user.setLastLocation(player.getLocation());
                lemonaid.getUserManager().updateUser(user);

                player.sendMessage(ChatColor.RED + "You don't have anywhere to go " +
                        ChatColor.GOLD + "/back " + ChatColor.RED + "to!");
                return true;
            }

            lemonaid.getTeleportManager().addRequest(
                    new Teleport(player, null, user.getLastLocation(), TeleportType.BACK));
        }
        else
            sender.sendMessage(playerOnly());

        return true;
    }
}
