package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public record Jail(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public boolean exist() {
        return getFile().exists();
    }
    public File getFile() {
        return new File(getDataFolder(), "jail.yml");
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean locationExist() {
        return !getConfig().getKeys(false).isEmpty();
    }
    public void setLocation(Location location) {
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("world",location.getWorld().getName());
        config.set("x",location.getX());
        config.set("y",location.getY());
        config.set("z",location.getZ());
        config.set("yaw",location.getYaw());
        config.set("pitch",location.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public Location getLocation() {
        if (locationExist()) {
            String world = getConfig().getString("world");
            double x = getConfig().getDouble("x");
            double y = getConfig().getDouble("y");
            double z = getConfig().getDouble("z");
            float yaw = getConfig().getLong("yaw");
            float pitch = getConfig().getLong("pitch");
            return new Location(getServer().getWorld(world), x, y, z, yaw, pitch);
        } else {
            return null;
        }
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
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}