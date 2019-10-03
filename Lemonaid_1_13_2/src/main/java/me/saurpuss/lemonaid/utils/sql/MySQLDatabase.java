package me.saurpuss.lemonaid.utils.sql;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLDatabase {

    private static Lemonaid plugin = Lemonaid.plugin;
    private static Connection connection;
    private static final String dbUrl =
            "jdbc:msql://" + plugin.getConfig().getString("sql.host") +
            ":" + plugin.getConfig().getString("sql.port") +
            "/" + plugin.getConfig().getString("sql.database");
    private static final String user = plugin.getConfig().getString("sql.user");
    private static final String pass = plugin.getConfig().getString("sql.password");

    private static String lemonTable = "lemonaid_users";
    private static String partyTable = "lemonaid_party";

    private static Connection getConnection() {
        try {
            if (connection != null || !connection.isClosed()) {
                return connection;
            }



            DriverManager.getConnection(dbUrl, user, pass);

        } catch (SQLException e) {
            plugin.getLogger().warning("SQL exception while trying to connect to the database!");
            e.printStackTrace();
        }

        return null;
    }

    private static void createTable(String table) {

    }




    public static Lemon getUser(UUID uuid) {
        // TODO connect and use prepared statement to retrieve a Lemon

        return new Lemon(uuid);
    }

    public static boolean saveUser(Lemon user) {

        return true;
    }
}
