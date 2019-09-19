package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.commands.admin.Cuff;
import me.saurpuss.lemonaid.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MasterCuff implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("lemonaid.admin.mastercuff")) {
            if ((args.length == 1) &&
                    ((args[0].equalsIgnoreCase("on")) ||
                    (args[0].equalsIgnoreCase("off")))) {
                boolean on = args[0].equalsIgnoreCase("on"); // on == true, off == false
                sender.sendMessage("&c" + (on ? "Cuffed" : "Uncuffed") + " all online players!!!");

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("lemonaid.admin.mastercuff") ||
                            (player.hasPermission("lemonaid.admin.notify.mastercuff")))
                        player.sendMessage(Utils.color("&c" + sender.getName() + " used MasterCuff " + args[0] + "!"));
                    else
                        Cuff.masterCuff(player, on);
                }

                Cuff.masterCuff(sender);
                return true;
            } else {
                sender.sendMessage("Usage: /mastercuff <on|off>");
                return true;
            }
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }
}
