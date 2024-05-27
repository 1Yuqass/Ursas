package me.yuqas;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandSpawn implements CommandExecutor {
    private final Main plugin;

    public CommandSpawn(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + "3 saniye sonra spawn noktasına ışınlanacaksınız...");

        new BukkitRunnable() {
            @Override
            public void run() {
                double x = plugin.getConfig().getDouble("firstSpawn.x");
                double y = plugin.getConfig().getDouble("firstSpawn.y");
                double z = plugin.getConfig().getDouble("firstSpawn.z");
                String world = plugin.getConfig().getString("firstSpawn.world");

                Location spawnLocation = new Location(plugin.getServer().getWorld(world), x, y, z);
                player.teleport(spawnLocation);
                player.sendMessage(ChatColor.GREEN + "Spawn noktasına ışınlandın.");
            }
        }.runTaskLater(plugin, 60L); // 3 seconds delay

        return true;
    }
}

