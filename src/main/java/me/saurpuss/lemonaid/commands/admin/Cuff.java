package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cuff implements CommandExecutor {

    Lemonaid plugin = Lemonaid.getInstance();
    public static ArrayList<String> cuffLog = new ArrayList<>();
    private static HashMap<Player, Date> cuffedPlayers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.cuff"))) {
            if (args.length == 0) {
                sender.sendMessage(Utils.admin("Usage: /cuff <name> [time] [reason]"));
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    OfflinePlayer[] list = Bukkit.getOfflinePlayers();
                    for (OfflinePlayer p : list) {
                        if (p.getPlayer().getName().equalsIgnoreCase(args[0])) {
                            target = p.getPlayer();
                        }
                    }

                    sender.sendMessage(Utils.admin(args[0] + " not found."));
                    return true;
                }
                // target is now defined
                if (args.length == 1) {
                    return cuffAction(target, 0, '0', null);
                } else {
                    if (args[1].equals("0")) {
                        return cuffAction(target, 0, '0', null);
                    } else {
                        if (!Character.isLetter(args[1].charAt(args[1].length() - 1))) {
                            sender.sendMessage(Utils.admin("Usage: /cuff <player> 0, replace 0 with 1m, 10d, etc."));
                            return true;
                        } else {
                            String s = "";
                            char[] chars = {'s', 'S', 'm', 'M', 'h', 'H', 'd', 'D', 'y', 'Y'};
                            char time = '0';
                            // check if last char is s, m, d, y
                            for (char c = 0; c < args[1].length(); c++) {
                                if (Character.isDigit(c)) {
                                    s += c;
                                } else if (Character.isLetter(c)) {
                                    for (char x : chars)
                                        if (c == x)
                                            time = c;
                                    break;
                                } else {
                                    sender.sendMessage(Utils.admin("Usage: /cuff <player> t, replace t with 0, 1m, 10d, etc."));
                                    return true;
                                }
                            }
                            return cuffAction(target, Integer.valueOf(s), time, null);
                        }

                        // check if chars before it are an int


                    }


                }

            }


            return true;
        } else {
            sender.sendMessage(Utils.error());
            return true;
        }
    }

    private boolean cuff(Player player, Date date) {
        if (!cuffedPlayers.containsKey(player)) {
            cuffedPlayers.put(player, date);

            if (date == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.admin(player.getName() + " is cuffed."));
                }
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.admin(player.getName() + " cuffed until " + date.toString() + "."));
                }
            }
        } else {
            cuffedPlayers.remove(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.admin.notify.cuff"))
                    p.sendMessage(Utils.admin(player.getName() + " is uncuffed."));
            }
        }
        return true;
    }

    private boolean cuffAction(Player player, int n, char amount, String reason) {
        if (n == 0)
            return cuff(player, null);

        long multiplier = 0;
        boolean date = false;
        switch (amount) {
            case 'm': case 'M': multiplier = 60; break;
            case 'h': case 'H': multiplier = 3600; break;
            case 's': case 'S': multiplier = 1; break;
            case 'd': case 'D': multiplier = 86400; date = true; break;
            case 'y': case 'Y': multiplier = 31536000; date = true; break;
            default: return cuff(player, null);
        }

        // Schedule release
        long addition = n * multiplier;
        if (!date) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                cuffedPlayers.remove(player);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.admin(player.getName() + " is uncuffed."));
                }
            }, 20 * multiplier * n);
        }

        cuff(player, new Date(System.currentTimeMillis() + (addition * 1000)));

        // TODO add log
        logCuff(reason);


        return true;
    }

    public static void cleanCuffList() {
        for (Map.Entry<Player, Date> entry : cuffedPlayers.entrySet()) {
            if ((entry.getValue() != null) && (entry.getValue().compareTo(new Date()) <= 0)) {
                cuffedPlayers.remove(entry.getKey());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.admin(entry.getKey().getName() + " is uncuffed."));
                }
            }
        }
    }

    private boolean logCuff(String log) {
        // TODO make this a viable log

        return true;
    }
}
