package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public record Portals(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile() {
        return new File(getDataFolder(), "portals.yml");
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean isEnable() {
        return getConfig().getBoolean("enable");
    }
    public String getWarp(World world, PlayerTeleportEvent.TeleportCause teleportCause) {
        if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            return getConfig().getString(world.getName() + ".END_PORTAL.warp");

        } else if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            return getConfig().getString(world.getName() + ".NETHER_PORTAL.warp");
        } else {
            return null;
        }
    }
    public void reload() {
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            config.set("enable", false);
            config.set("spawn.NETHER_PORTAL.warp", "survival");
            config.set("spawn.END_PORTAL.warp", "end");
            config.set("test.NETHER_PORTAL.warp", "survival");
            config.set("test.END_PORTAL.warp", "end");
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}