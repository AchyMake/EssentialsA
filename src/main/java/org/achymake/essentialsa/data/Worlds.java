package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public record Worlds(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Chunk getChunk(World world, int v1, int v2) {
        return world.getChunkAt(v1, v2);
    }
    public Chunk getChunk(World world, long longValue) {
        return world.getChunkAt(longValue);
    }
    public boolean folderExist(String worldName) {
        return new File(getServer().getWorldContainer(), worldName).exists();
    }
    public boolean worldExist(String worldName) {
        return getServer().getWorld(worldName) != null;
    }
    public World getWorld(String world) {
        return getServer().getWorld(world);
    }
    public File getFile(String worldName) {
        return new File(getDataFolder(), "worlds/" + worldName + ".yml");
    }
    public File getFile(World world) {
        return getFile(world.getName());
    }
    public boolean exists(World world) {
        return getFile(world).exists();
    }
    public FileConfiguration getConfig(World world) {
        return YamlConfiguration.loadConfiguration(getFile(world));
    }
    public void setupWorlds() {
        getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                createFiles();
                getMessage().sendLog(Level.INFO, "worlds folder detected");
                getMessage().sendLog(Level.INFO, "tempting to create worlds");
                File folder = new File(getDataFolder(), "worlds");
                for (File files : folder.listFiles()) {
                    String worldName = files.getName().replace(".yml", "");
                    if (worldExist(worldName)) {
                        getMessage().sendLog(Level.INFO, worldName + " already exist");
                    } else {
                        if (folderExist(worldName)) {
                            getMessage().sendLog(Level.INFO, "creating " + worldName);
                            FileConfiguration config = YamlConfiguration.loadConfiguration(files);
                            WorldCreator worldCreator = new WorldCreator(worldName);
                            worldCreator.environment(World.Environment.valueOf(config.getString("environment")));
                            worldCreator.seed(config.getLong("seed"));
                            worldCreator.createWorld();
                            getMessage().sendLog(Level.INFO, worldName + " has been created with " + config.getString("environment") + " environment");
                        } else {
                            files.delete();
                            getMessage().sendLog(Level.WARNING, worldName + " does not exist " + files.getName() + " has been deleted");
                        }
                    }
                }
                plugin.getVillagers().setup();
            }
        },40);
    }
    private void createFiles() {
        for (World world : getServer().getWorlds()) {
            File file = getFile(world);
            if (file.exists())return;
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("name", world.getName());
            config.set("display-name", world.getName());
            config.set("environment", world.getEnvironment().toString());
            config.set("seed", world.getSeed());
            config.set("pvp", true);
            try {
                config.save(file);
                getMessage().sendLog(Level.INFO, "created " + world.getName() + ".yml");
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    public void create(String worldName, World.Environment environment) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(environment);
        worldCreator.createWorld();
    }
    public void create(String worldName, World.Environment environment, long seed) {
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.seed(seed);
        worldCreator.environment(environment);
        worldCreator.createWorld();
    }
    public void createFile(World world) {
        File file = getFile(world);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("name", world.getName());
        config.set("display-name", world.getName());
        config.set("environment", world.getEnvironment().toString());
        config.set("seed", world.getSeed());
        config.set("pvp", true);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public boolean isPVP(World world) {
        return getConfig(world).getBoolean("pvp");
    }
    public String getDisplayName(World world) {
        return getMessage().addColor(getConfig(world).getString("display-name"));
    }
    public void setSpawn(World world, Location location) {
        File file = getFile(world);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public Location getSpawn(World world) {
        if (getConfig(world).isConfigurationSection("spawn")) {
            double x = getConfig(world).getDouble("spawn.x");
            double y = getConfig(world).getDouble("spawn.y");
            double z = getConfig(world).getDouble("spawn.z");
            float yaw = getConfig(world).getLong("spawn.yaw");
            float pitch = getConfig(world).getLong("spawn.pitch");
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            return world.getSpawnLocation().add(0.5, 0.0, 0.5);
        }
    }
    public void setPVP(World world, boolean value) {
        File file = getFile(world);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("pvp", value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public boolean isPortalEnable() {
        return getConfig().getBoolean("portal.enable");
    }
    public void teleport(Player player, String portalType) {
        player.teleport(getSpawn(getWorld(getConfig().getString("portal." + player.getWorld().getName() + "." + portalType))));
    }
    public void reload() {
        File folder = new File(getDataFolder(), "worlds");
        if (folder.exists() | folder.isDirectory()) {
            for (File files : folder.listFiles()) {
                if (files.exists() | files.isFile()) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(files);
                    try {
                        config.load(files);
                    } catch (IOException | InvalidConfigurationException e) {
                        getMessage().sendLog(Level.WARNING, e.getMessage());
                    }
                }
            }
        }
    }
}