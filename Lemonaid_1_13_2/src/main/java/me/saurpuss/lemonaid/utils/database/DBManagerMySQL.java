package me.saurpuss.lemonaid.utils.database;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.util.UUID;

public class DBManagerMySQL extends DBManager {

    private Lemonaid lemonaid;
    private FileConfiguration config;

    // Get connection variables from config
    private final String URL;
    private final String USER;
    private final String PASS;
    private final String DATABASE;
    //    database:
    //      mysql: true
    //        host: ''
    //        port: ''
    //        database: ''
    //        username: ''
    //        password: ''

    private Connection connection;

    public DBManagerMySQL(Lemonaid plugin) {
        lemonaid = plugin;
        config = lemonaid.getConfig();

        URL = "jdbc:mysql://" + config.getString("database.mysql.host") +
                ":" + config.getString("database.mysql.port") +
                "/" + config.getString("database.mysql.database");
        USER = config.getString("database.mysql.username");
        PASS = config.getString("database.mysql.password");
        DATABASE = config.getString("database.mysql.password");

        setup();
    }

    public void connect() {
        if (connection == null) {




        }



    }

    @Override
    boolean setup() {
        // test connection


        // create tables if necessary

        return false;
    }

    @Override
    boolean save() {
        return false;
    }

    @Override
    protected User createUser(UUID uuid) {



        return new User(uuid);
    }

    @Override
    public boolean saveUser(User user) {



        return false;
    }

    @Override
    public User getUser(UUID uuid) {
        // If new user #createUser(new User(uuid))



        // No pre-existing user
        return createUser(uuid);
    }

    @Override
    boolean deleteUser(User user) {


        return false;
    }

    @Override
    boolean deleteUser(UUID uuid) {

        return false;
    }

    @Override
    boolean updateUser(User user) {
        return false;
    }

    @Override
    boolean removeIgnore() {


        return false;
    }

    @Override
    boolean removeHome() {



        return false;
    }

    @Override
    boolean saveWarp() {



        return false;
    }

    @Override
    boolean updateWarp() {



        return false;
    }

    @Override
    boolean deleteWarp() {




        return false;
    }
}
