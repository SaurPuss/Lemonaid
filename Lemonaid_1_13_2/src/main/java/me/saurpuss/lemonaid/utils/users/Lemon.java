package me.saurpuss.lemonaid.utils.users;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.sql.DatabaseManager;
import org.bukkit.Location;

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
        muteEnd = 0;
        nickname = null;
        lastLocation = plugin.getServer().getWorlds().get(0).getSpawnLocation(); // Get from config?
        lastMessage = null;
        busy = false;
        cuffed = false;
        homes = new HashMap<>();
        maxHomes = 3; // TODO make this viable && if player isOP set to 100
        ignored = new HashSet<>();

        DatabaseManager.createUser(this);
    }

    public Lemon(UUID uuid, long muteEnd, String nickname, Location lastLocation,
                 UUID lastMessage, boolean busy, boolean cuffed,
                 HashMap<String, Location> homes, int maxHomes, HashSet<UUID> ignored) {
        this.uuid = uuid;
        this.muteEnd = muteEnd;
        this.nickname = nickname;
        this.lastLocation = lastLocation;
        this.lastMessage = lastMessage;
        this.busy = busy;
        this.cuffed = cuffed;
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
    public boolean isCuffed() { return cuffed; }
    public void setCuffed(boolean cuffed) { this.cuffed = cuffed; }
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
    public Location getHome(String name) { return homes.get(name.toLowerCase()); }
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
    public boolean toggleIgnore(UUID uuid) {
        if (ignored.contains(uuid)) {
            ignored.remove(uuid);
            DatabaseManager.removeRecord(this.getUuid(), uuid); // Add record for removal from DB
            return false; // Player is no longer ignored
        } else {
            ignored.add(uuid);
            DatabaseManager.undoRemoveRecord(this.getUuid(), uuid); // Make sure there isn't a deletion record in the map
            return true; // Player is now ignored
        }
    }

    public Lemon getUser() {
        Lemon user = plugin.getUser(uuid);
        if (user == null)
            user = DatabaseManager.getUser(uuid);

        return user;
    }

    private void saveUser() {
        DatabaseManager.saveUser(this);
    }

    public void updateUser() {
        saveUser();
        plugin.mapPlayer(uuid, this);
    }
}
