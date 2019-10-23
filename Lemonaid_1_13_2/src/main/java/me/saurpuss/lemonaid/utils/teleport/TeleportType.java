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
    TPA("tpa", true),

    /**
     * Teleporting target player to a requesting player,
     * invoked with the /tpahere [target] command.
     */
    TPAHERE("tpahere", true),

    /**
     * Teleporting requesting player to a target player,
     * on the condition that both players are in the same party,
     * invoked with the /ptp [target] command.
     */
    PTP("ptp", true),

    /**
     * Teleporting target player to a requesting player,
     * on the condition that both players are in the same party,
     * invoked with the /ptphere [target] command.
     */
    PTPHERE("ptphere", true),

    // Player to Location
    /**
     * Teleporting requesting player to their last saved location,
     * invoked with the /back command.
     */
    BACK("back", false),

    /**
     * Teleporting requesting player to a specified location saved
     * as a player home, invoked with the /home [home] command.
     */
    HOME("home", false),

    /**
     * Teleporting a requesting player to the spawn point defined
     * in the Lemonaid config.yml
     */
    SPAWN("spawn", false),

    /**
     * Teleporting a requesting player to a random (safe) location
     * within world boundaries defined in the Lemonaid config.yml
     */
    RANDOM("random", false),

    /**
     * Teleporting a requesting player to a predefined location saved
     * in the MySQL database, invoked with the /warp [name] command.
     */
    WARP("warp", false);

    /**
     * String value corresponding with the enum type. Used to retrieve
     * user defined values in the Lemonaid config.yml to determine
     * cost, timers, and other values that influence the Teleport event
     * for the invoking player.
     */
    private String name;

    /**
     * Boolean value used to determine if a Teleport event is a player
     * to player request, or a player to location request.
     */
    private boolean p2p;

    /**
     *
     * @param name Reference to type used comparing in files.
     * @param p2p Boolean to determine player to player or player to location types.
     */
    TeleportType(String name, boolean p2p) {
        this.name = name;
        this.p2p = p2p;
    }

    /**
     * Return the enum name value, used mainly to retrieve matching information
     * defined in the Lemonaid config.yml.
     * @return String value of the enum in lowercase.
     */
    String getName() {
        return name;
    }

    /**
     * Boolean value to determine player to player versus player to location types.
     * @return true if player to player request, false if player to location request.
     */
    boolean isP2p() {
        return p2p;
    }
}
