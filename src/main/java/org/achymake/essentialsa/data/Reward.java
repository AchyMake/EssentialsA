package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Server;
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
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public record Reward(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public File getFile() {
        return new File(getDataFolder(),"reward.yml");
    }
    public boolean exist() {
        return getFile().exists();
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean isEnable() {
        return getConfig().getBoolean("enable");
    }
    public List<String> getRewards() {
        return new ArrayList<>(getConfig().getKeys(false));
    }
    public List<ItemStack> getItems(String rewardName) {
        List<ItemStack> giveItems = new ArrayList<>();
        for (String items : getConfig().getConfigurationSection("rewards." + rewardName + ".items").getKeys(false)) {
            String materialString = getConfig().getString("rewards." + rewardName + ".items." + items + ".type");
            int amount = getConfig().getInt("rewards." + rewardName + ".items." + items + ".amount");
            ItemStack item = getDatabase().getItem(materialString, amount);
            ItemMeta itemMeta = item.getItemMeta();
            if (getConfig().getKeys(true).contains("rewards." + rewardName + ".items." + items + ".name")) {
                String displayName = getConfig().getString("rewards." + rewardName + ".items." + items + ".name");
                itemMeta.setDisplayName(getMessage().addColor(displayName));
            }
            if (getConfig().getKeys(true).contains("rewards." + rewardName + ".items." + items + ".lore")) {
                List<String> lore = new ArrayList<>();
                for (String listedLore : getConfig().getStringList("rewards." + rewardName + ".items." + items + ".lore")) {
                    lore.add(getMessage().addColor(listedLore));
                }
                itemMeta.setLore(lore);
            }
            if (getConfig().getKeys(true).contains("rewards." + rewardName+".items." + items + ".enchantments")) {
                for (String enchantList : getConfig().getConfigurationSection("rewards." + rewardName + ".items." + items + ".enchantments").getKeys(false)) {
                    String enchantmentString = getConfig().getString("rewards." + rewardName + ".items." + items + ".enchantments." + enchantList + ".type");
                    Enchantment enchantment = Enchantment.getByName(enchantmentString);
                    int enchantmentLevel = getConfig().getInt("rewards." + rewardName + ".items." + items + ".enchantments." + enchantList + ".amount");
                    itemMeta.addEnchant(enchantment, enchantmentLevel, true);
                }
            }
            item.setItemMeta(itemMeta);
            giveItems.add(item);
        }
        return giveItems;
    }
    public void giveReward(Player player) {
        int random = new Random().nextInt(100);
        List<String> test = new ArrayList<>();
        test.add("common");
        for (String reward : getRewards()) {
            if (random < getConfig().getInt("rewards." + reward + ".chance")) {
                test.clear();
                test.add(reward);
            }
        }
        customReward(player, test.getFirst());
        test.clear();
    }
    public void customReward(Player player, String rewardName) {
        getDatabase().giveItems(player, getItems(rewardName));
        for (String commands : getConfig().getStringList("rewards." + rewardName + ".commands")) {
            getServer().dispatchCommand(getServer().getConsoleSender(), commands.replace("%player%", player.getName()));
        }
        getMessage().send(player, "&6You received the&f " + rewardName + "&6 reward");
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
            lore.add("&7-&6 Vote");
            List<String> commands = new ArrayList<>();
            commands.add("eco add %player% 25");
            config.set("enable", true);
            config.set("rewards.common.commands", commands);
            config.set("rewards.common.items.diamond.type", "DIAMOND");
            config.set("rewards.common.items.diamond.name", "Diamond");
            config.set("rewards.common.items.diamond.amount", 1);
            config.set("rewards.common.items.diamond.lore", lore);
            config.set("rewards.common.items.diamond.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.common.items.diamond.enchantments.unbreaking.amount", 1);
            config.set("rewards.common.items.emerald.type", "EMERALD");
            config.set("rewards.common.items.emerald.name", "Emerald");
            config.set("rewards.common.items.emerald.amount", 3);
            config.set("rewards.common.items.emerald.lore", lore);
            config.set("rewards.common.items.emerald.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.common.items.emerald.enchantments.unbreaking.amount", 1);
            config.set("rewards.uncommon.chance", 75);
            config.set("rewards.uncommon.commands", commands);
            config.set("rewards.uncommon.items.diamond.type", "DIAMOND");
            config.set("rewards.uncommon.items.diamond.name", "Diamond");
            config.set("rewards.uncommon.items.diamond.amount", 2);
            config.set("rewards.uncommon.items.diamond.lore", lore);
            config.set("rewards.uncommon.items.diamond.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.uncommon.items.diamond.enchantments.unbreaking.amount", 1);
            config.set("rewards.uncommon.items.emerald.type", "EMERALD");
            config.set("rewards.uncommon.items.emerald.name", "Emerald");
            config.set("rewards.uncommon.items.emerald.amount", 6);
            config.set("rewards.uncommon.items.emerald.lore", lore);
            config.set("rewards.uncommon.items.emerald.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.uncommon.items.emerald.enchantments.unbreaking.amount", 1);
            config.set("rewards.rare.chance", 50);
            config.set("rewards.rare.commands", commands);
            config.set("rewards.rare.items.diamond.type", "DIAMOND");
            config.set("rewards.rare.items.diamond.name", "Diamond");
            config.set("rewards.rare.items.diamond.amount", 4);
            config.set("rewards.rare.items.diamond.lore", lore);
            config.set("rewards.rare.items.diamond.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.rare.items.diamond.enchantments.unbreaking.amount", 1);
            config.set("rewards.rare.items.emerald.type", "EMERALD");
            config.set("rewards.rare.items.emerald.name", "Emerald");
            config.set("rewards.rare.items.emerald.amount", 12);
            config.set("rewards.rare.items.emerald.lore", lore);
            config.set("rewards.rare.items.emerald.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.rare.items.emerald.enchantments.unbreaking.amount", 1);
            config.set("rewards.epic.chance", 25);
            config.set("rewards.epic.commands", commands);
            config.set("rewards.epic.items.diamond.type", "DIAMOND");
            config.set("rewards.epic.items.diamond.name", "Diamond");
            config.set("rewards.epic.items.diamond.amount", 6);
            config.set("rewards.epic.items.diamond.lore", lore);
            config.set("rewards.epic.items.diamond.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.epic.items.diamond.enchantments.unbreaking.amount", 1);
            config.set("rewards.epic.items.emerald.type", "EMERALD");
            config.set("rewards.epic.items.emerald.name", "Emerald");
            config.set("rewards.epic.items.emerald.amount", 24);
            config.set("rewards.epic.items.emerald.lore", lore);
            config.set("rewards.epic.items.emerald.enchantments.unbreaking.type", "UNBREAKING");
            config.set("rewards.epic.items.emerald.enchantments.unbreaking.amount", 1);
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}