package me.saurpuss.lemonaid.utils.users;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.database.DatabaseManager;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private Lemonaid plugin;
    private static HashMap<UUID, User> userManager;

    public UserManager(Lemonaid plugin) {
        this.plugin = plugin;

        userManager = new HashMap<>(); // look for online players to populate with
    }




    // TODO rework everything




    // save/update/delete object references
    public User getUser(UUID uuid) {

        User user = plugin.getUserManager().getUser(uuid);
        if (user == null)
            user = DatabaseManager.getUser(uuid);

        return user;
    }

    public void saveUser(User user) {
        DatabaseManager.saveUser(user);
    }

    public void updateUser(User user) {
        saveUser(user);
        plugin.getUserManager().mapPlayer(user.getUuid(), user);
    }


    public void addUser(UUID uuid) {
        User user = new User(uuid);
        userManager.put(uuid, user);
    }

    public void removeUser(User user) {
        userManager.remove(user.getUuid());
    }

    public void removeUser(UUID uuid) {
        userManager.remove(uuid);
    }





    // Keep track of Lemons
    public void mapPlayer(UUID uuid, User user) { userManager.put(uuid, user); }
    public void unmapPlayer(UUID uuid) { userManager.remove(uuid); }

    // trigger these during world save event && onDisable
    public void saveUserManager() {
        userManager.forEach((uuid, user) -> saveUser(user));
        DatabaseManager.deleteRemovalRecords();
    }
}
