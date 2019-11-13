package me.saurpuss.lemonaid.utils.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Teleport object containing information required to run a TeleportTask initiated by the
 * Teleport Manager.
 */
public class Teleport {

    /**
     * Player initiating the Teleport
     */
    private Player client;

    /**
     * Player receiving the Teleport request. Only required for player to player teleportation.
     */
    private Player target;

    /**
     * Location of the Teleport destination. Only required for player to location teleportation.
     */
    private Location location;

    /**
     * Enum constant designating what type of Teleport request should be initiated.
     */
    private TeleportType tpType;

    /**
     * Constructor for a Teleport the TeleportManager will use.
     *
     * @param client   Initiating player
     * @param target   Target player receiving the request
     * @param location Target destination for the request
     * @param tpType   Constant determining the type
     */
    public Teleport(Player client, Player target, Location location, TeleportType tpType) {
        this.client = client;
        this.target = target;
        this.location = location;
        this.tpType = tpType;
    }

    /**
     * Get the initiating player for the Teleport.
     *
     * @return Teleport client Player object.
     */
    public Player getClient() {
        return client;
    }

    /**
     * Get the intended target for the Teleport.
     *
     * @return Teleport target Player object.
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Get the intended destination for the Teleport.
     *
     * @return Teleport target Location object.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the constant TeleportType for the Teleport.
     *
     * @return Enum constant.
     */
    TeleportType getTpType() {
        return tpType;
    }
}