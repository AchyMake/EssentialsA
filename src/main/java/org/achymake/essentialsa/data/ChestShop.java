package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record ChestShop(EssentialsA plugin) {
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private List<Player> getChestShopEditors() {
        return plugin.getChestShopEditors();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public PersistentDataContainer getData(Sign sign) {
        return sign.getPersistentDataContainer();
    }
    public PersistentDataContainer getData(Chest chest) {
        return chest.getPersistentDataContainer();
    }
    public boolean isChestShopEditor(Player player) {
        return getChestShopEditors().contains(player);
    }
    public void toggleChestShopEditor(Player player) {
        setChestShopEditor(player, !isChestShopEditor(player));
    }
    public void setChestShopEditor(Player player, boolean value) {
        if (value) {
            if (!getChestShopEditors().contains(player)) {
                getChestShopEditors().add(player);
            }
        } else {
            if (getChestShopEditors().contains(player)) {
                getChestShopEditors().remove(player);
            }
        }
    }
    public boolean isShop(Block block) {
        if (Tag.WALL_SIGNS.isTagged(block.getType())) {
            Sign sign = (Sign) block.getState();
            return isShop(sign);
        } else if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            Chest chest = (Chest) block.getState();
            return isShop(chest);
        } else {
            return false;
        }
    }
    public Chest getShop(Block block) {
        if (Tag.WALL_SIGNS.isTagged(block.getType())) {
            Sign sign = (Sign) block.getState();
            if (isShop(sign)) {
                if (sign.getBlockData() instanceof WallSign wallSign) {
                    if (wallSign.getFacing().equals(BlockFace.EAST)) {
                        if (sign.getLocation().add(-1,0,0).getBlock().getState() instanceof Chest chest) {
                            return chest;
                        } else {
                            return null;
                        }
                    } else if (wallSign.getFacing().equals(BlockFace.NORTH)) {
                        if (sign.getLocation().add(0,0,1).getBlock().getState() instanceof Chest chest) {
                            return chest;
                        } else {
                            return null;
                        }
                    } else if (wallSign.getFacing().equals(BlockFace.WEST)) {
                        if (sign.getLocation().add(1,0,0).getBlock().getState() instanceof Chest chest) {
                            return chest;
                        } else {
                            return null;
                        }
                    } else if (wallSign.getFacing().equals(BlockFace.SOUTH)) {
                        if (sign.getLocation().add(0,0,-1).getBlock().getState() instanceof Chest chest) {
                            return chest;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            Chest chest = (Chest) block.getState();
            if (isShop(chest)) {
                return chest;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    public void addOwner(OfflinePlayer offlinePlayer, Sign sign) {
        getData(sign).set(NamespacedKey.minecraft("owner"), PersistentDataType.STRING, offlinePlayer.getUniqueId().toString());
    }
    public void addOwner(OfflinePlayer offlinePlayer, Chest chest) {
        getData(chest).set(NamespacedKey.minecraft("owner"), PersistentDataType.STRING, offlinePlayer.getUniqueId().toString());
    }
    public void removeOwner(Chest chest) {
        getData(chest).remove(NamespacedKey.minecraft("owner"));
    }
    public void addAmount(Sign sign, int value) {
        getData(sign).set(NamespacedKey.minecraft("amount"), PersistentDataType.INTEGER, value);
    }
    public void addValue(Sign sign, double value) {
        getData(sign).set(NamespacedKey.minecraft("value"), PersistentDataType.DOUBLE, value);
    }
    public void addMaterial(Sign sign, String value) {
        getData(sign).set(NamespacedKey.minecraft("material"), PersistentDataType.STRING, value);
    }
    public void setupShop(Player player, Sign sign, Chest chest) {
        String line1 = sign.getLine(0);
        int line2= Integer.parseInt(sign.getLine(1));
        String line3 = sign.getLine(2).toUpperCase().replace(" ", "_");
        double line4 = Double.parseDouble(sign.getLine(3));
        if (line1.equalsIgnoreCase("[sell]")) {
            if (!player.hasPermission("essentials.chestshop.create"))return;
            plugin.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    addAmount(sign, line2);
                    addMaterial(sign, line3);
                    addValue(sign, line4);
                    sign.setLine(0, getMessage().addColor("&6" + player.getName()));
                    sign.setLine(1, getMessage().addColor("&f" + getAmount(sign)));
                    sign.setLine(2, getMessage().addColor("&f" + getMaterial(sign).name()));
                    sign.setLine(3, getMessage().addColor("&a" + getEconomy().currencyNamePlural() + getEconomy().format(getValue(sign))));
                    sign.setWaxed(true);
                    addOwner(player, sign);
                    sign.update();
                    addOwner(player, chest);
                    chest.update();
                    getMessage().send(player, "&6You created a chest shop");
                }
            },3);
        }
    }
    public void buy(Player player, Sign sign, Chest chest) {
        if (getOwner(chest) == player || getOwner(sign) == player) {
            getMessage().send(player, "&cYou can not buy from your own shop");
        } else if (chest.getBlockInventory().isEmpty()) {
            getMessage().send(player, "&cChest is is currently out of stuck");
        } else {
            if (chest.getInventory().contains(getMaterial(sign))) {
                int first = chest.getInventory().first(getMaterial(sign));
                ItemStack items = chest.getInventory().getItem(first);
                if (items.getAmount() >= getAmount(sign)) {
                    if (getEconomy().has(player, getValue(sign))) {
                        getEconomy().withdrawPlayer(player, getValue(sign));
                        getEconomy().depositPlayer(getOwner(sign), getValue(sign));
                        ItemStack bought = new ItemStack(items.getType());
                        bought.setItemMeta(items.getItemMeta());
                        bought.setAmount(getAmount(sign));
                        giveItems(player, bought);
                        items.setAmount(items.getAmount() - getAmount(sign));
                        getMessage().send(player, "&6You bought &f" + getAmount(sign) + " " + getMaterial(sign).name() + "&6 for &a" + getEconomy().currencyNamePlural() + getEconomy().format(getValue(sign)));
                    } else {
                        getMessage().send(player, "&cYou do not have&a " + getEconomy().currencyNamePlural() + getEconomy().format(getValue(sign)));
                    }
                } else {
                    getMessage().send(player, "&cChest is currently out of stuck");
                }
            } else {
                getMessage().send(player, "&cChest is currently out of stuck");
            }
        }
    }
    public boolean isShop(Sign sign) {
        return getData(sign).has(NamespacedKey.minecraft("owner"), PersistentDataType.STRING);
    }
    public boolean isShop(Chest chest) {
        return getData(chest).has(NamespacedKey.minecraft("owner"), PersistentDataType.STRING);
    }
    public OfflinePlayer getOwner(Chest chest) {
        return plugin.getServer().getOfflinePlayer(UUID.fromString(getData(chest).get(NamespacedKey.minecraft("owner"), PersistentDataType.STRING)));
    }
    public OfflinePlayer getOwner(Sign sign) {
        return plugin.getServer().getOfflinePlayer(UUID.fromString(getData(sign).get(NamespacedKey.minecraft("owner"), PersistentDataType.STRING)));
    }
    public int getAmount(Sign sign) {
        return getData(sign).get(NamespacedKey.minecraft("amount"), PersistentDataType.INTEGER);
    }
    public double getValue(Sign sign) {
        return getData(sign).get(NamespacedKey.minecraft("value"), PersistentDataType.DOUBLE);
    }
    public Material getMaterial(Sign sign) {
        return Material.valueOf(getData(sign).get(NamespacedKey.minecraft("material"), PersistentDataType.STRING));
    }
    private void giveItems(Player player, ItemStack itemStack) {
        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
}