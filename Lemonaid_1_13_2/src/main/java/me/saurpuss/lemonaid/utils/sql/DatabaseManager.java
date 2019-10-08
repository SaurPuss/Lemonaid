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

public class DatabaseManager {

    // Use removal records to delete composite primary keys
    // Track ignore removals for deletion <User, Target>
    private static HashMap<UUID, UUID> ignoreRemovals = new HashMap<>();
    // Track home removals for deletion <User, HomeName>
    private static HashMap<UUID, String> homeRemovals = new HashMap<>();

    private static Lemonaid plugin = Lemonaid.plugin;
    private static Connection conn;
    private static final String DB_URL =
            "jdbc:mysql://" + plugin.getConfig().getString("sql.host") +
                    ":" + plugin.getConfig().getString("sql.port") +
                    "/" + plugin.getConfig().getString("sql.database");
    private static final String DB_USER = plugin.getConfig().getString("sql.user");
    private static final String DB_PASS = plugin.getConfig().getString("sql.password");

    private static final String lemonTable = "lemonaid_users";
    private static final String lemonHomes = "lemonaid_homes";
    private static final String lemonIgnored = "lemonaid_ignored";
    private static final String partyTable = "lemonaid_party";

    private static void connect() {
        try {
//            Class.forName("com.mysql.jdbc.Driver"); // No longer required in java 8
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            plugin.getLogger().warning("SQL exception while trying to connect to the database!");
            e.printStackTrace();
        }
    }

    private static void createTables() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String lemons = "CREATE IF NOT EXISTS " + lemonTable + "(" +
                    " pk_uuid CHAR(36) CHARACTER SET ascii," +
                    " mute_end INT UNSIGNED DEFAULT 0," +
                    " nickname VARCHAR(20)," +
                    " last_location_world VARCHAR(20)," +
                    " last_location_x FLOAT," +
                    " last_location_y FLOAT," +
                    " last_location_z FLOAT," +
                    " last_message CHAR(36) CHARACTER SET ascii," +
                    " busy BOOLEAN," +
                    " cuffed BOOLEAN," +
                    " max_homes INT UNSIGNED DEFAULT 1," +
                    " PRIMARY KEY (pk_uuid)" +
                    ");";
            String homes = "CREATE IF NOT EXISTS " + lemonHomes + "(" +
                    " user_uuid CHAR(36) NOT NULL," +
                    " home_name VARCHAR(20) NOT NULL," +
                    " home_world VARCHAR(20) DEFAULT 'world'," +
                    " home_x FLOAT DEFAULT 0," +
                    " home_y FLOAT DEFAULT 0," +
                    " home_z FLOAT DEFAULT 0," +
                    " PRIMARY KEY (user_uuid, home_name)," +
                    " FOREIGN KEY (user_uuid) REFERENCES "+ lemonTable + " (pk_uuid) ON DELETE CASCADE" +
                    ");";
            // What if a player removes an ignored party from their list? On update cascade?
            String ignored = "CREATE IF NOT EXISTS " + lemonIgnored + "(" +
                    " user_uuid CHAR(36) NOT NULL," +
                    " ignored_player CHAR(36) CHARACTER SET ascii," +
                    " PRIMARY KEY (user_uuid, ignored_player)," +
                    " FOREIGN KEY (user_uuid) REFERENCES "+ lemonTable + " (pk_uuid) ON DELETE CASCADE" +
                    ");";



        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static Lemon getUser(UUID uuid) {
        Lemon user = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Run an sql query to retrieve a Lemon record
            String sql = "SELECT * FROM " + lemonTable + " WHERE uuid = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            // Try to put resulting info into a Lemon
            while (rs.next()) {
                // TODO refactor based on mysql scheme above
                HashMap<String, Location> homes = new HashMap<>();

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

                // TODO seperate table?
                String[] ign = rs.getString("ignored").split("\\|");
                HashSet<UUID> ignored = new HashSet<>();
                for (String s : ign) ignored.add(UUID.fromString(s));

                // Put the data into a Lemon
                user = new Lemon(
                        UUID.fromString(rs.getString("pk_uuid")),
                        rs.getLong("mute_end"),
                        rs.getString("nickname"),
                        Utils.locationFromString(rs.getString("lastLocation")), // "world|x|y|z"
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
