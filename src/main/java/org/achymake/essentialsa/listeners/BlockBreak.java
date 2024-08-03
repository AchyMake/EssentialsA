package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.*;
import org.bukkit.block.*;
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
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private ChestShop getChestShop() {
        return plugin.getChestShop();
    }
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        } else if (getChestShop().isShop(block)) {
            Chest chest = getChestShop().getShop(block);
            if (getChestShop().getOwner(chest) == player) {
                getChestShop().removeOwner(chest);
                chest.update();
            } else {
                event.setCancelled(true);
                getMessage().send(player, "&cShop is owned by&f " + getChestShop().getOwner(getChestShop().getShop(block)).getName());
            }
        } else if (getChunks().isEnable()) {
            Chunk chunk = block.getChunk();
            if (!getChunks().isClaimed(chunk))return;
            if (!getChunks().isDisableBlockBreak())return;
            if (getChunks().hasAccess(player, chunk)) {
                notifyAdmin(player, block);
            } else {
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
            }
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
        if (getUserdata().isAutoPick(player)) {
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
        if (getUserdata().isAutoPick(player)) {
            getDatabase().giveItem(player, getDatabase().getSpawner(spawner.getSpawnedType().toString(), 1));
        } else {
            getServer().getWorld(player.getWorld().getName()).dropItem(block.getLocation().add(0.5,0.5,0.5), getDatabase().getSpawner(spawner.getSpawnedType().toString(), 1));
        }
    }
}