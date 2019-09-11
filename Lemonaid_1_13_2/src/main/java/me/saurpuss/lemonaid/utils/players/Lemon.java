package me.saurpuss.lemonaid.utils.players;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.util.Database;
import org.bukkit.Location;

import java.util.UUID;

public class Lemon {
    private static Lemonaid plugin = Lemonaid.plugin;
    private UUID uuid;
    private long muteEnd;
    private String nickname;
    private Location lastLocation;

    public Lemon() {}

    public Lemon(UUID uuid) {
        this.uuid = uuid;
        muteEnd = 0;
        nickname = "";
        lastLocation = null;
    }

    public Lemon(UUID uuid, long muteEnd, String nickname, Location lastLocation) {
        this.uuid = uuid;
        this.muteEnd = muteEnd;
        this.nickname = nickname;
        this.lastLocation = lastLocation;
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

    public boolean isMuted() { return muteEnd > System.currentTimeMillis(); }

    public static Lemon getUser(UUID uuid) {
        Lemon user = plugin.getUser(uuid);
        if (user == null) {
            // TODO retrieve from DB
            user = Database.getUser(uuid);
        }
        return user;
    }

    private void saveUser() {
        // TODO save to DB


    }

    public void updateUser() {
        this.saveUser();
        plugin.mapPlayer(this.uuid, this);
    }
}
