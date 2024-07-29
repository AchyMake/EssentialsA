package org.achymake.essentialsa.data;

import me.clip.placeholderapi.PlaceholderAPI;
import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.net.UpdateChecker;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public record Database(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private List<Player> getVanished() {
        return plugin.getVanished();
    }
    private HashMap<String, Long> getCommandCooldown() {
        return plugin.getCommandCooldown();
    }
    private UpdateChecker getUpdateChecker() {
        return plugin.getUpdateChecker();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    public PersistentDataContainer getData(Player player) {
        return player.getPersistentDataContainer();
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
            config.set("settings.auto-pick", true);
            config.set("settings.banned", false);
            config.set("settings.ban-reason", "");
            config.set("settings.frozen", false);
            config.set("settings.jailed", false);
            config.set("settings.muted", false);
            config.set("settings.pvp", true);
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
    public void teleport(Player player, String string, Location location) {
        if (hasTaskID(player, "teleport")) {
            getMessage().sendActionBar(player, "&cYou cannot teleport twice you have to wait");
        } else {
            location.getChunk().load();
            getMessage().sendActionBar(player, "&6Teleporting in&f " + getConfig().getInt("teleport.delay") + "&6 seconds");
            int taskID = plugin.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    getMessage().sendActionBar(player, "&6Teleporting to&f " + string);
                    player.teleport(location);
                    setString(player, "tasks.teleport", null);
                }
            },getConfig().getInt("teleport.delay") * 20L).getTaskId();
            addTaskID(player, "teleport", taskID);
        }
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
    public Block highestRandomBlock() {
        String worldName = getConfig().getString("commands.rtp.world");
        int x = new Random().nextInt(0, getConfig().getInt("commands.rtp.spread"));
        int z = new Random().nextInt(0, getConfig().getInt("commands.rtp.spread"));
        return getServer().getWorld(worldName).getHighestBlockAt(x, z);
    }
    public void randomTeleport(Player player) {
        Block block = highestRandomBlock();
        if (block.isLiquid()) {
            getMessage().sendActionBar(player, "&cFinding new location due to liquid block");
            randomTeleport(player);
        } else {
            block.getChunk().load();
            getMessage().sendActionBar(player, "&6Teleporting");
            player.teleport(block.getLocation().add(0.5,1,0.5));
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
    public ItemStack getItem(String type, int amount) {
        return new ItemStack(Material.valueOf(type.toUpperCase()), amount);
    }
    public ItemStack getOfflinePlayerHead(OfflinePlayer offlinePlayer, int amount) {
        if (offlinePlayer == null) {
            return getItem("player_head", amount);
        } else {
            ItemStack skullItem = getItem("player_head", amount);
            SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
            skullMeta.setOwningPlayer(offlinePlayer);
            skullItem.setItemMeta(skullMeta);
            return skullItem;
        }
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
    public void setBaby(Player player, boolean value) {
        if (value) {
            if (!isBaby(player)) {
                setScale(player, 0.5);
            }
        } else {
            if (isBaby(player)) {
                setScale(player, 1.0);
            }
        }
    }
    public double getScale(Player player) {
        return player.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
    }
    public void setScale(Player player, double value) {
        player.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
    }
    public void resetScale(Player player) {
        if (getScale(player) != 1) {
            setScale(player, 1);
        }
    }
    public Material getMaterial(String name) {
        return Material.valueOf(name.toUpperCase());
    }
    public void giveItems(Player player, Collection<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
                player.getInventory().addItem(itemStack);
            } else {
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
        }
    }
    public void giveItem(Player player, ItemStack itemStack) {
        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
    public boolean hasMoved(Location from, Location to) {
        if (from.getX() != to.getX()) {
            return true;
        } else if (from.getY() != to.getY()) {
            return true;
        } else return from.getZ() != to.getZ();
    }
    public ItemStack getSpawner(String entityType, int amount) {
        ItemStack spawner = getItem("spawner", amount);
        ItemMeta itemMeta = spawner.getItemMeta();
        itemMeta.getPersistentDataContainer().set(NamespacedKey.minecraft("entity"), PersistentDataType.STRING, entityType.toUpperCase());
        if (getConfig().isString("entities." + entityType.toUpperCase() + ".name")) {
            itemMeta.setDisplayName(getMessage().addColor("&dSpawner:&f " + getConfig().getString("entities." + entityType.toUpperCase() + ".name")));
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        spawner.setItemMeta(itemMeta);
        return spawner;
    }
    public void getUpdate(Player player) {
        getUpdateChecker().getUpdate(player);
    }
    public void sendJoinSound() {
        if (getConfig().getBoolean("connection.join.sound.enable")) {
            String soundType = getConfig().getString("connection.join.sound.type");
            float soundVolume = (float) getConfig().getDouble("connection.join.sound.volume");
            float soundPitch = (float) getConfig().getDouble("connection.join.sound.pitch");
            for (Player players : getServer().getOnlinePlayers()) {
                players.playSound(players, Sound.valueOf(soundType), soundVolume, soundPitch);
            }
        }
    }
    public void sendMotd(Player player, String motd) {
        getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (getConfig().isList("message-of-the-day." + motd)) {
                    for (String messages : getConfig().getStringList("message-of-the-day." + motd)) {
                        getMessage().send(player, messages.replaceAll("%player%", player.getName()));
                    }
                } else if (getConfig().isString("message-of-the-day." + motd)) {
                    getMessage().send(player, getConfig().getString("message-of-the-day." + motd).replaceAll("%player%", player.getName()));
                }
            }
        }, 3);
    }
    public void reload(OfflinePlayer[] offlinePlayers) {
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (exist(offlinePlayer)) {
                File file = getFile(offlinePlayer);
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                try {
                    config.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
            }
        }
    }
}