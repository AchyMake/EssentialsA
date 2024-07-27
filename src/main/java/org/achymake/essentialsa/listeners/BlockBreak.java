package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;

public record BlockBreak(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isDisabled(player)) {
            event.setCancelled(true);
        } else if (getChunkdata().isClaimed(block.getChunk())) {
            if (getChunkdata().hasAccess(player, block.getChunk()))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(block.getChunk()).getName());
        } else {
            if (!player.getGameMode().equals(GameMode.SURVIVAL))return;
            if (getConfig().getBoolean("notification.enable")) {
                if (!getConfig().getStringList("notification.block-break").contains(block.getType().toString()))return;
                String worldName = block.getWorld().getName();
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();
                for (Player players : getServer().getOnlinePlayers()) {
                    if (!players.hasPermission("essentials.event.block-break.notify"))return;
                    for (String messages : getConfig().getStringList("notification.message")) {
                        players.sendMessage(getMessage().addColor(MessageFormat.format(messages, player.getName(), block.getType().toString(), worldName, x, y, z)));
                    }
                }
            }
            if (block.getType().equals(Material.BUDDING_AMETHYST)) {
                if (player.getInventory().getItemInMainHand().isEmpty())return;
                if (!player.hasPermission("essentials.silk_touch.budding_amethyst"))return;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                if (!Tag.ITEMS_PICKAXES.isTagged(itemInMainHand.getType()))return;
                if (!itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH))return;
                if (getDatabase().isAutoPick(player)) {
                    getDatabase().giveItem(player, getDatabase().getItem(block.getType().toString(), 1));
                } else {
                    getServer().getWorld(player.getWorld().getName()).dropItem(block.getLocation().add(0.5,0.5,0.5), getDatabase().getItem(block.getType().toString(), 1));
                }
            } else if (block.getType().equals(Material.SPAWNER)) {
                if (player.getInventory().getItemInMainHand().isEmpty())return;
                if (!player.hasPermission("essentials.silk_touch.spawner"))return;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                if (!Tag.ITEMS_PICKAXES.isTagged(itemInMainHand.getType()))return;
                if (!itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH))return;
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                if (getDatabase().isAutoPick(player)) {
                    getDatabase().giveItem(player, getDatabase().getSpawner(spawner.getSpawnedType().toString(), 1));
                } else {
                    getServer().getWorld(player.getWorld().getName()).dropItem(block.getLocation().add(0.5,0.5,0.5), getDatabase().getSpawner(spawner.getSpawnedType().toString(), 1));
                }
                event.setExpToDrop(0);
            }
        }
    }
    private boolean isDisabled(Player player) {
        return getDatabase().isFrozen(player) || getDatabase().isJailed(player);
    }
}