package me.saurpuss.lemonaid.utils;

public enum TeleportType {
    TPA("tpa"), TPAHERE("tpahere"), PTP("ptp"), PTPHERE("ptphere"), BACK("back");

    String name;

    TeleportType(String name) {
        this.name = name;
    }
}
