package me.saurpuss.lemonaid.utils.utility;

public interface PermissionErrors {
    // No permission errors
    default String noPermission() { return "§cYou don't have permission to do this!"; }
    default String playerOnly() {
        return "§cOnly players can use this command!";
    }
}
