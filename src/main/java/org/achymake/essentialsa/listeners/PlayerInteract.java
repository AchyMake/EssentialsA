package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public record PlayerInteract(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Harvester getHarvester() {
        return plugin.getHarvester();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null)return;
        Block block = event.getClickedBlock();
        Chunk chunk = block.getChunk();
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (getDatabase().isFrozen(player) || getDatabase().isJailed(player) || getDatabase().isVanished(player)) {
                event.setCancelled(true);
            } else if (getChunkdata().isClaimed(chunk)) {
                if (!getChunkdata().isPhysical(block))return;
                if (getChunkdata().hasAccess(player, chunk)) {
                    if (!disabledTrampling(block))return;
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
            } else if (disabledTrampling(block)) {
                event.setCancelled(true);
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getHand() != EquipmentSlot.HAND)return;
            if (getChunkdata().isClaimed(chunk)) {
                if (getChunkdata().hasAccess(player, chunk)) {
                    if (getHarvester().isHoe(player.getInventory().getItemInMainHand())) {
                        if (!getHarvester().isAllowHarvest(block))return;
                        getHarvester().harvest(player, block);
                    }
                    if (getEntities().hasPassenger(player)) {
                        if (!event.getBlockFace().equals(BlockFace.UP))return;
                        if (!getEntities().isAllowCarry(block))return;
                        Entity passenger = getEntities().getPassenger(player);
                        if (!getEntities().isEnableCarry(passenger))return;
                        if (!player.hasPermission("essentials.carry." + passenger.getType().toString().toLowerCase()))return;
                        player.swingMainHand();
                        getEntities().removeMount(player, passenger, block);
                    }
                } else {
                    if (!getChunkdata().isRightClickBlock(block))return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
            } else {
                if (getHarvester().isHoe(player.getInventory().getItemInMainHand())) {
                    if (!getHarvester().isAllowHarvest(block))return;
                    getHarvester().harvest(player, block);
                }
                if (player.getPassenger() != null) {
                    if (!event.getBlockFace().equals(BlockFace.UP)) return;
                    if (!getEntities().isAllowCarry(block)) return;
                    Entity passenger = player.getPassenger();
                    if (!getEntities().isEnableCarry(passenger)) return;
                    if (!player.hasPermission("essentials.carry." + passenger.getType().toString().toLowerCase())) return;
                    player.swingMainHand();
                    getEntities().removeMount(player, passenger, block);
                }
            }
        }
    }
    private boolean disabledTrampling(Block block) {
        if (block.getType().equals(Material.FARMLAND)) {
            return getConfig().getBoolean("physics.disable-tramping-farmland");
        } else if (block.getType().equals(Material.TURTLE_EGG)) {
            return getConfig().getBoolean("physics.disable-tramping-turtle-egg");
        } else if (block.getType().equals(Material.SNIFFER_EGG)) {
            return getConfig().getBoolean("physics.disable-tramping-sniffer-egg");
        } else {
            return false;
        }
    }
}