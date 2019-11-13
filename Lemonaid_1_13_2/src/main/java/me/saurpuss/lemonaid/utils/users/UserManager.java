package me.saurpuss.lemonaid.utils.users;

import me.saurpuss.lemonaid.Lemonaid;
import me.saurpuss.lemonaid.utils.database.DatabaseManager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    /**
     * Dependecy Injection
     */
    private Lemonaid lemonaid;

    /**
     * Map containing the User wrapper objects for online players.
     */
    private static HashMap<UUID, User> userManager;

    /**
     * Manage and maintain the User wrappers needed to allow players to have access to plugin
     * options. Instantiating the Manager will create a new instance of the userManager HashMap
     * which is then populated from the MySQL database with objects matching the players that
     * are currently online. Otherwise Users are added and removed in the JoinLeave Event.
     * @param plugin instance dependency injection
     */
    public UserManager(Lemonaid plugin) {
        lemonaid = plugin;

        userManager = new HashMap<>();

        // look for online players to populate map
        // TODO open connection
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!userManager.containsKey(player.getUniqueId())) {
                User user = getUser(player.getUniqueId());
                userManager.put(player.getUniqueId(), user);
            }
        });
        // TODO close connection
    }

    /**
     * Retrieve a User wrapper from the userManager, or try to retrieve one from the Database if
     * there is no pre-existing User in the map.
     * @param uuid key to reference the User object
     * @return User wrapper object
     */
    public User getUser(UUID uuid) {
        if (userManager.containsKey(uuid))
            return userManager.get(uuid);

        return DatabaseManager.getUser(uuid);
    }




    // TODO rework everything
    // trigger these during world save event && onDisable
    public void saveUserManager() {
        userManager.forEach((uuid, user) -> saveUser(user));
        DatabaseManager.deleteRemovalRecords();
    }


    public void updateUser(User user) {
        userManager.put(user.getUuid(), user);
        saveUser(user);
    }

    private void saveUser(User user) {
        DatabaseManager.saveUser(user);
    }

    public void removeUser(User user) {
        userManager.remove(user.getUuid());
    }

    public void removeUser(UUID uuid) {
        userManager.remove(uuid);
    }


}
