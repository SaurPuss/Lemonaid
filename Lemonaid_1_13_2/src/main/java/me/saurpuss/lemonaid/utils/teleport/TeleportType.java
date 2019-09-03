package me.saurpuss.lemonaid.utils.teleport;

public enum TeleportType {
    TPA("tpa"), TPAHERE("tpahere"), PTP("ptp"), PTPHERE("ptphere"), BACK("back");

    private String name;

    TeleportType(String name) {
        this.name = name;
    }
}
