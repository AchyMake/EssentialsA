package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
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
    private ChestShop getChestShop() {
        return plugin.getChestShop();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Carry getCarry() {
        return plugin.getCarry();
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
            if (Tag.WALL_SIGNS.isTagged(block.getType())) {
                Sign sign = (Sign) block.getState();
                if (!getChestShop().isShop(sign))return;
                WallSign wallSign = (WallSign) sign.getBlockData();
                if (wallSign.getFacing().equals(BlockFace.EAST)) {
                    if (sign.getLocation().add(-1,0,0).getBlock().getState() instanceof Chest chest) {
                        event.setCancelled(true);
                        getChestShop().buy(player, sign, chest);
                    }
                } else if (wallSign.getFacing().equals(BlockFace.NORTH)) {
                    if (sign.getLocation().add(0,0,1).getBlock().getState() instanceof Chest chest) {
                        event.setCancelled(true);
                        getChestShop().buy(player, sign, chest);
                    }
                } else if (wallSign.getFacing().equals(BlockFace.WEST)) {
                    if (sign.getLocation().add(1,0,0).getBlock().getState() instanceof Chest chest) {
                        event.setCancelled(true);
                        getChestShop().buy(player, sign, chest);
                    }
                } else if (wallSign.getFacing().equals(BlockFace.SOUTH)) {
                    if (sign.getLocation().add(0,0,-1).getBlock().getState() instanceof Chest chest) {
                        event.setCancelled(true);
                        getChestShop().buy(player, sign, chest);
                    }
                }
            } else if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                Chest chest = (Chest) block.getState();
                if (!getChestShop().isShop(chest))return;
                if (player == getChestShop().getOwner(chest))return;
                if (getChestShop().isChestShopEditor(player))return;
                event.setCancelled(true);
            }
            if (event.getBlockFace().equals(BlockFace.UP)) {
                if (!getChairs().isAboveAir(block))return;
                if (!player.getInventory().getItemInMainHand().getType().isAir())return;
                if (!player.getInventory().getItemInOffHand().getType().isAir())return;
                if (!player.isOnGround())return;
                if (player.isSneaking())return;
                if (getChairs().hasChair(player))return;
                if (getChairs().isOccupied(block))return;
                getChairs().sit(player, block);
            }
            if (getChunkdata().isClaimed(chunk)) {
                if (getChunkdata().hasAccess(player, chunk)) {
                    if (getHarvester().isHoe(player.getInventory().getItemInMainHand())) {
                        if (!getHarvester().isAllowHarvest(block))return;
                        getHarvester().harvest(player, block);
                    }
                    if (getCarry().hasPassenger(player)) {
                        if (!event.getBlockFace().equals(BlockFace.UP))return;
                        if (!getCarry().isAllowCarry(block))return;
                        Entity passenger = getCarry().getPassenger(player);
                        if (!getCarry().isEnable(passenger))return;
                        if (!player.hasPermission("essentials.carry." + passenger.getType().toString().toLowerCase()))return;
                        event.setCancelled(true);
                        player.swingMainHand();
                        getCarry().removeMount(player, passenger, block);
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
                    if (!getCarry().isAllowCarry(block)) return;
                    Entity passenger = player.getPassenger();
                    if (!getCarry().isEnable(passenger)) return;
                    if (!player.hasPermission("essentials.carry." + passenger.getType().toString().toLowerCase()))return;
                    event.setCancelled(true);
                    player.swingMainHand();
                    getCarry().removeMount(player, passenger, block);
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