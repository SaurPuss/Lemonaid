package me.saurpuss.lemonaid.utils.users;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.sql.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Lemon {

    private static transient Lemonaid plugin = Lemonaid.getPlugin(Lemonaid.class); // TODO check if correct usage
    private UUID uuid, lastMessage;
    private long muteEnd;
    private String nickname;
    private Location lastLocation;
    private boolean busy, cuffed;
    private HashMap<String, Location> homes;
    private int maxHomes; // TODO make this based on perm groups instead
    private HashSet<UUID> ignored;

    public Lemon(UUID uuid) {
        this.uuid = uuid;
        maxHomes = determineMaxHomes(this.uuid);
        muteEnd = 0;
        nickname = null;
        lastLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation(); // Get from config?
        lastMessage = null;
        busy = false;
        cuffed = false;
        homes = new HashMap<>();
        ignored = new HashSet<>();

        DatabaseManager.createUser(this);
    }

    public Lemon(UUID uuid, long muteEnd, String nickname, Location lastLocation,
                 UUID lastMessage, boolean busy, boolean cuffed,
                 HashMap<String, Location> homes, HashSet<UUID> ignored) {
        this.uuid = uuid;
        this.muteEnd = muteEnd;
        this.nickname = nickname;
        this.lastLocation = lastLocation;
        this.lastMessage = lastMessage;
        this.busy = busy;
        this.cuffed = cuffed;
        this.homes = homes;
        this.maxHomes = determineMaxHomes(this.uuid);
        this.ignored = ignored;
    }

    // getters and setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public long getMuteEnd() { return muteEnd; }
    public void setMuteEnd(long muteEnd) { this.muteEnd = muteEnd; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public Location getLastLocation() { return lastLocation; }
    public void setLastLocation(Location lastLocation) { this.lastLocation = lastLocation; }
    public UUID getLastMessage() { return lastMessage; }
    public void setLastMessage(UUID lastMessage) { this.lastMessage = lastMessage; }
    public boolean isBusy() { return busy; }
    public void setBusy(boolean busy) { this.busy = busy; }
    public boolean isCuffed() { return cuffed; }
    public void setCuffed(boolean cuffed) { this.cuffed = cuffed; }
    public HashMap<String, Location> getHomes() { return homes; }
    public void setHomes(HashMap<String, Location> homes) { this.homes = homes;}
    public HashSet<UUID> getIgnored() { return ignored; }
    public void setIgnored(HashSet<UUID> ignored) { this.ignored = ignored; }
    public int getMaxHomes() { return maxHomes; }
    public void setMaxHomes(int maxHomes) { this.maxHomes = maxHomes; }

    // bespoke getters and setters
    public boolean isMuted() { return muteEnd > System.currentTimeMillis(); }
    public void removeHome(String name) {
        homes.remove(name.toLowerCase());
        DatabaseManager.removeRecord(uuid, name); // Create a record for DB removal
    }
    public int homeCount() { return homes.size(); }
    public Location getHome(String name) { return homes.get(name.toLowerCase()); }
    public Location getFirstHome() {
        if (homes.size() == 1)
            return homes.values().stream().findFirst().get();
        else
            return null;
    }
    public short addHome(String name, Location location) {
        if (homes.containsKey(name.toLowerCase())) {
            return -1; // Home name already exists
        } else if (homes.size() >= maxHomes) {
            return 0; // No additional homes allowed
        } else {
            homes.put(name.toLowerCase(), location);
            return 1; // Successfully added location to homes
        }
    }
    public boolean updateHome(String name, Location location) {
        boolean bool = true;
        if (!homes.containsKey(name.toLowerCase())) bool = false;

        homes.put(name.toLowerCase(), location);
        return bool;
    }
    public String listHomes() {
        StringBuilder s = new StringBuilder();
        homes.forEach((name, location) -> s.append(name + " "));
        return s.toString();
    }
    public boolean isIgnored(UUID uuid) { return ignored.contains(uuid); }
    public boolean toggleIgnore(UUID uuid) {
        if (ignored.contains(uuid)) {
            ignored.remove(uuid);
            DatabaseManager.removeRecord(this.uuid, uuid); // Add record for removal from DB
            return false; // Player is no longer ignored
        } else {
            ignored.add(uuid);
            DatabaseManager.undoRemoveRecord(this.uuid, uuid); // Make sure there isn't a deletion record in the map
            return true; // Player is now ignored
        }
    }

    private static int determineMaxHomes(UUID uuid) {
        int maxHome = plugin.getConfig().getInt("player-homes.default");

        Player player = Bukkit.getPlayer(uuid);
        Set<String> list = plugin.getConfig().getConfigurationSection("player-homes").getKeys(false);

        if (player == null || list.isEmpty()) return maxHome;

        if (player.hasPermission("lemonaid.homes.unlimited"))
            maxHome = 0; // 0 equals infinite homes TODO implement
        else {
            for (String node : list) {
                if (player.hasPermission("lemonaid.homes." + node)) {
                    if (maxHome < plugin.getConfig().getInt(node)) {
                        maxHome = plugin.getConfig().getInt(node);
                    }
                }
            }
            if (maxHome == 0) maxHome = 1; // fallback so players don't end up with infinite homes
        }

        return maxHome;
    }


    // save/update/delete object references
    public Lemon getUser() {
        Lemon user = plugin.getUser(uuid);
        if (user == null)
            user = DatabaseManager.getUser(uuid);

        return user;
    }

    public void saveUser() {
        DatabaseManager.saveUser(this);
    }

    public void updateUser() {
        saveUser();
        plugin.mapPlayer(uuid, this);
    }
}
