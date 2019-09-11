package me.saurpuss.lemonaid.utils.util;

import me.saurpuss.lemonaid.utils.players.Lemon;

import java.util.UUID;

public class Database {
    public static Lemon getUser(UUID uuid) {
        // TODO connect and use prepared statement to retrieve a Lemon

        return new Lemon(uuid);
    }
}
