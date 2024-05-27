package me.yuqas;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
    private PlayerDataManager playerDataManager;
    private HashMap<UUID, Long> randomTeleportCooldowns;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        playerDataManager = new PlayerDataManager(this);
        randomTeleportCooldowns = new HashMap<>();

        getCommand("basla").setExecutor(new CommandBasla(this));
        getCommand("spawn").setExecutor(new CommandSpawn(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public HashMap<UUID, Long> getRandomTeleportCooldowns() {
        return randomTeleportCooldowns;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        double x = getConfig().getDouble("firstSpawn.x");
        double y = getConfig().getDouble("firstSpawn.y");
        double z = getConfig().getDouble("firstSpawn.z");
        String world = getConfig().getString("firstSpawn.world");

        Location firstSpawn = new Location(getServer().getWorld(world), x, y, z);
        player.teleport(firstSpawn);
        player.sendMessage(ChatColor.GREEN + "Hoş geldin! Başlangıç noktasına ışınlandın.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.savePlayerLocation(player, player.getLocation());
    }
}
//

