package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor {

    /**
     * Toggle a player or a target player's fly mode.
     * @param sender Command Sender
     * @param command /fly or /fly target
     * @param label fly
     * @param args target player to toggle fly on
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // This can only be used by admins
            if (player.hasPermission("lemonaid.admin.fly")) {
                // /fly <target>
                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);

                    if (target == null) {
                        player.sendMessage(Utils.chat(args[0] + " is not a valid player."));
                        return true;
                    }

                    if (target.isFlying()) {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        player.sendMessage(Utils.chat(target.getDisplayName() + " can no longer fly!"));
                        target.sendMessage(Utils.chat("You can no longer fly!"));
                        return true;
                    } else {
                        target.setAllowFlight(true);
//                        target.setFlying(true);
                        player.sendMessage(Utils.chat(target.getDisplayName() + " is now flying!"));
                        target.sendMessage(Utils.chat("You can now fly!"));
                        return true;
                    }
                }
                // fly
                else {
                    if (player.isFlying()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.sendMessage(Utils.chat("You are no longer flying."));
                        return true;
                    } else {
                        player.setAllowFlight(true);
//                        player.setFlying(true);
                        player.sendMessage(Utils.chat("You can now fly!"));
                        return true;
                    }
                }
            }
            // For the plebs trying to be OP
            else {
                player.sendMessage(Utils.error());
                return true;
            }
        }
        // The command is sent from the console
        else {
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(Utils.console(args[0] + " is not a valid player."));
                    return true;
                }

                if (target.isFlying()) {
                    target.setAllowFlight(false);
                    target.setFlying(false);
                    sender.sendMessage(Utils.console(target.getDisplayName() + " can no longer fly!"));
                    target.sendMessage(Utils.chat("You can no longer fly!"));
                    return true;
                } else {
                    target.setAllowFlight(true);
//                        target.setFlying(true);
                    sender.sendMessage(Utils.console(target.getDisplayName() + " is now flying!"));
                    target.sendMessage(Utils.chat("You can now fly!"));
                    return true;
                }
            }

            sender.sendMessage(Utils.console("Only players can use this command. Try /fly <target>."));
            return true;
        }
    }
}
