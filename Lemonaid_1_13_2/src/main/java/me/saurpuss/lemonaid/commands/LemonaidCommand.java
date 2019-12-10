package me.saurpuss.lemonaid.commands;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.command.*;

public class LemonaidCommand implements CommandExecutor {

    private Lemonaid lemonaid;

    public LemonaidCommand(Lemonaid plugin) {
        lemonaid = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("lemonaid.reload")) {
            // TODO reload configs
            lemonaid.reloadConfig();

            return true;
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            // TODO do stuff

            return true;
        } else if (args[0].equalsIgnoreCase("info")) {
            // TODO do stuff

            return true;
        }

        // TODO more here?

        return true;
    }
}
