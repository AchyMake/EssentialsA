package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public record Database(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private List<Player> getVanished() {
        return plugin.getVanished();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public void teleport(Player player, String name, Location location) {
        if (getUserdata().hasTaskID(player, "teleport")) {
            getMessage().sendActionBar(player, "&cYou cannot teleport twice you have to wait");
        } else {
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }
            getMessage().sendActionBar(player, "&6Teleporting in&f " + getConfig().getInt("teleport.delay") + "&6 seconds");
            int taskID = plugin.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    getMessage().sendActionBar(player, "&6Teleporting to&f " + name);
                    player.teleport(location);
                    getUserdata().setString(player, "tasks.teleport", null);
                }
            },getConfig().getInt("teleport.delay") * 20L).getTaskId();
            getUserdata().addTaskID(player, "teleport", taskID);
        }
    }
    public Block highestRandomBlock() {
        String worldName = getConfig().getString("commands.rtp.world");
        int x = new Random().nextInt(0, getConfig().getInt("commands.rtp.spread"));
        int z = new Random().nextInt(0, getConfig().getInt("commands.rtp.spread"));
        return getServer().getWorld(worldName).getHighestBlockAt(x, z);
    }
    public void randomTeleport(Player player) {
        Block block = highestRandomBlock();
        if (block.isLiquid()) {
            getMessage().sendActionBar(player, "&cFinding new location due to liquid block");
            randomTeleport(player);
        } else {
            block.getChunk().load();
            getMessage().sendActionBar(player, "&6Teleporting");
            player.teleport(block.getLocation().add(0.5,1,0.5));
        }
    }
    public ItemStack getItem(String type, int amount) {
        return new ItemStack(Material.valueOf(type.toUpperCase()), amount);
    }
    public ItemStack getOfflinePlayerHead(OfflinePlayer offlinePlayer, int amount) {
        if (offlinePlayer == null) {
            return getItem("player_head", amount);
        } else {
            ItemStack skullItem = getItem("player_head", amount);
            SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
            skullMeta.setOwningPlayer(offlinePlayer);
            skullItem.setItemMeta(skullMeta);
            return skullItem;
        }
    }
    public Material getMaterial(String name) {
        return Material.valueOf(name.toUpperCase());
    }
    public void giveItems(Player player, Collection<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
                player.getInventory().addItem(itemStack);
            } else {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
        }
    }
    public void giveItem(Player player, ItemStack itemStack) {
        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
    public boolean hasMoved(Location from, Location to) {
        if (from.getX() != to.getX()) {
            return true;
        } else if (from.getY() != to.getY()) {
            return true;
        } else return from.getZ() != to.getZ();
    }
    public ItemStack getSpawner(String entityType, int amount) {
        ItemStack spawner = getItem("spawner", amount);
        ItemMeta itemMeta = spawner.getItemMeta();
        itemMeta.getPersistentDataContainer().set(NamespacedKey.minecraft("entity"), PersistentDataType.STRING, entityType.toUpperCase());
        if (getConfig().isString("entities." + entityType.toUpperCase() + ".name")) {
            itemMeta.setDisplayName(getMessage().addColor("&dSpawner:&f " + getConfig().getString("entities." + entityType.toUpperCase() + ".name")));
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        spawner.setItemMeta(itemMeta);
        return spawner;
    }
    public List<Player> getOnlinePlayers() {
        List<Player> onlinePlayers = new ArrayList<>();
        for (Player players : getServer().getOnlinePlayers()) {
            if (!getVanished().contains(players)) {
                onlinePlayers.add(players);
            }
        }
        return onlinePlayers;
    }
}