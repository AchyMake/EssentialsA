package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.MessageFormat;

public record BlockPlace(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin().getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        } else if (getChunkdata().isClaimed(chunk)) {
            if (getChunkdata().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
        } else {
            if (getConfig().getBoolean("notification.enable")) {
                if (!getConfig().getStringList("notification.block-place").contains(block.getType().toString()))return;
                String worldName = block.getWorld().getName();
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();
                for (Player players : getServer().getOnlinePlayers()) {
                    if (!players.hasPermission("essentials.event.block-place.notify"))return;
                    for (String messages : getConfig().getStringList("notification.message")) {
                        players.sendMessage(getMessage().addColor(MessageFormat.format(messages, player.getName(), block.getType().toString(), worldName, x, y, z)));
                    }
                }
            }
            if (block.getType().equals(Material.SPAWNER)) {
                ItemStack itemInHand = event.getItemInHand();
                ItemMeta itemInHandMeta = itemInHand.getItemMeta();
                if (itemInHandMeta.getPersistentDataContainer().has(NamespacedKey.minecraft("entity"), PersistentDataType.STRING)) {
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    spawner.setSpawnedType(EntityType.valueOf(itemInHandMeta.getPersistentDataContainer().get(NamespacedKey.minecraft("entity"), PersistentDataType.STRING)));
                    spawner.update();
                }
            }
        }
    }
}