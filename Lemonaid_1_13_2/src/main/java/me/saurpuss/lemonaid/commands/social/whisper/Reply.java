package me.saurpuss.lemonaid.commands.social.whisper;

import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reply implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {





            // TODO add error message if map is empty because player quit

            return true;
        } else {
            sender.sendMessage(Utils.console("Only players can use this command"));
            return true;
        }
    }
}
