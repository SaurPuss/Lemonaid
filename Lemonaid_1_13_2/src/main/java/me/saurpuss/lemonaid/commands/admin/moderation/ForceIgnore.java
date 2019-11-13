package me.saurpuss.lemonaid.commands.admin.moderation;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import me.saurpuss.lemonaid.utils.utility.PlayerSearch;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceIgnore implements CommandExecutor, PlayerSearch {

    private Lemonaid lemonaid;

    private final String LOG_TYPE;

    public ForceIgnore(Lemonaid plugin) {
        lemonaid = plugin;
        LOG_TYPE = lemonaid.getLogManager().RECAP;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Permission check
        if (!sender.hasPermission("lemonaid.forceignore")) return true;

        // Admin command to force an addition to a player's ignored list
        if (args.length == 2) {
            Player player = getPlayer(args[0]);
            Player target = getPlayer(args[1]);
            if (player == null || target == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Can't find " + ChatColor.RED +
                        (player == null ? args[0] : args[1]) + ChatColor.DARK_RED + "!");
                return true;
            }

            User user = lemonaid.getUserManager().getUser(player.getUniqueId());
            user.toggleIgnore(target.getUniqueId());
            lemonaid.getUserManager().updateUser(user);

            String log = ChatColor.BLUE + sender.getName() + ChatColor.GOLD + " forced " +
                    ChatColor.DARK_PURPLE + player.getName() + ChatColor.GOLD + " to " +
                    (user.isIgnored(target.getUniqueId()) ? ChatColor.RED + "ignore " :
                            ChatColor.GREEN + "unignore ") +
                    ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.GOLD + "!";

            // Recap & Notify moderators
            lemonaid.getLogManager().addLog(LOG_TYPE, log);
            return true;
        }

        return false;
    }

}
