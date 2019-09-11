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
    private UUID lastMessage;
    private boolean busy;

    public Lemon() {}

    public Lemon(UUID uuid) {
        this.uuid = uuid;
        muteEnd = 0;
        nickname = "";
        lastLocation = null;
        lastMessage = null;
        busy = false;
    }

    public Lemon(UUID uuid, long muteEnd, String nickname, Location lastLocation, UUID lastMessage, boolean busy) {
        this.uuid = uuid;
        this.muteEnd = muteEnd;
        this.nickname = nickname;
        this.lastLocation = lastLocation;
        this.lastMessage = lastMessage;
        this.busy = busy;
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

    public boolean isMuted() { return muteEnd > System.currentTimeMillis(); }

    public static Lemon getUser(UUID uuid) {
        Lemon user = plugin.getUser(uuid);
        if (user == null) {
            // TODO bool, check for sql method or config method


            // TODO retrieve from DB
            user = Database.getUser(uuid);
        }
        return user;
    }

    private void saveUser() {
        // TODO bool, check for sql method or config method

        // TODO save to DB


    }

    public void updateUser() {
        this.saveUser();
        plugin.mapPlayer(this.uuid, this);
    }
}
