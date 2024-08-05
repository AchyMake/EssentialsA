package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
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
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private ChestShop getChestShop() {
        return plugin.getChestShop();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (event.getClickedBlock() == null)return;
            Block block = event.getClickedBlock();
            if (getUserdata().isFrozen(player) || getUserdata().isJailed(player) || getUserdata().isVanished(player)) {
                event.setCancelled(true);
            } else if (disabledTrampling(block)) {
                event.setCancelled(true);
            }
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() == null)return;
            Block block = event.getClickedBlock();
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