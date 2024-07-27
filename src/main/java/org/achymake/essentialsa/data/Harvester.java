package org.achymake.essentialsa.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;

public record Harvester(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile() {
        return new File(getDataFolder(), "harvester.yml");
    }
    public boolean exists() {
        return getFile().exists();
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean isAllowHarvest(Block block) {
        try {
            BlockVector3 pt1 = BlockVector3.at(block.getX(), block.getY(), block.getZ());
            BlockVector3 pt2 = BlockVector3.at(block.getX(), block.getY(), block.getZ());
            ProtectedCuboidRegion region = new ProtectedCuboidRegion("_", pt1, pt2);
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
            if (regionManager != null) {
                for (ProtectedRegion regionIn : regionManager.getApplicableRegions(region)) {
                    StateFlag.State flag = regionIn.getFlag(plugin.getFlagHarvest());
                    if (flag == StateFlag.State.ALLOW) {
                        return true;
                    } else if (flag == StateFlag.State.DENY) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
            return false;
        }
    }
    public void harvest(Player player, Block block) {
        if (isWoodenHoe(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("essentials.harvest.wooden_hoe"))return;
            if (isEnable(block)) {
                if (!isRightAge(block))return;
                playHarvestSound(player, block);
                resetAge(block);
                player.swingMainHand();
                addDamage(player.getInventory().getItemInMainHand(), getDamage(block));
                dropItems(player, block);
                dropExperience(player, block);
                isDestroyed(player, player.getInventory().getItemInMainHand());
            }
        } else if (isStoneHoe(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("essentials.harvest.stone_hoe"))return;
            if (isEnable(block)) {
                if (!isRightAge(block))return;
                playHarvestSound(player, block);
                resetAge(block);
                player.swingMainHand();
                addDamage(player.getInventory().getItemInMainHand(), getDamage(block));
                dropItems(player, block);
                dropExperience(player, block);
                isDestroyed(player, player.getInventory().getItemInMainHand());
            }
        } else if (isIronHoe(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("essentials.harvest.iron_hoe"))return;
            if (isEnable(block)) {
                if (!isRightAge(block))return;
                playHarvestSound(player, block);
                resetAge(block);
                player.swingMainHand();
                addDamage(player.getInventory().getItemInMainHand(), getDamage(block));
                dropItems(player, block);
                dropExperience(player, block);
                isDestroyed(player, player.getInventory().getItemInMainHand());
            }
        } else if (isGoldenHoe(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("essentials.harvest.golden_hoe"))return;
            if (isEnable(block)) {
                if (!isRightAge(block))return;
                playHarvestSound(player, block);
                resetAge(block);
                player.swingMainHand();
                addDamage(player.getInventory().getItemInMainHand(), getDamage(block));
                dropItems(player, block);
                dropExperience(player, block);
                isDestroyed(player, player.getInventory().getItemInMainHand());
            }
        } else if (isDiamondHoe(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("essentials.harvest.diamond_hoe"))return;
            if (isEnable(block)) {
                if (!isRightAge(block))return;
                playHarvestSound(player, block);
                player.swingMainHand();
                addDamage(player.getInventory().getItemInMainHand(), getDamage(block));
                dropItems(player, block);
                resetAge(block);
                dropExperience(player, block);
                isDestroyed(player, player.getInventory().getItemInMainHand());
            }
        } else if (isNetheriteHoe(player.getInventory().getItemInMainHand())) {
            if (!player.hasPermission("essentials.harvest.netherite_hoe"))return;
            if (isEnable(block)) {
                if (!isRightAge(block))return;
                playHarvestSound(player, block);
                resetAge(block);
                player.swingMainHand();
                addDamage(player.getInventory().getItemInMainHand(), getDamage(block));
                dropItems(player, block);
                dropExperience(player, block);
                isDestroyed(player, player.getInventory().getItemInMainHand());
            }
        }
    }
    public boolean isHoe(ItemStack itemStack) {
        return isWoodenHoe(itemStack) || isStoneHoe(itemStack) || isIronHoe(itemStack) || isGoldenHoe(itemStack) || isDiamondHoe(itemStack) || isNetheriteHoe(itemStack);
    }
    public boolean isWoodenHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.WOODEN_HOE);
    }
    public boolean isStoneHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.STONE_HOE);
    }
    public boolean isIronHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.IRON_HOE);
    }
    public boolean isGoldenHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.GOLDEN_HOE);
    }
    public boolean isDiamondHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.DIAMOND_HOE);
    }
    public boolean isNetheriteHoe(ItemStack itemStack) {
        return itemStack.getType().equals(Material.NETHERITE_HOE);
    }
    public void addDamage(ItemStack itemStack, int damage) {
        if (itemStack.containsEnchantment(Enchantment.UNBREAKING)) {
            int durability = itemStack.getItemMeta().getEnchantLevel(Enchantment.UNBREAKING);
            Damageable toolHealthDamage = (Damageable) itemStack.getItemMeta();
            int result = toolHealthDamage.getDamage() + damage + getConfig().getInt("max-durability");
            toolHealthDamage.setDamage(result - durability);
            itemStack.setItemMeta(toolHealthDamage);
        } else {
            Damageable toolHealthDamage = (Damageable) itemStack.getItemMeta();
            int result = toolHealthDamage.getDamage() + damage + getConfig().getInt("max-durability");
            toolHealthDamage.setDamage(result);
            itemStack.setItemMeta(toolHealthDamage);
        }
    }
    public void isDestroyed(Player player, ItemStack itemStack) {
        if (isWoodenHoe(itemStack)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 59) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (isStoneHoe(itemStack)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 131) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (isIronHoe(itemStack)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 250) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (isGoldenHoe(itemStack)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 32) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (isDiamondHoe(itemStack)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 1561) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        } else if (isNetheriteHoe(itemStack)) {
            Damageable toolHealthAfter = (Damageable) itemStack.getItemMeta();
            if (toolHealthAfter.getDamage() >= 2031) {
                itemStack.setAmount(0);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            }
        }
    }
    public boolean isEnable(Block block) {
        return getConfig().getBoolean("crops." + block.getType() + ".enable");
    }
    public boolean isRightAge(Block block) {
        return ((Ageable) block.getBlockData()).getAge() == getConfig().getInt("crops." + block.getType() + ".max-age");
    }
    public int getDamage(Block block) {
        return getConfig().getInt("crops." + block.getType() + ".damage");
    }
    public void playHarvestSound(Player player, Block block) {
        player.playSound(block.getLocation().add(0.5, 0.3, 0.5), Sound.ITEM_SHOVEL_FLATTEN, 1.0F, 1.0F);
    }
    public void resetAge(Block block) {
        BlockData blockData = block.getBlockData();
        ((Ageable) blockData).setAge(0);
        block.setBlockData(blockData);
    }
    public void dropExperience(Player player, Block block) {
        if (getConfig().getBoolean("crops." + block.getType() + ".experience.enable")) {
            if (new Random().nextInt(100) < getConfig().getInt("crops." + block.getType() + ".experience.chance")) {
                Location location = block.getLocation().add(0.5, 0.3, 0.5);
                ExperienceOrb experience = (ExperienceOrb) player.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
                experience.setExperience(getConfig().getInt("crops." + block.getType() + ".experience.amount"));
                if (getConfig().getBoolean("crops." + block.getType() + ".sound.enable")) {
                    String soundType = getConfig().getString("crops." + block.getType() + ".sound.type");
                    float volume = (float) getConfig().getDouble("crops." + block.getType() + ".sound.volume");
                    float pitch = (float) getConfig().getDouble("crops." + block.getType() + ".sound.pitch");
                    player.playSound(location, Sound.valueOf(soundType), volume, pitch);
                }
                if (getConfig().getBoolean("crops." + block.getType() + ".particle.enable")) {
                    String particleType = getConfig().getString("crops." + block.getType() + ".particle.type");
                    int count = getConfig().getInt("crops." + block.getType() + ".particle.count");
                    double offsetX = getConfig().getDouble("crops." + block.getType() + ".particle.offsetX");
                    double offsetY = getConfig().getDouble("crops." + block.getType() + ".particle.offsetY");
                    double offsetZ = getConfig().getDouble("crops." + block.getType() + ".particle.offsetZ");
                    player.spawnParticle(Particle.valueOf(particleType), location, count, offsetX, offsetY, offsetZ, 0.0);
                }
            }
        }
    }
    public void dropItems(Player player, Block block) {
        if (getConfig().getInt("crops." + block.getType() + ".drops.amount.max") == 1) {
            int amount = getConfig().getInt("crops." + block.getType() + ".drops.amount.max");
            ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("crops." + block.getType() + ".drops.item")), amount);
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.FORTUNE)) {
                int extra = new Random().nextInt(0, player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FORTUNE));
                if (new Random().nextInt(100) >= 70) {
                    itemStack.setAmount(itemStack.getAmount() + extra);
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                } else {
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                }
            } else {
                player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
            }
        } else {
            int amount = new Random().nextInt(getConfig().getInt("crops." + block.getType() + ".drops.amount.min"), getConfig().getInt("crops." + block.getType() + ".drops.amount.max"));
            ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("crops." + block.getType() + ".drops.item")), amount);
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.FORTUNE)) {
                int extra = new Random().nextInt(0, player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FORTUNE));
                if (new Random().nextInt(100) >= 70) {
                    itemStack.setAmount(itemStack.getAmount() + extra);
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                } else {
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
                }
            } else {
                player.getWorld().dropItem(block.getLocation().add(0.5,0.3,0.5), itemStack);
            }
        }
    }
    public void reload() {
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (exists()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            config.set("enable", true);
            config.set("max-durability", 3);
            config.set("crops.CARROTS.enable", true);
            config.set("crops.CARROTS.max-age", 7);
            config.set("crops.CARROTS.damage", 1);
            config.set("crops.CARROTS.drops.item", "CARROT");
            config.set("crops.CARROTS.drops.amount.min", 2);
            config.set("crops.CARROTS.drops.amount.max", 5);
            config.set("crops.CARROTS.experience.enable", true);
            config.set("crops.CARROTS.experience.chance", 30);
            config.set("crops.CARROTS.experience.amount", 1);
            config.set("crops.CARROTS.particle.enable", true);
            config.set("crops.CARROTS.particle.type", "TOTEM");
            config.set("crops.CARROTS.particle.offsetX", 0.3);
            config.set("crops.CARROTS.particle.offsetY", 0.3);
            config.set("crops.CARROTS.particle.offsetZ", 0.3);
            config.set("crops.CARROTS.particle.count", 25);
            config.set("crops.CARROTS.sound.enable", true);
            config.set("crops.CARROTS.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("crops.CARROTS.sound.volume", 0.75);
            config.set("crops.CARROTS.sound.pitch", 1.00);
            config.set("crops.POTATOES.enable", true);
            config.set("crops.POTATOES.max-age", 7);
            config.set("crops.POTATOES.damage", 1);
            config.set("crops.POTATOES.drops.item", "POTATO");
            config.set("crops.POTATOES.drops.amount.min", 2);
            config.set("crops.POTATOES.drops.amount.max", 5);
            config.set("crops.POTATOES.experience.enable", true);
            config.set("crops.POTATOES.experience.chance", 30);
            config.set("crops.POTATOES.experience.amount", 1);
            config.set("crops.POTATOES.particle.enable", true);
            config.set("crops.POTATOES.particle.type", "TOTEM");
            config.set("crops.POTATOES.particle.offsetX", 0.3);
            config.set("crops.POTATOES.particle.offsetY", 0.3);
            config.set("crops.POTATOES.particle.offsetZ", 0.3);
            config.set("crops.POTATOES.particle.count", 25);
            config.set("crops.POTATOES.sound.enable", true);
            config.set("crops.POTATOES.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("crops.POTATOES.sound.volume", 0.75);
            config.set("crops.POTATOES.sound.pitch", 1.00);
            config.set("crops.WHEAT.enable", true);
            config.set("crops.WHEAT.max-age", 7);
            config.set("crops.WHEAT.damage", 1);
            config.set("crops.WHEAT.drops.item", "WHEAT");
            config.set("crops.WHEAT.drops.amount.min", 1);
            config.set("crops.WHEAT.drops.amount.max", 1);
            config.set("crops.WHEAT.experience.enable", true);
            config.set("crops.WHEAT.experience.chance", 30);
            config.set("crops.WHEAT.experience.amount", 1);
            config.set("crops.WHEAT.particle.enable", true);
            config.set("crops.WHEAT.particle.type", "TOTEM");
            config.set("crops.WHEAT.particle.offsetX", 0.3);
            config.set("crops.WHEAT.particle.offsetY", 0.3);
            config.set("crops.WHEAT.particle.offsetZ", 0.3);
            config.set("crops.WHEAT.particle.count", 25);
            config.set("crops.WHEAT.sound.enable", true);
            config.set("crops.WHEAT.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("crops.WHEAT.sound.volume", 0.75);
            config.set("crops.WHEAT.sound.pitch", 1.00);
            config.set("crops.BEETROOTS.enable", true);
            config.set("crops.BEETROOTS.max-age", 7);
            config.set("crops.BEETROOTS.damage", 1);
            config.set("crops.BEETROOTS.drops.item", "BEETROOT");
            config.set("crops.BEETROOTS.drops.amount.min", 1);
            config.set("crops.BEETROOTS.drops.amount.max", 1);
            config.set("crops.BEETROOTS.experience.enable", true);
            config.set("crops.BEETROOTS.experience.chance", 30);
            config.set("crops.BEETROOTS.experience.amount", 1);
            config.set("crops.BEETROOTS.particle.enable", true);
            config.set("crops.BEETROOTS.particle.type", "TOTEM");
            config.set("crops.BEETROOTS.particle.offsetX", 0.3);
            config.set("crops.BEETROOTS.particle.offsetY", 0.3);
            config.set("crops.BEETROOTS.particle.offsetZ", 0.3);
            config.set("crops.BEETROOTS.particle.count", 25);
            config.set("crops.BEETROOTS.sound.enable", true);
            config.set("crops.BEETROOTS.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("crops.BEETROOTS.sound.volume", 0.75);
            config.set("crops.BEETROOTS.sound.pitch", 1.00);
            config.set("crops.COCOA.enable", true);
            config.set("crops.COCOA.max-age", 7);
            config.set("crops.COCOA.damage", 1);
            config.set("crops.COCOA.drops.item", "COCOA_BEAN");
            config.set("crops.COCOA.drops.amount.min", 2);
            config.set("crops.COCOA.drops.amount.max", 3);
            config.set("crops.COCOA.experience.enable", true);
            config.set("crops.COCOA.experience.chance", 30);
            config.set("crops.COCOA.experience.amount", 1);
            config.set("crops.COCOA.particle.enable", true);
            config.set("crops.COCOA.particle.type", "TOTEM");
            config.set("crops.COCOA.particle.offsetX", 0.3);
            config.set("crops.COCOA.particle.offsetY", 0.3);
            config.set("crops.COCOA.particle.offsetZ", 0.3);
            config.set("crops.COCOA.particle.count", 25);
            config.set("crops.COCOA.sound.enable", true);
            config.set("crops.COCOA.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("crops.COCOA.sound.volume", 0.75);
            config.set("crops.COCOA.sound.pitch", 1.00);
            config.set("crops.NETHER_WART.enable", true);
            config.set("crops.NETHER_WART.max-age", 7);
            config.set("crops.NETHER_WART.damage", 1);
            config.set("crops.NETHER_WART.drops.item", "NETHER_WART");
            config.set("crops.NETHER_WART.drops.amount.min", 2);
            config.set("crops.NETHER_WART.drops.amount.max", 4);
            config.set("crops.NETHER_WART.experience.enable", true);
            config.set("crops.NETHER_WART.experience.chance", 30);
            config.set("crops.NETHER_WART.experience.amount", 1);
            config.set("crops.NETHER_WART.particle.enable", true);
            config.set("crops.NETHER_WART.particle.type", "TOTEM");
            config.set("crops.NETHER_WART.particle.offsetX", 0.3);
            config.set("crops.NETHER_WART.particle.offsetY", 0.3);
            config.set("crops.NETHER_WART.particle.offsetZ", 0.3);
            config.set("crops.NETHER_WART.particle.count", 25);
            config.set("crops.NETHER_WART.sound.enable", true);
            config.set("crops.NETHER_WART.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("crops.NETHER_WART.sound.volume", 0.75);
            config.set("crops.NETHER_WART.sound.pitch", 1.00);
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}