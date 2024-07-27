package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public record Kits(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private HashMap<String, Long> getKitCooldown() {
        return plugin.getKitCooldown();
    }
    public boolean exist() {
        return getFile().exists();
    }
    public File getFile() {
        return new File(getDataFolder(),"kits.yml");
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public List<String> getKits() {
        return new ArrayList<>(getConfig().getKeys(false));
    }
    public boolean hasPrice(String kitName) {
        return getConfig().isDouble(kitName + ".cost");
    }
    public double cost(String kitName) {
        return getConfig().getDouble(kitName + ".cost");
    }
    public List<ItemStack> getKit(String kitName) {
        List<ItemStack> giveItems = new ArrayList<>();
        for (String items : getConfig().getConfigurationSection(kitName + ".items").getKeys(false)) {
            String materialString = getConfig().getString(kitName + ".items." + items + ".type");
            int amount = getConfig().getInt(kitName + ".items." + items + ".amount");
            ItemStack item = getDatabase().getItem(materialString, amount);
            ItemMeta itemMeta = item.getItemMeta();
            if (getConfig().getKeys(true).contains(kitName + ".items." + items + ".name")) {
                String displayName = getConfig().getString(kitName + ".items." + items + ".name");
                itemMeta.setDisplayName(getMessage().addColor(displayName));
            }
            if (getConfig().getKeys(true).contains(kitName + ".items." + items + ".lore")) {
                List<String> lore = new ArrayList<>();
                for (String listedLore : getConfig().getStringList(kitName + ".items." + items + ".lore")) {
                    lore.add(getMessage().addColor(listedLore));
                }
                itemMeta.setLore(lore);
            }
            if (getConfig().getKeys(true).contains(kitName+".items." + items + ".enchantments")) {
                for (String enchantList : getConfig().getConfigurationSection(kitName + ".items." + items + ".enchantments").getKeys(false)) {
                    String enchantmentString = getConfig().getString(kitName + ".items." + items + ".enchantments." + enchantList + ".type");
                    Enchantment enchantment = Enchantment.getByName(enchantmentString);
                    int enchantmentLevel = getConfig().getInt(kitName + ".items." + items + ".enchantments." + enchantList + ".amount");
                    itemMeta.addEnchant(enchantment, enchantmentLevel, true);
                }
            }
            item.setItemMeta(itemMeta);
            giveItems.add(item);
        }
        return giveItems;
    }
    public void giveKit(Player player, String kitName) {
        getDatabase().giveItems(player, getKit(kitName));
    }
    public boolean hasCooldown(Player player, String kitName) {
        if (getKitCooldown().containsKey(kitName + "-" + player.getUniqueId())) {
            long timeElapsed = System.currentTimeMillis() - getKitCooldown().get(kitName + "-" + player.getUniqueId());
            String cooldownTimer = getConfig().getString(kitName + ".cooldown");
            int integer = Integer.valueOf(cooldownTimer.replace(cooldownTimer, cooldownTimer + "000"));
            return timeElapsed < integer;
        } else {
            return false;
        }
    }
    public void addCooldown(Player player, String kitName) {
        if (getKitCooldown().containsKey(kitName + "-" + player.getUniqueId())) {
            long timeElapsed = System.currentTimeMillis() - getKitCooldown().get(kitName + "-" + player.getUniqueId());
            String cooldownTimer = getConfig().getString(kitName + ".cooldown");
            int integer = Integer.valueOf(cooldownTimer.replace(cooldownTimer, cooldownTimer + "000"));
            if (timeElapsed > integer) {
                getKitCooldown().put(kitName + "-" + player.getUniqueId(), System.currentTimeMillis());
            }
        } else {
            getKitCooldown().put(kitName + "-" + player.getUniqueId(), System.currentTimeMillis());
        }
    }
    public String getCooldown(Player player, String kitName) {
        if (getKitCooldown().containsKey(kitName + "-" + player.getUniqueId())) {
            long timeElapsed = System.currentTimeMillis() - getKitCooldown().get(kitName + "-" + player.getUniqueId());
            String cooldownTimer = getConfig().getString(kitName + ".cooldown");
            int integer = Integer.valueOf(cooldownTimer.replace(cooldownTimer, cooldownTimer + "000"));
            if (timeElapsed < integer) {
                long timer = (integer-timeElapsed);
                return String.valueOf(timer).substring(0, String.valueOf(timer).length() - 3);
            }
        } else {
            return "0";
        }
        return "0";
    }
    public void reload() {
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (exist()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            List<String> lore = new ArrayList<>();
            lore.add("&9from");
            lore.add("&7-&6 Starter");
            config.addDefault("starter.cooldown", 3600);
            config.addDefault("starter.cost", 75.0);
            config.addDefault("starter.items.sword.type", "STONE_SWORD");
            config.addDefault("starter.items.sword.amount", 1);
            config.addDefault("starter.items.sword.name", "&6Stone Sword");
            config.addDefault("starter.items.sword.lore", lore);
            config.addDefault("starter.items.sword.enchantments.unbreaking.type", "UNBREAKING");
            config.addDefault("starter.items.sword.enchantments.unbreaking.amount", 1);
            config.addDefault("starter.items.pickaxe.type", "STONE_PICKAXE");
            config.addDefault("starter.items.pickaxe.amount", 1);
            config.addDefault("starter.items.pickaxe.name", "&6Stone Pickaxe");
            config.addDefault("starter.items.pickaxe.lore", lore);
            config.addDefault("starter.items.pickaxe.enchantments.unbreaking.type", "UNBREAKING");
            config.addDefault("starter.items.pickaxe.enchantments.unbreaking.amount", 1);
            config.addDefault("starter.items.axe.type", "STONE_AXE");
            config.addDefault("starter.items.axe.amount", 1);
            config.addDefault("starter.items.axe.name", "&6Stone Axe");
            config.addDefault("starter.items.axe.lore", lore);
            config.addDefault("starter.items.axe.enchantments.unbreaking.type", "UNBREAKING");
            config.addDefault("starter.items.axe.enchantments.unbreaking.amount", 1);
            config.addDefault("starter.items.shovel.type", "STONE_SHOVEL");
            config.addDefault("starter.items.shovel.amount", 1);
            config.addDefault("starter.items.shovel.name", "&6Stone Shovel");
            config.addDefault("starter.items.shovel.lore", lore);
            config.addDefault("starter.items.shovel.enchantments.unbreaking.type", "UNBREAKING");
            config.addDefault("starter.items.shovel.enchantments.unbreaking.amount", 1);
            config.addDefault("starter.items.food.type", "COOKED_BEEF");
            config.addDefault("starter.items.food.amount", 16);
            config.addDefault("food.cooldown", 3600);
            config.addDefault("food.items.food.type", "COOKED_BEEF");
            config.addDefault("food.items.food.amount", 16);
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}