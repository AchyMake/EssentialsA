package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public record Worlds(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    private Message getMessage() {
        return plugin.getMessage();
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
    public void setupWorlds() {
        getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                File folder = new File(getDataFolder(), "worlds");
                if (folder.exists()) {
                    getMessage().sendLog(Level.INFO, "worlds folder detected");
                    getMessage().sendLog(Level.INFO, "tempting to create worlds");
                    if (folder.list().length > 0) {
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
                    }
                } else {
                    getMessage().sendLog(Level.INFO, "worlds folder undetected");
                    getMessage().sendLog(Level.INFO, "tempting to create files");
                    folder.mkdirs();
                    for (World world : getServer().getWorlds()) {
                        File file = new File(getDataFolder(), "worlds/" + world.getName() + ".yml");
                        if (!file.exists()) {
                            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                            config.addDefault("name", world.getName());
                            config.addDefault("display-name", world.getName());
                            config.addDefault("environment", world.getEnvironment().toString());
                            config.addDefault("seed", world.getSeed());
                            config.addDefault("pvp", true);
                            config.options().copyDefaults(true);
                            try {
                                config.save(file);
                                getMessage().sendLog(Level.INFO, "created " + world.getName() + ".yml");
                            } catch (IOException e) {
                                getMessage().sendLog(Level.WARNING, e.getMessage());
                            }
                        }
                    }
                }
            }
        },40);
    }
    public void create(String worldName, World.Environment environment) {
        File file = new File(getDataFolder(), "worlds/" + worldName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        WorldCreator worldCreator = new WorldCreator(worldName);
        config.addDefault("name", worldName);
        config.addDefault("display-name", worldName);
        config.addDefault("environment", worldCreator.environment().toString());
        config.addDefault("seed", worldCreator.seed());
        config.addDefault("pvp", true);
        config.options().copyDefaults(true);
        try {
            config.save(file);
            worldCreator.environment(environment);
            worldCreator.createWorld();
            getMessage().sendLog(Level.INFO, "created " + worldName + ".yml");
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void create(String worldName, World.Environment environment, long seed) {
        File file = new File(getDataFolder(), "worlds/" + worldName + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.seed(seed);
        config.addDefault("name", worldName);
        config.addDefault("display-name", worldName);
        config.addDefault("environment", worldCreator.environment().toString());
        config.addDefault("seed", seed);
        config.addDefault("pvp", true);
        config.options().copyDefaults(true);
        worldCreator.createWorld();
        try {
            config.save(file);
            worldCreator.environment(environment);
            worldCreator.createWorld();
            getMessage().sendLog(Level.INFO, "created " + worldName + ".yml");
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public File getFile(World world) {
        return new File("worlds/" + world.getName() + ".yml");
    }
    public boolean exists(World world) {
        return getFile(world).exists();
    }
    public FileConfiguration getConfig(World world) {
        return YamlConfiguration.loadConfiguration(getFile(world));
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
    public void reload() {
        File folder = new File(getDataFolder(), "worlds");
        if (folder.exists()) {
            for (File files : folder.listFiles()) {
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