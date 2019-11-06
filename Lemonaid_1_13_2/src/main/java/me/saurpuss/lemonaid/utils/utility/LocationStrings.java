package me.saurpuss.lemonaid.utils.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;
import java.util.StringJoiner;

public interface LocationStrings {

    default Location locationFromString(String s) {
        String[] loc = s.split("\\|");
        return new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]),
                Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
    }
    default String stringFromLocation(Location location) {
        StringJoiner string = new StringJoiner("|");
        string.add(Objects.requireNonNull(location.getWorld()).toString());
        string.add(String.valueOf(location.getBlockX()));
        string.add(String.valueOf(location.getBlockY()));
        string.add(String.valueOf(location.getBlockZ()));

        return string.toString();
    }
}
