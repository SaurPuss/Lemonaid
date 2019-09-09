package me.saurpuss.lemonaid.commands.admin;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Cuff implements CommandExecutor {

    static Lemonaid plugin = Lemonaid.getInstance();
    // TODO save and retrieve log from file instead of arraylist
    public static ArrayList<CuffLog> cuffLog = new ArrayList<>();
    // TODO save and retrieve map from file
    private static HashMap<Player, Date> cuffedPlayers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!(sender instanceof Player)) || (sender.hasPermission("lemonaid.admin.cuff"))) {
            if (args.length == 0) {
                sender.sendMessage(Utils.color("Usage: /cuff <name> [time] [reason]"));
                return true;
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    // Check if the player is offline
                    OfflinePlayer[] list = Bukkit.getOfflinePlayers();
                    for (OfflinePlayer p : list) {
                        if (p.getPlayer().getName().equalsIgnoreCase(args[0])) {
                            target = p.getPlayer();
                        }
                    }
                    // The player name was probably a typo
                    if (target == null) {
                        sender.sendMessage(Utils.color(args[0] + " not found."));
                        return true;
                    }
                }
                // target is now defined
                if (args.length == 1) {
                    return cuffAction(sender, target, 0, '0', null);
                } else {
                    if (args[1].equals("0")) {
                        String reason = null;
                        if (args.length >= 3) {
                            reason = StringUtils.join(args, ' ', 2, args.length);
                        }
                        return cuffAction(sender, target, 0, '0', reason);
                    } else {
                        if (!Character.isLetter(args[1].charAt(args[1].length() - 1))) {
                            sender.sendMessage(Utils.color("Usage: /cuff <player> 0, replace 0 with 1m, 10d, etc."));
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
                                    sender.sendMessage(Utils.color("Usage: /cuff <player> t, replace t with 0, 1m, 10d, etc."));
                                    return true;
                                }
                            }

                            // TODO check if correct
                            String reason = null;
                            if (args.length >= 3) {
                                reason = StringUtils.join(args, ' ', 2, args.length);
                            }
                            return cuffAction(sender, target, Integer.valueOf(s), time, reason);
                        }
                    }
                }
            }
        } else {
            sender.sendMessage(Utils.noPermission());
            return true;
        }
    }

    private boolean cuff(Player player, Date date) {
        if (!cuffedPlayers.containsKey(player)) {
            cuffedPlayers.put(player, date);

            if (date == null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.color(player.getName() + " is cuffed."));
                }
                plugin.getLogger().info(player.getName() + " is cuffed.");
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.color(player.getName() + " cuffed until " + date.toString() + "."));
                }
                plugin.getLogger().info(player.getName() + " cuffed until " + date.toString() + ".");
            }
        } else {
            cuffedPlayers.remove(player);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("lemonaid.admin.notify.cuff"))
                    p.sendMessage(Utils.color(player.getName() + " is uncuffed."));
            }
            plugin.getLogger().info(player.getName() + " is uncuffed.");
        }
        return true;
    }

    private boolean cuffAction(CommandSender sender, Player player, int n, char amount, String reason) {
        if (n == 0) {
            CuffLog log = new CuffLog(player, new Date(), null, (Player) sender, reason);
            log.saveLog();
            return cuff(player, null);
        }

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
                        p.sendMessage(Utils.color(player.getName() + " is uncuffed."));
                }
            }, 20 * multiplier * n);
        }
        Date temp = new Date(System.currentTimeMillis() + (addition * 1000));
        cuff(player, temp);

        CuffLog log = new CuffLog(player, new Date(), temp, (Player)sender, reason);
        log.saveLog();


        return true;
    }

    public static void cleanCuffList() {
        for (Map.Entry<Player, Date> entry : cuffedPlayers.entrySet()) {
            if ((entry.getValue() != null) && (entry.getValue().compareTo(new Date()) <= 0)) {
                cuffedPlayers.remove(entry.getKey());
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("lemonaid.admin.notify.cuff"))
                        p.sendMessage(Utils.color(entry.getKey().getName() + " is uncuffed."));
                }
                plugin.getLogger().info(entry.getKey().getName() + " is cuffed.");
            }
        }
    }

    public static void saveCuffList() {
        // TODO save current cuff list to file
    }
    public static void loadCuffList() {
        // TODO get cuffs from the cuff log and slap them in the cuff list

        // TODO fallback, if the list is empty rebuild from viable log entries
    }

    class CuffLog implements Serializable {
        // TODO make an abstract parent so that MuteLog will be the same scheme

        private Player player;
        private Date date;
        private Date end;
        private Player actor;
        private String reason;

        CuffLog() {}
        CuffLog(Player player, Date date, Date end, Player actor, String reason) {
            this.player = player;
            this.date = date;
            this.end = end;
            this.actor = actor;
            this.reason = reason;
        }

        private String toString(CuffLog c){
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy");
            StringBuilder s = new StringBuilder();
            s.append(c.player.getName() + " from: " + sdf.format(c.date));
            if (c.end != null)
                s.append(" until " + sdf.format(c.end));
            s.append(" by " + c.actor.getName());
            if (c.reason != null)
                s.append(": " + c.reason);

            return s.toString();
        }

        private void saveLog() {
            // TODO try catch to properties file




        }

        public ArrayList<String> getLog() {
            ArrayList<String> list = new ArrayList<>();
            // TODO deserialize cufflogs and put in list

            // TODO sort from newest to oldest
            Collections.sort(list);

            return list;
        }

        public ArrayList<String> getLog(int size) {
            ArrayList<String> list = new ArrayList<>();
            // TODO deserialize cufflogs and put in list

            // TODO sort from newest to oldest
            Collections.sort(list);

            // TODO crop to size

            return list;
        }
    }
}
