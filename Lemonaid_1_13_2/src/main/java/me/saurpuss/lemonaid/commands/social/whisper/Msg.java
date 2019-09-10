package me.saurpuss.lemonaid.commands.social.whisper;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Msg implements CommandExecutor {

    Lemonaid plugin = Lemonaid.getInstance();
    // TODO move to util?
    private Map<UUID, UUID> lastMessage = new HashMap<>();

    /**
     * Send a private message to another player.
     * Logged in the console using:
     *      [MSG] {sender >> receiver} message
     * @param sender Command Sender
     * @param command /mgs, /tell, /whisper
     * @param label msg, tell, whisper
     * @param args target message...
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // The sender is another player
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length <= 1) {
                player.sendMessage(Utils.color("&cType: /msg <player> <message>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Utils.color("&c" + args[0] + " is not online."));
                return true;
            }

            String message = StringUtils.join(args, ' ', 1, args.length);

            setLastMessage(player.getUniqueId(), target.getUniqueId());
            // TODO lemonaid.chat.colors check and translation
            target.sendMessage(Utils.color("&6[MSG]&c[&6" + player.getName() + "&c >> &6me&c]&f " + message));
            player.sendMessage(Utils.color("&6[MSG]&c[&6me &c >> &6" + target.getName() + "&c]&f " + message));

            // Log a human readable copy in the console
            // TODO social spy copy for admins
            plugin.getLogger().info("[MSG] {" + player.getName() + " >> " + target.getName() + "} " + message);

            return true;
        }
        // The sender is the console
        else {
            if (args.length <= 1) {
                sender.sendMessage("Usage: /msg <player> <message>");
                return true;
            } else {
                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage(args[0] + " is not online.");
                    return true;
                }

                String message = StringUtils.join(args, ' ', 1, args.length);

                target.sendMessage(Utils.color("&e[MSG] >> &6 " + message));
                sender.sendMessage("[MSG] >> "+ target.getName() + ": " + message);
                return true;
            }
        }
    }

    UUID getLastMessage(UUID uuid) {
        return lastMessage.get(uuid);
    }

    void setLastMessage(UUID sender, UUID receiver) {
        this.lastMessage.put(receiver, sender);
//        this.lastMessage.put(sender, receiver);
    }

    // TODO remove on player quit event to save memory
    public void removeLastMessage(UUID player) {

    }
}
