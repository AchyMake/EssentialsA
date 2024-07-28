package org.achymake.essentialsa.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.achymake.essentialsa.EssentialsA;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public record Chunkdata(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public File getFile(Chunk chunk) {
        return new File(getDataFolder(), "chunks/" + chunk.getWorld().getName() + "/" + chunk.getChunkKey() + ".yml");
    }
    public boolean exist(Chunk chunk) {
        return getFile(chunk).exists();
    }
    public FileConfiguration getConfig(Chunk chunk) {
        return YamlConfiguration.loadConfiguration(getFile(chunk));
    }
    public boolean isAllowedClaim(Chunk chunk) {
        try {
            int bx = chunk.getX() << 4;
            int bz = chunk.getZ() << 4;
            BlockVector3 pt1 = BlockVector3.at(bx, -64, bz);
            BlockVector3 pt2 = BlockVector3.at(bx + 15, 320, bz + 15);
            ProtectedCuboidRegion region = new ProtectedCuboidRegion("_", pt1, pt2);
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(chunk.getWorld()));
            if (regionManager != null) {
                for (ProtectedRegion regionIn : regionManager.getApplicableRegions(region)) {
                    StateFlag.State flag = regionIn.getFlag(plugin.getFlagChunksClaim());
                    if (flag == StateFlag.State.ALLOW) {
                        return true;
                    } else if (flag == StateFlag.State.DENY) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
            return false;
        }
    }
    public void setup(OfflinePlayer offlinePlayer, Chunk chunk) {
        File file = getFile(chunk);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("owner", offlinePlayer.getUniqueId().toString());
        config.set("date-claimed", offlinePlayer.getLastSeen());
        config.set("settings.tnt", false);
        try {
            addClaim(offlinePlayer, chunk);
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public void remove(OfflinePlayer offlinePlayer, Chunk chunk) {
        removeClaim(offlinePlayer, chunk);
        getFile(chunk).delete();
    }
    public void removeAll(OfflinePlayer offlinePlayer) {
        for (String worldName : getDatabase().getConfig(offlinePlayer).getConfigurationSection("chunks.worlds").getKeys(false)) {
            for (String longString : getDatabase().getConfig(offlinePlayer).getStringList("chunks.worlds." + worldName)) {
                long chunks = Long.parseLong(longString);
                Chunk chunk = getServer().getWorld(worldName).getChunkAt(chunks);
                remove(offlinePlayer, chunk);
            }
        }
    }
    public void setOwner(Player player, OfflinePlayer offlinePlayer, Chunk chunk) {
        File file = getFile(chunk);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("owner", offlinePlayer.getUniqueId().toString());
        config.set("date-claimed", player.getLastSeen());
        config.set("settings.tnt", false);
        try {
            addClaim(offlinePlayer, chunk);
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public OfflinePlayer getOwner(Chunk chunk) {
        return getServer().getOfflinePlayer(UUID.fromString(getConfig(chunk).getString("owner")));
    }
    public int getClaimCount(Chunk chunk) {
        return getClaimCount(getOwner(chunk));
    }
    public String getDateClaimed(Chunk chunk) {
        return SimpleDateFormat.getDateInstance().format(Long.parseLong(getConfig(chunk).getString("date-claimed")));
    }
    public boolean isClaimed(Chunk chunk) {
        return exist(chunk);
    }
    public boolean isTNTAllowed(Chunk chunk) {
        return getConfig(chunk).getBoolean("settings.tnt");
    }
    public void toggleTNT(Chunk chunk, boolean value) {
        File file = getFile(chunk);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("settings.tnt", value);
        try {
            config.save(file);
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    public boolean isOwner(OfflinePlayer offlinePlayer, Chunk chunk) {
        return getOwner(chunk) == offlinePlayer;
    }
    public boolean isMember(OfflinePlayer offlinePlayer, Chunk chunk) {
        return getMembers(getOwner(chunk)).contains(offlinePlayer);
    }
    public boolean hasAccess(Player player, Chunk chunk) {
        if (isClaimed(chunk)) {
            return isOwner(player, chunk) || isMember(player, chunk) || plugin.getChunkEditors().contains(player);
        } else {
            return true;
        }
    }
    public List<String> getBanned(Chunk chunk) {
        if (isClaimed(chunk)) {
            return getDatabase().getConfig(getOwner(chunk)).getStringList("chunks.banned");
        } else {
            return new ArrayList<>();
        }
    }
    public boolean isBanned(Chunk chunk, Player player) {
        return getBanned(chunk).contains(player.getUniqueId().toString());
    }
    public void claimEffect(Player player, Chunk chunk) {
        Location location = player.getLocation();
        Particle particle = Particle.valueOf(getConfig().getString("chunks.claim.particle"));
        player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
        player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
    }
    public void claimSound(Player player) {
        String soundType = getConfig().getString("chunks.claim.sound.type");
        float volume = (float) getConfig().getDouble("chunks.claim.sound.volume");
        float pitch = (float) getConfig().getDouble("chunks.claim.sound.pitch");
        player.playSound(player, Sound.valueOf(soundType), volume, pitch);
    }
    public void unclaimEffect(Player player, Chunk chunk) {
        Location location = player.getLocation();
        Particle particle = Particle.valueOf(getConfig().getString("chunks.unclaim.particle"));
        player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
        player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
    }
    public void unclaimSound(Player player) {
        String soundType = getConfig().getString("chunks.unclaim.sound.type");
        float volume = (float) getConfig().getDouble("chunks.unclaim.sound.volume");
        float pitch = (float) getConfig().getDouble("chunks.unclaim.sound.pitch");
        player.playSound(player, Sound.valueOf(soundType), volume, pitch);
    }
    public void claimEffect(Player player, Chunk chunk, OfflinePlayer offlinePlayer) {
        Location location = player.getLocation();
        Particle particle = Particle.valueOf(getConfig().getString("chunks.claim.particle"));
        Location north = new Location(player.getWorld(), chunk.getBlock(0, 0, 0).getX() + 0.5, location.getBlockY() - 1, chunk.getBlock(0, 0, 0).getZ() + 0.5 - 1);
        Location east = new Location(player.getWorld(), chunk.getBlock(0, 0, 0).getX() + 0.5 + 16, location.getBlockY() - 1, chunk.getBlock(0, 0, 0).getZ() + 0.5);
        Location south = new Location(player.getWorld(), chunk.getBlock(0, 0, 0).getX() + 0.5, location.getBlockY() - 1, chunk.getBlock(0, 0, 0).getZ() + 0.5 + 16);
        Location west = new Location(player.getWorld(), chunk.getBlock(0, 0, 0).getX() + 0.5 - 1, location.getBlockY() - 1, chunk.getBlock(0, 0, 0).getZ() + 0.5);
        if (isClaimed(north.getChunk())) {
            if (!getOwner(north.getChunk()).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
        }
        if (isClaimed(east.getChunk())) {
            if (!getOwner(east.getChunk()).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
        }
        if (isClaimed(south.getChunk())) {
            if (!getOwner(south.getChunk()).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
        }
        if (isClaimed(west.getChunk())) {
            if (!getOwner(west.getChunk()).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
        }
    }
    public void addClaim(OfflinePlayer offlinePlayer, Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        List<String> longList = getDatabase().getConfig(offlinePlayer).getStringList("chunks.worlds." + worldName);
        longList.add(String.valueOf(chunk.getChunkKey()));
        getDatabase().setStringList(offlinePlayer, "chunks.worlds." + worldName, longList);
    }
    public void removeClaim(OfflinePlayer offlinePlayer, Chunk chunk) {
        getEconomy().add(offlinePlayer, getConfig().getDouble("chunks.economy.refund"));
        String worldName = chunk.getWorld().getName();
        List<String> longList = getDatabase().getConfig(offlinePlayer).getStringList("chunks.worlds." + worldName);
        longList.remove(String.valueOf(chunk.getChunkKey()));
        getDatabase().setStringList(offlinePlayer, "chunks.worlds." + worldName, longList);
    }
    public int getClaimCount(OfflinePlayer offlinePlayer) {
        Set<String> worlds = getDatabase().getConfig(offlinePlayer).getConfigurationSection("chunks.worlds").getKeys(false);
        if (worlds.isEmpty()) {
            return 0;
        } else {
            List<Integer> test = new ArrayList<>();
            for (String world : worlds) {
                int size = getDatabase().getConfig(offlinePlayer).getStringList("chunks.worlds." + world).size();
                if (test.isEmpty()) {
                    test.addFirst(size);
                } else {
                    int tests = test.getFirst() + size;
                    test.addFirst(tests);
                }
            }
            return test.getFirst();
        }
    }
    public List<OfflinePlayer> getMembers(OfflinePlayer offlinePlayer) {
        List<OfflinePlayer> offlinePlayerList = new ArrayList<>();
        for (String uuidString : getDatabase().getConfig(offlinePlayer).getStringList("chunks.members")) {
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer member = getServer().getOfflinePlayer(uuid);
            offlinePlayerList.add(member);
        }
        return offlinePlayerList;
    }
    public List<String> getMembersUUIDString(OfflinePlayer offlinePlayer) {
        List<String> uuidStringList = new ArrayList<>();
        uuidStringList.addAll(getDatabase().getConfig(offlinePlayer).getStringList("chunks.members"));
        return uuidStringList;
    }
    public List<OfflinePlayer> getBanned(OfflinePlayer offlinePlayer) {
        List<OfflinePlayer> offlinePlayerList = new ArrayList<>();
        for (String uuidString : getDatabase().getConfig(offlinePlayer).getStringList("chunks.banned")) {
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer member = getServer().getOfflinePlayer(uuid);
            offlinePlayerList.add(member);
        }
        return offlinePlayerList;
    }
    public List<String> getBannedUUIDString(OfflinePlayer offlinePlayer) {
        return new ArrayList<>(getDatabase().getConfig(offlinePlayer).getStringList("chunks.banned"));
    }
    public boolean isPhysical(Block block) {
        return block.getType().equals(Material.FARMLAND) || block.getType().equals(Material.TURTLE_EGG) || Tag.PRESSURE_PLATES.isTagged(block.getType()) || block.getType().equals(Material.SNIFFER_EGG);
    }
    public boolean isRightClickBlock(Block block) {
        if (Tag.BEDS.isTagged(block.getType())) {
            return true;
        } else if (Tag.SHULKER_BOXES.isTagged(block.getType())) {
            return true;
        } else if (Tag.FLOWER_POTS.isTagged(block.getType())) {
            return true;
        } else if (Tag.ANVIL.isTagged(block.getType())) {
            return true;
        } else if (Tag.CAMPFIRES.isTagged(block.getType())) {
            return true;
        } else if (Tag.LOGS.isTagged(block.getType())) {
            return true;
        } else if (Tag.TRAPDOORS.isTagged(block.getType())) {
            return true;
        } else if (Tag.DOORS.isTagged(block.getType())) {
            return true;
        } else if (Tag.BUTTONS.isTagged(block.getType())) {
            return true;
        } else if (Tag.FENCE_GATES.isTagged(block.getType())) {
            return true;
        } else if (Tag.CANDLES.isTagged(block.getType())) {
            return true;
        } else if (block.getType().equals(Material.DECORATED_POT)) {
            return true;
        } else if (block.getType().equals(Material.CHISELED_BOOKSHELF)) {
            return true;
        } else if (block.getType().equals(Material.DISPENSER)) {
            return true;
        } else if (block.getType().equals(Material.DROPPER)) {
            return true;
        } else if (block.getType().equals(Material.HOPPER)) {
            return true;
        } else if (block.getType().equals(Material.DAYLIGHT_DETECTOR)) {
            return true;
        } else if (block.getType().equals(Material.LECTERN)) {
            return true;
        } else if (block.getType().equals(Material.COMPARATOR)) {
            return true;
        } else if (block.getType().equals(Material.REPEATER)) {
            return true;
        } else if (block.getType().equals(Material.REDSTONE_WIRE)) {
            return true;
        } else if (block.getType().equals(Material.LEVER)) {
            return true;
        } else if (block.getType().equals(Material.JUKEBOX)) {
            return true;
        } else if (block.getType().equals(Material.NOTE_BLOCK)) {
            return true;
        } else if (block.getType().equals(Material.BEEHIVE)) {
            return true;
        } else if (block.getType().equals(Material.BEE_NEST)) {
            return true;
        } else if (block.getType().equals(Material.RESPAWN_ANCHOR)) {
            return true;
        } else if (block.getType().equals(Material.LODESTONE)) {
            return true;
        } else if (block.getType().equals(Material.BEACON)) {
            return true;
        } else if (block.getType().equals(Material.BELL)) {
            return true;
        } else if (block.getType().equals(Material.BREWING_STAND)) {
            return true;
        } else if (block.getType().equals(Material.SMOKER)) {
            return true;
        } else if (block.getType().equals(Material.BLAST_FURNACE)) {
            return true;
        } else if (block.getType().equals(Material.FURNACE)) {
            return true;
        } else if (block.getType().equals(Material.CHEST)) {
            return true;
        } else if (block.getType().equals(Material.TRAPPED_CHEST)) {
            return true;
        } else return block.getType().equals(Material.BARREL);
    }
    public void chunkView(Player player, OfflinePlayer offlinePlayer) {
        if (getDatabase().getConfig(offlinePlayer).isList("chunks." + player.getWorld().getName())) {
            for (String longString : getDatabase().getConfig(offlinePlayer).getStringList("chunks.worlds." + player.getWorld().getName())) {
                long chunks = Long.parseLong(longString);
                Chunk chunk = player.getWorld().getChunkAt(chunks);
                if (chunk.isLoaded()) {
                    claimEffect(player, chunk, offlinePlayer);
                }
            }
        }
    }
    public void reload() {
        File folder = new File(getDataFolder(), "chunks");
        if (!folder.exists())return;
        for (String worlds : folder.list()) {
            File folders = new File(getDataFolder(), "chunks/" + worlds);
            if (!folders.exists())return;
            for (File files : folders.listFiles()) {
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