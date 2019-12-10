package me.saurpuss.lemonaid.commands.social;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Welcome implements CommandExecutor {

    private Lemonaid lemonaid;
    private FileConfiguration config;

    public Welcome(Lemonaid plugin) {
        lemonaid = plugin;
        config = lemonaid.getConfig(); // TODO maybe just grab the list of options or the section
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String lastJoined = lemonaid.getUserManager().getLastJoinedPlayer();




        return false;
    }
}
