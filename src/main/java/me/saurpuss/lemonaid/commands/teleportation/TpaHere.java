package me.saurpuss.lemonaid.commands.teleportation;

import me.saurpuss.lemonaid.utils.util.Teleportation;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaHere implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(Utils.tpa("Type: /tpahere <name> to request a teleport."));
                return true;
            }

            // TODO add multiple requests

            // TODO check if target player has outgoing requests with other players (is key in hashmap)


            return true;
        } else {
            return Teleportation.isConsole(sender);
        }

    }
}