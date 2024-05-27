package me.yuqas;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerDataManager {
    private final Main plugin;
    private final File databaseFile;
    private final YamlConfiguration database;

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;
        this.databaseFile = new File(plugin.getDataFolder(), "database.yml");
        this.database = YamlConfiguration.loadConfiguration(databaseFile);
    }

    public void savePlayerLocation(Player player, Location location) {
        String path = "players." + player.getUniqueId() + ".location";
        database.set(path + ".world", location.getWorld().getName());
        database.set(path + ".x", location.getX());
        database.set(path + ".y", location.getY());
        database.set(path + ".z", location.getZ());
        saveDatabase();
    }

    public Location getPlayerLocation(Player player) {
        String path = "players." + player.getUniqueId() + ".location";
        if (!database.contains(path)) {
            return null;
        }

        String world = database.getString(path + ".world");
        double x = database.getDouble(path + ".x");
        double y = database.getDouble(path + ".y");
        double z = database.getDouble(path + ".z");

        return new Location(plugin.getServer().getWorld(world), x, y, z);
    }

    private void saveDatabase() {
        try {
            database.save(databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
