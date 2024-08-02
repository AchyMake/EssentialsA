package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerTeleportEvent;
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
                createFiles();
                File folder = new File(getDataFolder(), "worlds");
                getMessage().sendLog(Level.INFO, "worlds folder detected");
                getMessage().sendLog(Level.INFO, "tempting to create worlds");
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
            File file = new File(getDataFolder(), "worlds/" + world.getName() + ".yml");
            if (file.exists()) {
                getMessage().sendLog(Level.INFO, world.getName() + " already exist");
            } else {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("name", world.getName());
                config.set("display-name", world.getName());
                config.set("environment", world.getEnvironment().toString());
                config.set("seed", world.getSeed());
                config.set("pvp", true);
                config.set("portal.enable", false);
                config.set("portal.NETHER_PORTAL.warp", "spawn");
                config.set("portal.END_PORTAL.warp", "end");
                try {
                    config.save(file);
                    getMessage().sendLog(Level.INFO, "created " + world.getName() + ".yml");
                } catch (IOException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
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
        config.set("portal.enable", false);
        config.set("portal.NETHER_PORTAL.warp", "spawn");
        config.set("portal.END_PORTAL.warp", "end");
        try {
            config.save(file);
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
    public boolean isPortalEnable(World world) {
        return getConfig(world).getBoolean("portal.enable");
    }
    public String getWarp(World world, PlayerTeleportEvent.TeleportCause teleportCause) {
        if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            return getConfig(world).getString("portal.END_PORTAL.warp");

        } else if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            return getConfig(world).getString("portal.NETHER_PORTAL.warp");
        } else {
            return null;
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