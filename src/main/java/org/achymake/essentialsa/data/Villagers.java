package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public record Villagers(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public File getFile(String path) {
        return new File(getDataFolder(), path);
    }
    public File getFile(Villager villager) {
        return getFile("villager/" + villager.getUniqueId() + ".yml");
    }
    public File getFile(Entity entity) {
        return getFile("villager/" + entity.getUniqueId() + ".yml");
    }
    public FileConfiguration getConfig(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }
    public FileConfiguration getConfig(Villager villager) {
        return YamlConfiguration.loadConfiguration(getFile(villager));
    }
    public FileConfiguration getConfig(Entity entity) {
        return YamlConfiguration.loadConfiguration(getFile(entity));
    }
    public boolean isNPC(Entity entity) {
        return getFile(entity).exists();
    }
    public void setCommandType(Entity entity, String commandType) {
        setString(entity, "command-type", commandType);
    }
    public void setCommand(Entity entity, String command) {
        setString(entity, "command", command);
    }
    public void setString(Entity entity, String path, String type) {
        File file = getFile(entity);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, type);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void setBoolean(Entity entity, String path, boolean value) {
        File file = getFile(entity);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void createVillager(Player player, String name) {
        Location location = player.getLocation();
        location.setPitch(0);
        Villager villager = (Villager) player.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setProfession(Villager.Profession.NONE);
        villager.setVillagerType(Villager.Type.PLAINS);
        villager.setCustomName(getMessage().addColor(name));
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);
        villager.setAI(false);
        getMessage().send(player, "&6You created&f " + name + "&6 villager");
        createVillagerFile(villager);
    }
    public boolean hasCommand(Entity entity) {
        return getConfig(entity).isString("command");
    }
    public String getCommand(Entity entity) {
        return getConfig(entity).getString("command");
    }
    public boolean isCommandConsole(Entity entity) {
        return getConfig(entity).getString("command-type").equalsIgnoreCase("console");
    }
    public boolean isCommandPlayer(Entity entity) {
        return getConfig(entity).getString("command-type").equalsIgnoreCase("player");
    }
    public Villager getSelected(Player player) {
        Entity entity = player.getTargetEntity(3);
        if (entity == null) {
            return null;
        } else {
            if (isNPC(entity)) {
                return (Villager) entity;
            } else {
                return null;
            }
        }
    }
    public void reload() {
        for (File files : getFile("villager").listFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(files);
            try {
                Entity entity = getServer().getEntity(UUID.fromString(files.getName().replace(".yml", "")));
                if (entity != null) {
                    entity.remove();
                }
                config.load(files);
                createVillager(files);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    public void disable() {
        for (File files : getFile("villager").listFiles()) {
            getServer().getEntity(UUID.fromString(files.getName().replace(".yml", ""))).remove();
        }
    }
    public void setup() {
        if (getFile("villager").exists() || getFile("villager").isDirectory()) {
            for (File files : getFile("villager").listFiles()) {
                createVillager(files);
            }
        }
    }
    private void createVillager(File file) {
        Entity entity = getServer().getEntity(UUID.fromString(file.getName().replace(".yml", "")));
        if (entity != null) {
            entity.remove();
        }
        String worldName = getConfig(file).getString("location.world");
        World world = getWorlds().getWorld(worldName);
        double x = getConfig(file).getDouble("location.x");
        double y = getConfig(file).getDouble("location.y");
        double z = getConfig(file).getDouble("location.z");
        float yaw = getConfig(file).getLong("location.yaw");
        float pitch = getConfig(file).getLong("location.pitch");
        String villagerName = getMessage().addColor(getConfig(file).getString("name"));
        String type = getConfig(file).getString("type");
        String profession = getConfig(file).getString("profession");
        String commandType = getConfig(file).getString("command-type");
        String command = getConfig(file).getString("command");
        boolean silent = getConfig(file).getBoolean("silent");
        boolean adult = getConfig(file).getBoolean("adult");
        Villager villager = (Villager) world.spawnEntity(new Location(world, x, y, z, yaw, pitch), EntityType.VILLAGER);
        villager.setCustomName(villagerName);
        villager.setVillagerType(Villager.Type.valueOf(type));
        villager.setProfession(Villager.Profession.valueOf(profession));
        villager.setSilent(silent);
        if (adult) {
            villager.setAdult();
        } else {
            villager.setBaby();
        }
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);
        villager.setAI(false);
        createVillagerFile(villager);
        setString(villager, "command-type", commandType);
        setString(villager, "command", command);
        file.delete();

    }
    public void createVillagerFile(Villager villager) {
        File file = getFile(villager);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("name", villager.getName());
        config.set("profession", villager.getProfession().name());
        config.set("type", villager.getVillagerType().name());
        config.set("silent", villager.isSilent());
        config.set("adult", villager.isAdult());
        config.set("command-type", "player");
        config.set("command", "help");
        Location location = villager.getLocation();
        config.set("location.world", location.getWorld().getName());
        config.set("location.x", location.getX());
        config.set("location.y", location.getY());
        config.set("location.z", location.getZ());
        config.set("location.yaw", location.getYaw());
        config.set("location.pitch", location.getPitch());
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
}