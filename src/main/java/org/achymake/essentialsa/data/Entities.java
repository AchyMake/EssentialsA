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
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public record Entities(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile(String path) {
        return new File(getDataFolder(), path);
    }
    public File getFile(EntityType entityType) {
        return getFile("entity/" + entityType + ".yml");
    }
    public File getFile(Entity entity) {
        return getFile("entity/" + entity.getType() + ".yml");
    }
    public boolean exist(Entity entity) {
        return getFile(entity).exists();
    }
    public FileConfiguration getConfig(Entity entity) {
        return YamlConfiguration.loadConfiguration(getFile(entity));
    }
    public boolean isEnable(Entity entity) {
        return getFile(entity).exists();
    }
    public String getName(Entity entity) {
        return getConfig(entity).getString("name");
    }
    public int getChunkLimit(Entity entity) {
        return getConfig(entity).getInt("chunk-limit");
    }
    public boolean disableSpawn(Entity entity) {
        return getConfig(entity).getBoolean("disable-spawn");
    }
    public boolean disableBlockForm(Entity entity) {
        return getConfig(entity).getBoolean("disable-block-form");
    }
    public boolean disableBlockDamage(Entity entity) {
        return getConfig(entity).getBoolean("disable-block-damage");
    }
    public boolean disableBlockChange(Entity entity) {
        return getConfig(entity).getBoolean("disable-block-change");
    }
    public boolean disableBlockInteract(Entity entity, Block block) {
        return getConfig(entity).getBoolean("disable-block-interact." + block.getType());
    }
    public boolean disableTarget(Entity entity, Entity target) {
        return getConfig(entity).getBoolean("disable-target." + target.getType());
    }
    public boolean disableDamage(Entity damager, Entity entity) {
        return getConfig(damager).getBoolean("disable-damage." + entity.getType());
    }
    public boolean isHostile(Entity entity) {
        return getConfig(entity).getBoolean("hostile");
    }
    public void reload() {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.equals(EntityType.PLAYER))return;
            File file = getFile(entityType);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (file.exists()) {
                try {
                    config.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
            } else {
                config.set("name", entityType.getName());
                config.set("hostile", false);
                config.set("chunk-limit", -1);
                config.set("disable-spawn", false);
                config.set("disable-block-damage", false);
                config.set("disable-block-form", false);
                config.set("disable-block-change", false);
                config.set("disable-block-interact.FARMLAND", true);
                config.set("disable-block-interact.TURTLE_EGG", true);
                config.set("disable-block-interact.SNIFFER_EGG", true);
                config.set("disable-target.VILLAGER", false);
                config.set("disable-damage.VILLAGER", false);
                config.set("disable-damage.ITEM", true);
                config.set("carry.enable", false);
                config.set("carry.weight.adult", 1);
                config.set("carry.weight.baby", 0);
                try {
                    config.save(file);
                } catch (IOException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
            }
        }
    }
}