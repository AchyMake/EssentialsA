package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.ChestShop;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Tag;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
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
    private ChestShop getChestShop() {
        return plugin.getChestShop();
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
        } else if (cancel(player, block)) {
            event.setCancelled(true);
        } else if (getChunkdata().isClaimed(block.getChunk())) {
            if (getChunkdata().hasAccess(player, block.getChunk()))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(block.getChunk()).getName());
        } else {
            if (!player.getGameMode().equals(GameMode.SURVIVAL))return;
            notifyAdmin(player, block);
            if (block.getType().equals(Material.BUDDING_AMETHYST)) {
                dropAmethyst(player, block);
            } else if (block.getType().equals(Material.SPAWNER)) {
                dropSpawner(player, block);
                event.setExpToDrop(0);
            }
        }
    }
    private boolean cancel(Player player, Block block) {
        if (Tag.WALL_SIGNS.isTagged(block.getType())) {
            Sign sign = (Sign) block.getState();
            if (getChestShop().isShop(sign)) {
                if (sign.getBlockData() instanceof WallSign wallSign) {
                    if (wallSign.getFacing().equals(BlockFace.EAST)) {
                        if (sign.getLocation().add(-1,0,0).getBlock().getState() instanceof Chest chest) {
                            if (getChestShop().getOwner(chest) == player || getChestShop().isChestShopEditor(player)) {
                                getChestShop().removeOwner(chest);
                                chest.update();
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else if (wallSign.getFacing().equals(BlockFace.NORTH)) {
                        if (sign.getLocation().add(0,0,1).getBlock().getState() instanceof Chest chest) {
                            if (getChestShop().getOwner(chest) == player || getChestShop().isChestShopEditor(player)) {
                                getChestShop().removeOwner(chest);
                                chest.update();
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else if (wallSign.getFacing().equals(BlockFace.WEST)) {
                        if (sign.getLocation().add(1,0,0).getBlock().getState() instanceof Chest chest) {
                            if (getChestShop().getOwner(chest) == player || getChestShop().isChestShopEditor(player)) {
                                getChestShop().removeOwner(chest);
                                chest.update();
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else if (wallSign.getFacing().equals(BlockFace.SOUTH)) {
                        if (sign.getLocation().add(0,0,-1).getBlock().getState() instanceof Chest chest) {
                            if (getChestShop().getOwner(chest) == player || getChestShop().isChestShopEditor(player)) {
                                getChestShop().removeOwner(chest);
                                chest.update();
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            Chest chest = (Chest) block.getState();
            if (getChestShop().isShop(chest)) {
                return player != getChestShop().getOwner(chest) && !getChestShop().isChestShopEditor(player);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private void notifyAdmin(Player player, Block block) {
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
    }
    private void dropAmethyst(Player player, Block block) {
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
    }
    private void dropSpawner(Player player, Block block) {
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
    }
    private boolean isDisabled(Player player) {
        return getDatabase().isFrozen(player) || getDatabase().isJailed(player);
    }
}