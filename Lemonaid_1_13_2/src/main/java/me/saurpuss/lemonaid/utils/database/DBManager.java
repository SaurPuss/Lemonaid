package me.saurpuss.lemonaid.utils.database;

public abstract class DBManager {

    // DB manager methods
    abstract boolean setup();
    abstract boolean save();

    // User related methods
    abstract boolean saveUser();
    abstract boolean getUser();
    abstract boolean deleteUser();
    abstract boolean updateUser();
    abstract boolean removeIgnore();
    abstract boolean removeHome();

    // Warp related methods
    abstract boolean saveWarp();
    abstract boolean updateWarp();
    abstract boolean deleteWarp();

}
