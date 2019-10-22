package me.saurpuss.lemonaid.utils.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Teleport {

    // Teleport()
    private Player client;
    private Player target;
    private Location location;
    private TeleportType tpType;

    public Teleport(Player client, Player target, Location location, TeleportType tpType) {
        this.client = client;
        this.tpType = tpType;
        this.target = target;
        this.location = location;
    }

    public Player getClient() {
        return client;
    }

    public Player getTarget() {
        return target;
    }

    public Location getLocation() {
        return location;
    }

    public TeleportType getTpType() {
        return tpType;
    }
}