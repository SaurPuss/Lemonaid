package me.saurpuss.lemonaid.utils.database;

import me.saurpuss.lemonaid.utils.users.User;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public abstract class DBManager {

    // DB manager methods
    abstract boolean setup();
    abstract boolean save();

    // User related methods
    protected abstract User createUser(UUID uuid);
    public abstract boolean saveUser(User user);
    public abstract User getUser(UUID uuid);
    abstract boolean deleteUser(User user);
    abstract boolean deleteUser(UUID uuid);
    abstract boolean updateUser(User user);
    abstract boolean removeIgnore();
    abstract boolean removeHome();

    // Warp related methods
    public abstract boolean saveWarp();
    public abstract boolean updateWarp();
    public abstract boolean deleteWarp();
    public abstract HashMap<String, Location> getWarps();
}
