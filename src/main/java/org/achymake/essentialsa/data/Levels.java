package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public record Levels(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    public File getFile() {
        return new File(getDataFolder(), "levels.yml");
    }
    public boolean exists() {
        return getFile().exists();
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean isEnable() {
        return getConfig().getBoolean("enable");
    }
    public void effectUp(Player player) {
        if (getConfig().getBoolean("level-up.particle.enable")) {
            player.spawnParticle(Particle.valueOf(getConfig().getString("level-up.particle.type")), player.getLocation(), getConfig().getInt("level-up.particle.count"), getConfig().getDouble("level-up.particle.offsetX"), getConfig().getDouble("level-up.particle.offsetY"), getConfig().getDouble("level-up.particle.offsetZ"), 0.0);
        }
        if (getConfig().getBoolean("level-up.sound.enable")) {
            player.playSound(player, Sound.valueOf(getConfig().getString("level-up.sound.type")), (float) getConfig().getDouble("level-up.sound.volume"), (float) getConfig().getDouble("level-up.sound.pitch"));
        }
    }
    public void effectDown(Player player) {
        if (getConfig().getBoolean("level-down.particle.enable")) {
            player.spawnParticle(Particle.valueOf(getConfig().getString("level-down.particle.type")), player.getLocation(), getConfig().getInt("level-down.particle.count"), getConfig().getDouble("level-down.particle.offsetX"), getConfig().getDouble("level-down.particle.offsetY"), getConfig().getDouble("level-down.particle.offsetZ"), 0.0);
        }
        if (getConfig().getBoolean("level-down.sound.enable")) {
            player.playSound(player, Sound.valueOf(getConfig().getString("level-down.sound.type")), (float) getConfig().getDouble("level-down.sound.volume"), (float) getConfig().getDouble("level-down.sound.pitch"));
        }
    }
    public void levelChanged(Player player) {
        int level = player.getLevel();
        if (getConfig().isConfigurationSection("level." + level)) {
            if (getConfig().isDouble("level." + level + ".health")) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getConfig().getDouble("level." + level + ".health"));
            }
            if (getConfig().isDouble("level." + level + ".damage")) {
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(getConfig().getDouble("level." + level + ".damage"));
            }
        }
    }
    public void reload() {
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                plugin.getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            config.set("enable", true);
            config.set("level-up.particle.enable", true);
            config.set("level-up.particle.type", "TOTEM");
            config.set("level-up.particle.offsetX", 0.3);
            config.set("level-up.particle.offsetY", 0.7);
            config.set("level-up.particle.offsetZ", 0.3);
            config.set("level-up.particle.count", 25);
            config.set("level-up.sound.enable", true);
            config.set("level-up.sound.type", "ENTITY_VEX_AMBIENT");
            config.set("level-up.sound.volume", 2.5);
            config.set("level-up.sound.pitch", 1.0);
            config.set("level-down.particle.enable", true);
            config.set("level-down.particle.type", "SOUL");
            config.set("level-down.particle.offsetX", 0.3);
            config.set("level-down.particle.offsetY", 0.7);
            config.set("level-down.particle.offsetZ", 0.3);
            config.set("level-down.particle.count", 25);
            config.set("level-down.sound.enable", true);
            config.set("level-down.sound.type", "ENTITY_VEX_AMBIENT");
            config.set("level-down.sound.volume", 2.5);
            config.set("level-down.sound.pitch", 1.0);
            config.set("level.0.enable", true);
            config.set("level.0.health", 20.0);
            config.set("level.0.damage", 1.0);
            config.set("level.1.enable", true);
            config.set("level.1.health", 20.0);
            config.set("level.1.damage", 1.0);
            config.set("level.2.enable", true);
            config.set("level.2.health", 20.0);
            config.set("level.2.damage", 1.0);
            config.set("level.3.enable", true);
            config.set("level.3.health", 22.0);
            config.set("level.3.damage", 1.0);
            config.set("level.4.enable", true);
            config.set("level.4.health", 22.0);
            config.set("level.4.damage", 1.0);
            config.set("level.5.enable", true);
            config.set("level.5.health", 22.0);
            config.set("level.5.damage", 1.0);
            config.set("level.6.enable", true);
            config.set("level.6.health", 24.0);
            config.set("level.6.damage", 1.0);
            config.set("level.7.enable", true);
            config.set("level.7.health", 24.0);
            config.set("level.7.damage", 1.0);
            config.set("level.8.enable", true);
            config.set("level.8.health", 24.0);
            config.set("level.8.damage", 1.0);
            config.set("level.9.enable", true);
            config.set("level.9.health", 26.0);
            config.set("level.9.damage", 1.0);
            config.set("level.10.enable", true);
            config.set("level.10.health", 26.0);
            config.set("level.10.damage", 1.0);
            config.set("level.11.enable", true);
            config.set("level.11.health", 26.0);
            config.set("level.11.damage", 1.0);
            config.set("level.12.enable", true);
            config.set("level.12.health", 28.0);
            config.set("level.12.damage", 1.0);
            config.set("level.13.enable", true);
            config.set("level.13.health", 28.0);
            config.set("level.13.damage", 1.0);
            config.set("level.14.enable", true);
            config.set("level.14.health", 28.0);
            config.set("level.14.damage", 1.0);
            config.set("level.15.enable", true);
            config.set("level.15.health", 30.0);
            config.set("level.15.damage", 1.0);
            config.set("level.16.enable", true);
            config.set("level.16.health", 30.0);
            config.set("level.16.damage", 1.0);
            config.set("level.17.enable", true);
            config.set("level.17.health", 30.0);
            config.set("level.17.damage", 1.0);
            config.set("level.18.enable", true);
            config.set("level.18.health", 32.0);
            config.set("level.18.damage", 1.0);
            config.set("level.19.enable", true);
            config.set("level.19.health", 32.0);
            config.set("level.19.damage", 1.0);
            config.set("level.20.enable", true);
            config.set("level.20.health", 32.0);
            config.set("level.20.damage", 1.0);
            config.set("level.21.enable", true);
            config.set("level.21.health", 34.0);
            config.set("level.21.damage", 1.0);
            config.set("level.22.enable", true);
            config.set("level.22.health", 34.0);
            config.set("level.22.damage", 1.0);
            config.set("level.23.enable", true);
            config.set("level.23.health", 34.0);
            config.set("level.23.damage", 1.0);
            config.set("level.24.enable", true);
            config.set("level.24.health", 36.0);
            config.set("level.24.damage", 1.0);
            config.set("level.25.enable", true);
            config.set("level.25.health", 36.0);
            config.set("level.25.damage", 1.0);
            config.set("level.26.enable", true);
            config.set("level.26.health", 36.0);
            config.set("level.26.damage", 1.0);
            config.set("level.27.enable", true);
            config.set("level.27.health", 38.0);
            config.set("level.27.damage", 1.0);
            config.set("level.28.enable", true);
            config.set("level.28.health", 38.0);
            config.set("level.28.damage", 1.0);
            config.set("level.29.enable", true);
            config.set("level.29.health", 38.0);
            config.set("level.29.damage", 1.0);
            config.set("level.30.enable", true);
            config.set("level.30.health", 40.0);
            config.set("level.30.damage", 1.0);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}
