package me.saurpuss.lemonaid.utils.users;

import me.saurpuss.lemonaid.Lemonaid;
import org.bukkit.Location;

import java.util.*;

public class Lemon {
    private static Lemonaid plugin = Lemonaid.getInstance();
    private final static boolean DB_SQL = plugin.getConfig().getBoolean("mysql");

    private UUID uuid, lastMessage;
    private long muteEnd;
    private String nickname;
    private Location lastLocation;
    private boolean busy;
    private HashMap<String, Location> homes;
    private int maxHomes; // TODO make this based on perm groups instead
    private HashSet<UUID> ignored;

    Lemon() {} // TODO replace

    public Lemon(UUID uuid) {
        this.uuid = uuid;
        muteEnd = 0;
        nickname = "";
        lastLocation = null;
        lastMessage = null;
        busy = false;
        homes = new HashMap<>();
        maxHomes = 3;
        ignored = new HashSet<>();
    }

    Lemon(UUID uuid, long muteEnd, String nickname, Location lastLocation, UUID lastMessage,
          boolean busy, HashMap<String, Location> homes, int maxHomes, HashSet<UUID> ignored) {
        this.uuid = uuid;
        this.muteEnd = muteEnd;
        this.nickname = nickname;
        this.lastLocation = lastLocation;
        this.lastMessage = lastMessage;
        this.busy = busy;
        this.homes = homes;
        this.maxHomes = maxHomes;
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
    public HashMap<String, Location> getHomes() { return homes; }
    public void setHomes(HashMap<String, Location> homes) { this.homes = homes;}
    public int getMaxHomes() { return maxHomes; }
    public void setMaxHomes(int maxHomes) { this.maxHomes = maxHomes; }
    public HashSet<UUID> getIgnored() { return ignored; }
    public void setIgnored(HashSet<UUID> ignored) { this.ignored = ignored; }

    // bespoke getters and setters
    public boolean isMuted() { return muteEnd > System.currentTimeMillis(); }
    public void removeHome(String name) { homes.remove(name.toLowerCase()); }
    public int homeCount() { return homes.size(); }
    public Location getHomeName(String name) { return homes.get(name.toLowerCase()); }
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
    public String listHomes() {
        StringBuilder s = new StringBuilder();
        homes.forEach((name, location) -> s.append(name + " "));
        return s.toString();
    }
    public boolean isIgnored(UUID uuid) { return ignored.contains(uuid); }
    public boolean setIgnore(UUID uuid) {
        if (ignored.contains(uuid)) {
            ignored.remove(uuid);
            return false; // Player is no longer ignored
        } else {
            ignored.add(uuid);
            return true; // Player is now ignored
        }
    }



    public Lemon getUser() {
        // TODO make this a thing
        if (DB_SQL) {
//            return MySQLDatabase.getUser(uuid);
        } else {
//            return LemonConfig.getUser(uuid);
        }
        return new Lemon();
    }

    private void saveUser() {
        // TOD make this a thing
        if (DB_SQL) {
//            MySQLDatabase.saveUser(this);
        } else {
//            LemonConfig.saveUser(this);
        }
    }

    public void updateUser() {
        this.saveUser();
        plugin.mapPlayer(uuid, this);
    }
}
