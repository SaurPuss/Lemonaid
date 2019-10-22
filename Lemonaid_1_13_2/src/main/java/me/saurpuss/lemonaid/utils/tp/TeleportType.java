package me.saurpuss.lemonaid.utils.tp;

public enum TeleportType {
    // Player to Player
    TPA("tpa", true), TPAHERE("tpahere", true),
    PTP("ptp", true), PTPHERE("ptphere", true),
    // Player to Location
    BACK("back", false), HOME("home", false),
    SPAWN("spawn", false), RANDOM("random", false),
    WARP("warp", false);

    private String name;
    private boolean p2p;

    TeleportType(String name, boolean p2p) {
        this.name = name;
        this.p2p = p2p;
    }

    String getName() {
        return name;
    }

    boolean isP2p() {
        return p2p;
    }
}
