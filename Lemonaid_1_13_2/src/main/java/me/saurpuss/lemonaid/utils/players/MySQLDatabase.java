package me.saurpuss.lemonaid.utils.players;

import me.saurpuss.lemonaid.Lemonaid;

import java.util.UUID;

public class MySQLDatabase {

    private static Lemonaid plugin = Lemonaid.plugin;
    private static String prefix = plugin.getConfig().getString("mysql.table-prefix");
    private static String lemonTable = "users";
    private static String partyTable = "party";

    private static void createTable(String table) {
        plugin.getLogger().info("Setting up Database table " + prefix + "-" + table);

    }



    public static void pingTables() {
        if (!pingTable(lemonTable)) {
            createTable(lemonTable);
        }

        if (!pingTable(partyTable)) {
            createTable(partyTable);
        }

    }

    private static boolean pingTable(String table) {
        plugin.getLogger().info("Checking connection to " + table);

        return true;
    }

    public static Lemon getUser(UUID uuid) {
        // TODO connect and use prepared statement to retrieve a Lemon

        return new Lemon(uuid);
    }

    static boolean saveUser(Lemon user) {

        return true;
    }
}
