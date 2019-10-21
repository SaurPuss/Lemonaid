package me.saurpuss.lemonaid.utils.tp;

public enum TeleportType {
    // Player to Player
    TPA("tpa"), TPAHERE("tpahere"), PTP("ptp"), PTPHERE("ptphere"),
    // Player to Location
    BACK("back"), HOME("home"), SPAWN("spawn"), RANDOM("random"), WARP("warp");

    String name;

    TeleportType(String name) {
        this.name = name;
    }
}
