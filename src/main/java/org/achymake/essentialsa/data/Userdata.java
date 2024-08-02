package org.achymake.essentialsa.data;

import me.clip.placeholderapi.PlaceholderAPI;
import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public record Userdata(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private List<Player> getVanished() {
        return plugin.getVanished();
    }
    private HashMap<String, Long> getCommandCooldown() {
        return plugin.getCommandCooldown();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile(OfflinePlayer offlinePlayer) {
        return new File(getDataFolder(), "userdata/" + offlinePlayer.getUniqueId() + ".yml");
    }
    public boolean exist(OfflinePlayer offlinePlayer) {
        return getFile(offlinePlayer).exists();
    }
    public FileConfiguration getConfig(OfflinePlayer offlinePlayer) {
        return YamlConfiguration.loadConfiguration(getFile(offlinePlayer));
    }
    public PersistentDataContainer getData(Player player) {
        return player.getPersistentDataContainer();
    }
    public void setup(OfflinePlayer offlinePlayer) {
        if (exist(offlinePlayer)) {
            if (!getConfig(offlinePlayer).getString("name").equals(offlinePlayer.getName())) {
                File file = getFile(offlinePlayer);
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("name", offlinePlayer.getName());
                try {
                    config.save(file);
                } catch (IOException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
            }
        } else {
            File file = getFile(offlinePlayer);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("name", offlinePlayer.getName());
            config.set("display-name", offlinePlayer.getName());
            config.set("account", getConfig().getDouble("economy.starting-balance"));
            config.set("voted", 0);
            config.set("settings.auto-pick", false);
            config.set("settings.banned", false);
            config.set("settings.ban-reason", "");
            config.set("settings.frozen", false);
            config.set("settings.jailed", false);
            config.set("settings.muted", false);
            config.set("settings.pvp", true);
            config.set("settings.scale", 1.0);
            config.set("settings.vanished", false);
            config.createSection("homes");
            config.createSection("locations");
            config.createSection("chunks.members");
            config.createSection("chunks.banned");
            config.createSection("chunks.worlds");
            config.createSection("tasks");
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    public void setInt(OfflinePlayer offlinePlayer, String path, int value) {
        File file = getFile(offlinePlayer);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void setDouble(OfflinePlayer offlinePlayer, String path, double value) {
        File file = getFile(offlinePlayer);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void setFloat(OfflinePlayer offlinePlayer, String path, float value) {
        File file = getFile(offlinePlayer);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void setString(OfflinePlayer offlinePlayer, String path, String value) {
        File file = getFile(offlinePlayer);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void setStringList(OfflinePlayer offlinePlayer, String path, List<String> value) {
        File file = getFile(offlinePlayer);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void setBoolean(OfflinePlayer offlinePlayer, String path, boolean value) {
        File file = getFile(offlinePlayer);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void addTaskID(Player player, String task, int value) {
        setInt(player, "tasks." + task, value);
    }
    public boolean hasTaskID(Player player, String task) {
        return getConfig(player).isInt("tasks." + task);
    }
    public int getTaskID(Player player, String task) {
        return getConfig(player).getInt("tasks." + task);
    }
    public void removeTaskID(Player player, String task) {
        setString(player, "tasks." + task, null);
    }
    public boolean hasCooldown(Player player, String path) {
        if (getCommandCooldown().containsKey(path + "-" + player.getUniqueId())) {
            Long timeElapsed = System.currentTimeMillis() - getCommandCooldown().get(path + "-" + player.getUniqueId());
            String cooldownTimer = getConfig().getString("commands.cooldown." + path);
            Integer integer = Integer.valueOf(cooldownTimer.replace(cooldownTimer, cooldownTimer + "000"));
            return timeElapsed < integer;
        } else {
            return false;
        }
    }
    public void addCooldown(Player player, String path) {
        if (getCommandCooldown().containsKey(path + "-" + player.getUniqueId())) {
            Long timeElapsed = System.currentTimeMillis() - getCommandCooldown().get(path + "-" + player.getUniqueId());
            String cooldownTimer = getConfig().getString("commands.cooldown." + path);
            Integer integer = Integer.valueOf(cooldownTimer.replace(cooldownTimer, cooldownTimer + "000"));
            if (timeElapsed > integer) {
                getCommandCooldown().put(path + "-" + player.getUniqueId(), System.currentTimeMillis());
            }
        } else {
            getCommandCooldown().put(path + "-" + player.getUniqueId(), System.currentTimeMillis());
        }
    }
    public String getCooldown(Player player, String path) {
        if (getCommandCooldown().containsKey(path + "-" + player.getUniqueId())) {
            Long timeElapsed = System.currentTimeMillis() - getCommandCooldown().get(path + "-" + player.getUniqueId());
            String cooldownTimer = getConfig().getString("commands.cooldown." + path);
            Integer integer = Integer.valueOf(cooldownTimer.replace(cooldownTimer, cooldownTimer + "000"));
            if (timeElapsed < integer) {
                long timer = (integer-timeElapsed);
                return String.valueOf(timer).substring(0, String.valueOf(timer).length() - 3);
            }
        } else {
            return "0";
        }
        return "0";
    }
    public boolean homeExist(OfflinePlayer offlinePlayer, String homeName) {
        return getConfig(offlinePlayer).getConfigurationSection("homes").contains(homeName);
    }
    public List<String> getHomes(OfflinePlayer offlinePlayer) {
        return new ArrayList<>(getConfig(offlinePlayer).getConfigurationSection("homes").getKeys(false));
    }
    public boolean setHome(Player player, String homeName) {
        if (homeExist(player, homeName)) {
            setString(player, "homes." + homeName + ".world", player.getWorld().getName());
            setDouble(player, "homes." + homeName + ".x", player.getLocation().getX());
            setDouble(player, "homes." + homeName + ".y", player.getLocation().getY());
            setDouble(player, "homes." + homeName + ".z", player.getLocation().getZ());
            setFloat(player, "homes." + homeName + ".yaw", player.getLocation().getYaw());
            setFloat(player, "homes." + homeName + ".pitch", player.getLocation().getPitch());
            return true;
        } else {
            for (String rank : getConfig().getConfigurationSection("homes").getKeys(false)) {
                if (player.hasPermission("players.command.sethome.multiple." + rank)) {
                    if (getConfig().getInt("homes." + rank) > getHomes(player).size()) {
                        setString(player, "homes." + homeName + ".world", player.getWorld().getName());
                        setDouble(player, "homes." + homeName + ".x", player.getLocation().getX());
                        setDouble(player, "homes." + homeName + ".y", player.getLocation().getY());
                        setDouble(player, "homes." + homeName + ".z", player.getLocation().getZ());
                        setFloat(player, "homes." + homeName + ".yaw", player.getLocation().getYaw());
                        setFloat(player, "homes." + homeName + ".pitch", player.getLocation().getPitch());
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    public Location getHome(OfflinePlayer offlinePlayer, String homeName) {
        String worldName = getConfig(offlinePlayer).getString("homes." + homeName + ".world");
        double x = getConfig(offlinePlayer).getDouble("homes." + homeName + ".x");
        double y = getConfig(offlinePlayer).getDouble("homes." + homeName + ".y");
        double z = getConfig(offlinePlayer).getDouble("homes." + homeName + ".z");
        float yaw = getConfig(offlinePlayer).getLong("homes." + homeName + ".yaw");
        float pitch = getConfig(offlinePlayer).getLong("homes." + homeName + ".pitch");
        return new Location(getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }
    public boolean locationExist(OfflinePlayer offlinePlayer, String locationName) {
        return getConfig(offlinePlayer).getConfigurationSection("locations").contains(locationName);
    }
    public void setLocation(Player player, String locationName) {
        setString(player, "locations." + locationName + ".world", player.getWorld().getName());
        setDouble(player, "locations." + locationName + ".x", player.getLocation().getX());
        setDouble(player, "locations." + locationName + ".y", player.getLocation().getY());
        setDouble(player, "locations." + locationName + ".z", player.getLocation().getZ());
        setFloat(player, "locations." + locationName + ".yaw", player.getLocation().getYaw());
        setFloat(player, "locations." + locationName + ".pitch", player.getLocation().getPitch());
    }
    public void setLocation(OfflinePlayer offlinePlayer, String locationName, Location location) {
        setString(offlinePlayer, "locations." + locationName + ".world", location.getWorld().getName());
        setDouble(offlinePlayer, "locations." + locationName + ".x", location.getX());
        setDouble(offlinePlayer, "locations." + locationName + ".y", location.getY());
        setDouble(offlinePlayer, "locations." + locationName + ".z", location.getZ());
        setFloat(offlinePlayer, "locations." + locationName + ".yaw", location.getYaw());
        setFloat(offlinePlayer, "locations." + locationName + ".pitch", location.getPitch());
    }
    public Location getLocation(OfflinePlayer offlinePlayer, String locationName) {
        String worldName = getConfig(offlinePlayer).getString("locations." + locationName + ".world");
        double x = getConfig(offlinePlayer).getDouble("locations." + locationName + ".x");
        double y = getConfig(offlinePlayer).getDouble("locations." + locationName + ".y");
        double z = getConfig(offlinePlayer).getDouble("locations." + locationName + ".z");
        float yaw = getConfig(offlinePlayer).getLong("locations." + locationName + ".yaw");
        float pitch = getConfig(offlinePlayer).getLong("locations." + locationName + ".pitch");
        return new Location(getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }
    public void hideVanished(Player player) {
        for (Player vanished : getVanished()) {
            player.hidePlayer(plugin, vanished);
        }
    }
    public void setVanish(OfflinePlayer offlinePlayer, boolean value) {
        if (value) {
            setBoolean(offlinePlayer,"settings.vanished", true);
            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                getVanished().add(player);
                if (getConfig(player).getBoolean("settings.coordinates")) {
                    setBoolean(player, "settings.coordinates", false);
                }
                for (Player onlinePlayers : getServer().getOnlinePlayers()) {
                    onlinePlayers.hidePlayer(plugin, player);
                }
                player.setAllowFlight(true);
                player.setInvulnerable(true);
                player.setSleepingIgnored(true);
                player.setCollidable(false);
                player.setSilent(true);
                player.setCanPickupItems(false);
                for (Player vanished : getVanished()) {
                    vanished.showPlayer(plugin, player);
                    player.showPlayer(plugin, vanished);
                }
                addVanishTask(player);
                getMessage().sendActionBar(player, "&6&lVanish:&a Enabled");
            }
        } else {
            setBoolean(offlinePlayer,"settings.vanished", false);
            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                getVanished().remove(player);
                for (Player players : getServer().getOnlinePlayers()) {
                    players.showPlayer(plugin, player);
                }
                if (!player.hasPermission("essentials.command.fly")) {
                    player.setAllowFlight(false);
                }
                player.setInvulnerable(false);
                player.setSleepingIgnored(false);
                player.setCollidable(true);
                player.setSilent(false);
                player.setCanPickupItems(true);
                for (Player vanished : getVanished()) {
                    player.hidePlayer(plugin, vanished);
                }
                plugin.getScheduler().cancelTask(getTaskID(player, "vanish"));
                removeTaskID(player, "vanish");
                getMessage().sendActionBar(player, "&6&lVanish:&c Disabled");
            }
        }
    }
    private void addVanishTask(Player player) {
        int taskID = plugin.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                getMessage().sendActionBar(player, "&6&lVanish:&a Enabled");
                removeTaskID(player, "vanish");
                addVanishTask(player);
            }
        }, 50).getTaskId();
        addTaskID(player, "vanish", taskID);
    }
    public String prefix(Player player) {
        if (PlaceholderAPI.isRegistered("vault")) {
            return getMessage().addColor(PlaceholderAPI.setPlaceholders(player, "%vault_prefix%"));
        } else {
            return "";
        }
    }
    public String getDisplayName(Player player) {
        return getMessage().addColor(getConfig(player).getString("display-name"));
    }
    public String suffix(Player player) {
        if (PlaceholderAPI.isRegistered("vault")) {
            return getMessage().addColor(PlaceholderAPI.setPlaceholders(player, "%vault_suffix%"));
        } else {
            return "";
        }
    }
    public boolean hasJoined(OfflinePlayer offlinePlayer) {
        if (exist(offlinePlayer)) {
            return getConfig(offlinePlayer).isConfigurationSection("locations.quit");
        } else {
            return false;
        }
    }
    public boolean isPVP(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.pvp");
    }
    public boolean isMuted(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.muted");
    }
    public boolean isFrozen(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.frozen");
    }
    public boolean isJailed(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.jailed");
    }
    public boolean isVanished(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.vanished");
    }
    public boolean isBanned(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.banned");
    }
    public boolean isAutoPick(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getBoolean("settings.auto-pick");
    }
    public boolean isBaby(Player player) {
        return player.getAttribute(Attribute.GENERIC_SCALE).getValue() == 0.5;
    }
    public String getBanReason(OfflinePlayer offlinePlayer) {
        return getConfig(offlinePlayer).getString("settings.ban-reason");
    }
    public boolean isDisabled(OfflinePlayer offlinePlayer) {
        return isFrozen(offlinePlayer) || isJailed(offlinePlayer);
    }
    public void setAutoPick(OfflinePlayer offlinePlayer, boolean value) {
        setBoolean(offlinePlayer, "settings.auto-pick", value);
    }
    public void toggleAutoPick(OfflinePlayer offlinePlayer) {
        setAutoPick(offlinePlayer, !isAutoPick(offlinePlayer));
    }
    public void toggleBaby(Player player) {
        setBaby(player, !isBaby(player));
    }
    public void setBaby(Player player, boolean baby) {
        if (baby) {
            if (!isBaby(player)) {
                setDouble(player, "settings.scale", 0.5);
                setScale(player, 0.5);
            }
        } else {
            if (isBaby(player)) {
                setDouble(player, "settings.scale", 1.0);
                setScale(player, 1.0);
            }
        }
    }
    public double getScale(Player player) {
        return player.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
    }
    public void setScale(Player player, double scale) {
        player.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(scale);
    }
    public void resetScale(Player player, boolean isCommand) {
        if (getScale(player) != 1) {
            setScale(player, 1);
        }
    }
    public void reload() {
        File folder = new File(getDataFolder(), "userdata");
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