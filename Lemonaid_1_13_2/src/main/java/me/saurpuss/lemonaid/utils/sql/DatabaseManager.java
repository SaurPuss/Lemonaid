package me.saurpuss.lemonaid.utils.sql;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.Lemon;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;

public class DatabaseManager {

    // TODO add UserNotFound error on retrieval

    // Use removal records to delete composite primary keys
    // Track ignore pk removals for deletion <Player, Target>
    private static Multimap<UUID, UUID> ignoreRemovals = HashMultimap.create();
    // Track home pk removals for deletion <Player, HomeName>
    private static Multimap<UUID, String> homeRemovals = HashMultimap.create();

    private static Lemonaid plugin = Lemonaid.plugin;
    private static Connection conn; // TODO make this work
    private static final String DB_URL =
            "jdbc:mysql://" + plugin.getConfig().getString("sql.host") +
                    ":" + plugin.getConfig().getString("sql.port") +
                    "/" + plugin.getConfig().getString("sql.database");
    private static final String DB_USER = plugin.getConfig().getString("sql.user");
    private static final String DB_PASS = plugin.getConfig().getString("sql.password");

    private static final String lemonTable = "lemonaid_users";
    private static final String lemonHomes = "lemonaid_homes";
    private static final String lemonIgnored = "lemonaid_ignored";
    private static final String publicWarps = "lemonaid_warps";
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
            String lemons = "CREATE TABLE IF NOT EXISTS " + lemonTable + "(" +
                    " pk_uuid CHAR(36) CHARACTER SET ascii NOT NULL PRIMARY KEY," +
                    " mute_end INT UNSIGNED DEFAULT 0," +
                    " nickname VARCHAR(20) DEFAULT NULL," +
                    " last_location_world VARCHAR(20) DEFAULT NULL," +
                    " last_location_x FLOAT DEFAULT NULL," +
                    " last_location_y FLOAT DEFAULT NULL," +
                    " last_location_z FLOAT DEFAULT NULL," +
                    " last_location_yaw FLOAT DEFAULT NULL," +
                    " last_location_pitch FLOAT DEFAULT NULL, " +
                    " last_message CHAR(36) CHARACTER SET ascii DEFAULT NULL," +
                    " busy BOOLEAN DEFAULT false," +
                    " cuffed BOOLEAN DEFAULT false," +
                    ");";
            String homes = "CREATE TABLE IF NOT EXISTS " + lemonHomes + "(" +
                    " fk_uuid CHAR(36) CHARACTER SET ascii NOT NULL," +
                    " home_name VARCHAR(20) NOT NULL," +
                    " home_world VARCHAR(20) DEFAULT 'world'," +
                    " home_x FLOAT," +
                    " home_y FLOAT," +
                    " home_z FLOAT," +
                    " home_yaw FLOAT," +
                    " home_pitch FLOAT," +
                    " PRIMARY KEY (fk_uuid, home_name)," +
                    " FOREIGN KEY (fk_uuid) REFERENCES " + lemonTable + " (pk_uuid) ON DELETE CASCADE," +
                    ");";
            // What if a player removes an ignored party from their list? On update cascade?
            String ignored = "CREATE TABLE IF NOT EXISTS " + lemonIgnored + "(" +
                    " fk_uuid CHAR(36) CHARACTER SET ascii NOT NULL," +
                    " ignored_player CHAR(36) CHARACTER SET ascii NOT NULL," +
                    " PRIMARY KEY (fk_uuid, ignored_player)," +
                    " FOREIGN KEY (fk_uuid) REFERENCES " + lemonTable + " (pk_uuid) ON DELETE CASCADE" +
                    ");";
            String warps = "CREATE TABLE IF NOT EXISITS " + publicWarps + "(" +
                    " warp_name VARCHAR(50) PRIMARY KEY," +
                    " warp_world VARCHAR(20) DEFAULT 'world'," +
                    " warp_x FLOAT," +
                    " warp_y FLOAT," +
                    " warp_z FLOAT," +
                    " warp_yaw FLOAT," +
                    " warp_pitch FLOAT" +
                    ");";

            Statement statement = connection.createStatement();
            // TODO single query with batch update
            statement.executeQuery(lemons);
            statement.executeQuery(homes);
            statement.executeQuery(ignored);
            statement.executeQuery(warps); // TODO insert, update, delete methods

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Lemon getUser(UUID uuid) {
        Lemon user;
        UUID id = null, lastMessage = null;
        long muteEnd = 0;
        String nickname = null;
        Location lastLocation = null;
        boolean busy = false, cuffed = false;
        HashMap<String, Location> homes = new HashMap<>();
        HashSet<UUID> ignored = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Run an sql query to retrieve a Lemon record
            String getUser = "SELECT * FROM " + lemonTable + " WHERE pk_uuid = " + uuid.toString() +  ";";
            String getIgnored = "SELECT * FROM " + lemonIgnored + " WHERE fk_uuid = " + uuid.toString() +  ";";
            String getHomes = "SELECT * FROM " + lemonHomes + " WHERE fk_uuid = " + uuid.toString() +  ";";
            Statement userStatement = connection.createStatement();
            boolean results = userStatement.execute(getUser);

            // Try to put resulting info into a Lemon
            if (results) {
                ResultSet rs = userStatement.getResultSet();
                while (rs.next()) {
                    id = UUID.fromString(rs.getString("pk_uuid"));
                    muteEnd = rs.getLong("mute_end");

                    String nick = rs.getString("nickname");
                    if (!rs.wasNull()) nickname = nick;

                    lastLocation = new Location(
                            Bukkit.getServer().getWorld(rs.getString("last_location_world")),
                            rs.getDouble("last_location_x"),
                            rs.getDouble("last_location_y"),
                            rs.getDouble("last_location_z"),
                            rs.getFloat("last_location_yaw"),
                            rs.getFloat("last_location_pitch"));

                    String last = rs.getString("last_message");
                    if (!rs.wasNull()) lastMessage = UUID.fromString(last);

                    busy = rs.getBoolean("busy");
                    cuffed  = rs.getBoolean("cuffed");
                    rs.close();
                }

                // TODO I think I can simplify this
                Statement ignoredStatement = connection.createStatement();
                results = ignoredStatement.execute(getIgnored);
                if (results) {
                    rs = ignoredStatement.getResultSet();
                    while (rs.next()) {
                        ignored.add(UUID.fromString(rs.getString("ignored_player")));
                    }
                    rs.close();
                }

                Statement homesStatement = connection.createStatement();
                results = homesStatement.execute(getHomes);
                if (results) {
                    rs = ignoredStatement.getResultSet();
                    while (rs.next()) {
                        homes.put(rs.getString("home_name"), new Location(
                                Bukkit.getServer().getWorld(rs.getString("home_world")),
                                rs.getDouble("home_x"),
                                rs.getDouble("home_y"),
                                rs.getDouble("home_z")
                        ));
                    }
                    rs.close();
                }
            }

            if (id != null)
                user = new Lemon(id, muteEnd, nickname, lastLocation, lastMessage, busy, cuffed, homes, ignored);
            else
                user = new Lemon(uuid);

            return user;
        } catch (SQLException e) {
            plugin.getLogger().info("SQL exception when trying to retrieve Lemon from database, creating new entry.");
            e.printStackTrace();
            return new Lemon(uuid);
        }
    }

    public static void createUser(Lemon user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO " + lemonTable +
                    " (pk_uuid, last_location_world, last_location_x, last_location_y," +
                    " last_location_z, last_location_yaw, last_location_pitch, max_homes)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            Location location = user.getLastLocation();
            statement.setString(1, user.getUuid().toString());
            statement.setString(2, Objects.requireNonNull(location.getWorld()).toString());
            statement.setDouble(3, location.getBlockX());
            statement.setDouble(4, location.getBlockY());
            statement.setDouble(5, location.getBlockZ());
            statement.setFloat(6, location.getYaw());
            statement.setFloat(7, location.getPitch());
            statement.setInt(8, user.getMaxHomes());

            // One row should be affected
            if (statement.executeUpdate() != 1) {
                plugin.getLogger().warning("Error while inserting new user record into MySQL database!");
                plugin.getLogger().info("Affected user: " + Bukkit.getPlayer(user.getUuid()).getName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveUser(Lemon user) {

        // TODO sql update user & homes & ignored
        return true;
    }

    public static void deleteRemovalRecords() {
        if (ignoreRemovals.size() == 0 && homeRemovals.size() == 0) return;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            if (ignoreRemovals.size() > 0) {
                String sql = "DELETE FROM " + lemonIgnored + " WHERE " +
                        "(user_uuid, ignored_player) = (?,?);";
                PreparedStatement statement = connection.prepareStatement(sql);
                connection.setAutoCommit(false);

                // TODO test multimap
                for (Map.Entry<UUID, UUID> key : ignoreRemovals.entries()) {
                    try {
                        statement.setString(1, key.toString());
                        statement.setString(2, key.getValue().toString());
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                statement.executeBatch();
                connection.commit();
            }

            if (homeRemovals.size() > 0) {
                String sql = "DELETE FROM " + lemonHomes + " WHERE " +
                        "(user_uuid, home_name) = (?,?);";
                PreparedStatement statement = connection.prepareStatement(sql);
                connection.setAutoCommit(false);
                for (Map.Entry<UUID, String> key : homeRemovals.entries()) {
                    try {
                        statement.setString(1, key.toString());
                        statement.setString(2, key.getValue());
                        statement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                statement.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeRecord(UUID player, UUID target) {
        ignoreRemovals.put(player, target);
    }

    public static void undoRemoveRecord(UUID player, UUID target) {
        ignoreRemovals.remove(player, target);
    }

    public static void removeRecord(UUID player, String homeName) {
        homeRemovals.put(player, homeName);
    }

    public static void undoRemoveRecord(UUID player, String homeName) {
        homeRemovals.remove(player, homeName);
    }

    public static HashMap<String, Location> getWarps() {



        return new HashMap<>();
    }

    public static void addWarp(String warpName, Location warpLocation) {

    }

    public static void updateWarp(String warpName, Location warpLocation) {

    }

    public static void deleteWarp(String warpName) {


    }
}
