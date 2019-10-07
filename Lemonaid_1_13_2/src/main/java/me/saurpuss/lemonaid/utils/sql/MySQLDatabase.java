package me.saurpuss.lemonaid.utils.sql;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.Utils;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class MySQLDatabase {

    private static Lemonaid plugin = Lemonaid.plugin;
    private static Connection connection;
    private static final String dbUrl =
            "jdbc:msql://" + plugin.getConfig().getString("sql.host") +
            ":" + plugin.getConfig().getString("sql.port") +
            "/" + plugin.getConfig().getString("sql.database");
    private static final String username = plugin.getConfig().getString("sql.user");
    private static final String pass = plugin.getConfig().getString("sql.password");

    private static final String lemonTable = "lemonaid_users";
    private static final String partyTable = "lemonaid_party";

    private static void connect() {
        try {
            if (connection != null) {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, username, pass);
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().warning("SQL exception while trying to connect to the database!");
            e.printStackTrace();
        }
    }

    private static void createTable(String table) {



    }




    public static Lemon getUser(UUID uuid) {
        Lemon user = null;

        try (Connection conn = DriverManager.getConnection(dbUrl, username, pass)){
            String sql = "SELECT TOP 1 FROM " + lemonTable
                    + "WHERE uuid = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            rs.next();

            // Try to put resulting info into a Lemon
            while (rs.next()) {
                // TODO check regex for all arrays below
                HashMap<String, Location> homes = new HashMap<>();
                // TODO seperate table
                // {HomeName1|world|x|y|z,HomeName2|world|x|y|z,HomeName3|world|x|y|z}
                String[] rows = rs.getString("homes").split(",");
                for (String row : rows) {
                    String[] home = row.split("\\|");
                    Location homeLoc = new Location(Bukkit.getWorld(home[1]),
                            Double.parseDouble(home[2]),
                            Double.parseDouble(home[3]),
                            Double.parseDouble(home[4]));
                    homes.put(home[0], homeLoc);
                }

                // {XXXXX-XXXXXXX-XXXXXX-XXXX|XXXXX-XXXX-XXXXX|XXXX-XXXX-XXXX}
                // TODO seperate table
                String[] ign = rs.getString("ignored").split("\\|");
                HashSet<UUID> ignored = new HashSet<>();
                for (String s : ign) {
                    ignored.add(UUID.fromString(s));
                }

                // "world|x|y|z"



                user = new Lemon(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getLong("mute_end"),
                        rs.getString("nickname"),
                        Utils.locationFromString(rs.getString("lastLocation")),
                        UUID.fromString(rs.getString("last_message")),
                        rs.getBoolean("busy"),
                        rs.getBoolean("cuffed"),
                        homes,
                        rs.getInt("max_homes"),
                        ignored);
            }
            // If no user popped out of the db create a fresh Lemon
            if (user == null)
                user = new Lemon(uuid);

            return user;
        } catch (SQLException e) {
            plugin.getLogger().info("Unable to retrieve Lemon from database, creating new entry.");
            return new Lemon(uuid);
        }
    }

    public static Lemon createUser(Lemon user) {



        return user;
    }

    public static boolean saveUser(Lemon user) {

        return true;
    }
}
