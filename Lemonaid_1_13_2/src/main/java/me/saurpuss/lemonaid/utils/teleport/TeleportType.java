package me.saurpuss.lemonaid.utils.teleport;

/**
 * An enum to specify the various intents of a player initiated
 * teleportation event.
 */
public enum TeleportType {
    // Player to Player
    /**
     * Teleporting requesting player to a target player,
     * invoked with the /tpa [target] command.
     */
    TPA( true),

    /**
     * Teleporting target player to a requesting player,
     * invoked with the /tpahere [target] command.
     */
    TPAHERE(true),

    /**
     * Teleporting requesting player to a target player,
     * on the condition that both players are in the same party,
     * invoked with the /ptp [target] command.
     */
    PTP(true),

    /**
     * Teleporting target player to a requesting player,
     * on the condition that both players are in the same party,
     * invoked with the /ptphere [target] command.
     */
    PTPHERE(true),

    // Player to Location
    /**
     * Teleporting requesting player to their last saved location,
     * invoked with the /back command.
     */
    BACK(false),

    /**
     * Teleporting requesting player to a specified location saved
     * as a player home, invoked with the /home [home] command.
     */
    HOME(false),

    /**
     * Teleporting a requesting player to the spawn point defined
     * in the Lemonaid config.yml
     */
    SPAWN(false),

    /**
     * Teleporting a requesting player to a random (safe) location
     * within world boundaries defined in the Lemonaid config.yml
     */
    RANDOM(false),

    /**
     * Teleporting a requesting player to a predefined location saved
     * in the MySQL database, invoked with the /warp [name] command.
     */
    WARP(false);

    /**
     * Boolean value used to determine if a Teleport event is a player
     * to player request, or a player to location request.
     */
    private boolean p2p;

    /**
     *
     * @param p2p
     */
    TeleportType(boolean p2p) {
        this.p2p = p2p;
    }

    /**
     *
     * @return
     */
    String getName() {
        return this.toString().toLowerCase();
    }

    /**
     * Boolean value to determine player to player versus player to location types.
     * @return true if player to player request, false if player to location request.
     */
    boolean isP2p() {
        return p2p;
    }
}
