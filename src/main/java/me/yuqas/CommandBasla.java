package me.yuqas;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CommandBasla implements CommandExecutor, Listener {
    private final Main plugin;
    private static final long TELEPORT_COOLDOWN = 600 * 1000; // 600 seconds in milliseconds

    public CommandBasla(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;
        openGui(player);

        return true;
    }

    private void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Başlama Seçenekleri");

        ItemStack buradaItem = new ItemStack(Material.PAPER);
        ItemMeta buradaMeta = buradaItem.getItemMeta();
        buradaMeta.setDisplayName(ChatColor.YELLOW + "Olduğun yerden başla");
        buradaItem.setItemMeta(buradaMeta);
        gui.setItem(3, buradaItem);

        ItemStack randomItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta randomMeta = randomItem.getItemMeta();
        randomMeta.setDisplayName(ChatColor.YELLOW + "Rastgele bir yerden başla");
        randomItem.setItemMeta(randomMeta);
        gui.setItem(4, randomItem);

        ItemStack konumItem = new ItemStack(Material.COMPASS);
        ItemMeta konumMeta = konumItem.getItemMeta();
        konumMeta.setDisplayName(ChatColor.YELLOW + "Konum belirle");
        konumItem.setItemMeta(konumMeta);
        gui.setItem(5, konumItem);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Başlama Seçenekleri")) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        HashMap<UUID, Long> cooldowns = plugin.getRandomTeleportCooldowns();
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        switch (event.getSlot()) {
            case 3:
                Location loc = playerDataManager.getPlayerLocation(player);
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage(ChatColor.GREEN + "Kayıtlı konumuna ışınlandın.");
                } else {
                    player.sendMessage(ChatColor.RED + "Henüz kayıtlı bir konumun yok.");
                }
                break;
            case 4:
                if (cooldowns.containsKey(playerUUID) && (currentTime - cooldowns.get(playerUUID)) < TELEPORT_COOLDOWN) {
                    long timeLeft = (TELEPORT_COOLDOWN - (currentTime - cooldowns.get(playerUUID))) / 1000;
                    player.sendMessage(ChatColor.RED + "Rastgele ışınlanmayı kullanabilmek için " + timeLeft + " saniye beklemelisin.");
                } else {
                    player.closeInventory();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int minX = plugin.getConfig().getInt("randomSpawn.minX");
                            int maxX = plugin.getConfig().getInt("randomSpawn.maxX");
                            int minZ = plugin.getConfig().getInt("randomSpawn.minZ");
                            int maxZ = plugin.getConfig().getInt("randomSpawn.maxZ");

                            double x = minX + (Math.random() * (maxX - minX + 1));
                            double z = minZ + (Math.random() * (maxZ - minZ + 1));
                            Location randomLocation = new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt((int) x, (int) z), z);
                            player.teleport(randomLocation);
                            player.sendMessage(ChatColor.GREEN + "Rastgele bir konuma ışınlandın.");
                            cooldowns.put(playerUUID, System.currentTimeMillis());
                        }
                    }.runTaskLater(plugin, 60L); // 3 seconds delay
                }
                break;
            case 5:
                playerDataManager.savePlayerLocation(player, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Mevcut konumun kaydedildi.");
                break;
            default:
                break;
        }
    }
}
