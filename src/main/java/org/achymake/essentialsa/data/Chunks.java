package org.achymake.essentialsa.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
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

public record Chunks(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile(Chunk chunk) {
        return new File(getDataFolder(), "chunks/" + chunk.getWorld().getName() + "/" + chunk.getChunkKey() + ".yml");
    }
    public File getFile() {
        return new File(getDataFolder(), "chunks.yml");
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean exist(Chunk chunk) {
        return getFile(chunk).exists();
    }
    public FileConfiguration getConfig(Chunk chunk) {
        return YamlConfiguration.loadConfiguration(getFile(chunk));
    }
    public boolean isEnable() {
        return getConfig().getBoolean("enable");
    }
    public boolean isAllowedWorld(World world) {
        return getConfig().getStringList("worlds").contains(world.getName());
    }
    public boolean isAllowedClaim(Chunk chunk) {
        try {
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(chunk.getWorld()));
            if (regionManager != null) {
                int x = chunk.getX() << 4;
                int z = chunk.getZ() << 4;
                ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion("_", BlockVector3.at(x, -64, z), BlockVector3.at(x + 15, 320, z + 15));
                for (ProtectedRegion regionIn : regionManager.getApplicableRegions(protectedCuboidRegion)) {
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
        for (String worldName : getUserdata().getConfig(offlinePlayer).getConfigurationSection("chunks.worlds").getKeys(false)) {
            for (String longString : getUserdata().getConfig(offlinePlayer).getStringList("chunks.worlds." + worldName)) {
                Chunk chunk = getWorlds().getChunk(getWorlds().getWorld(worldName), Long.parseLong(longString));
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
            return getUserdata().getConfig(getOwner(chunk)).getStringList("chunks.banned");
        } else {
            return new ArrayList<>();
        }
    }
    public boolean isBanned(Chunk chunk, Player player) {
        return getBanned(chunk).contains(player.getUniqueId().toString());
    }
    public void claimEffect(Player player, Chunk chunk) {
        Location location = player.getLocation();
        Particle particle = Particle.valueOf(getConfig().getString("claim.particle"));
        player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
        player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
    }
    public void claimSound(Player player) {
        String soundType = getConfig().getString("claim.sound.type");
        float volume = (float) getConfig().getDouble("claim.sound.volume");
        float pitch = (float) getConfig().getDouble("claim.sound.pitch");
        player.playSound(player, Sound.valueOf(soundType), volume, pitch);
    }
    public void unclaimEffect(Player player, Chunk chunk) {
        Location location = player.getLocation();
        Particle particle = Particle.valueOf(getConfig().getString("unclaim.particle"));
        player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
        player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
        player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
    }
    public void unclaimSound(Player player) {
        String soundType = getConfig().getString("unclaim.sound.type");
        float volume = (float) getConfig().getDouble("unclaim.sound.volume");
        float pitch = (float) getConfig().getDouble("unclaim.sound.pitch");
        player.playSound(player, Sound.valueOf(soundType), volume, pitch);
    }
    public void claimEffect(Player player, Chunk chunk, OfflinePlayer offlinePlayer) {
        Location location = player.getLocation();
        Particle particle = Particle.valueOf(getConfig().getString("claim.particle"));
        Chunk chunkNorth = getWorlds().getChunk(player.getWorld(), chunk.getX(), chunk.getZ() - 1);
        Chunk chunkEast = getWorlds().getChunk(player.getWorld(), chunk.getX() + 1, chunk.getZ());
        Chunk chunkSouth = getWorlds().getChunk(player.getWorld(), chunk.getX(), chunk.getZ() + 1);
        Chunk chunkWest = getWorlds().getChunk(player.getWorld(), chunk.getX() - 1, chunk.getZ());
        if (isClaimed(chunkNorth)) {
            if (!getOwner(chunkNorth).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(8, 0, 0).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 0).getZ(), 250, 4, 12, 0, 0);
        }
        if (isClaimed(chunkEast)) {
            if (!getOwner(chunkEast).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(15, 0, 8).getX() + 1, location.getBlockY() - 1, chunk.getBlock(15, 0, 8).getZ(), 250, 0, 12, 4, 0);
        }
        if (isClaimed(chunkSouth)) {
            if (!getOwner(chunkSouth).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(8, 0, 15).getX(), location.getBlockY() - 1, chunk.getBlock(8, 0, 15).getZ() + 1, 250, 4, 12, 0, 0);
        }
        if (isClaimed(chunkWest)) {
            if (!getOwner(chunkWest).equals(offlinePlayer)) {
                player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
            }
        } else {
            player.spawnParticle(particle, chunk.getBlock(0, 0, 8).getX(), location.getBlockY() - 1, chunk.getBlock(0, 0, 8).getZ(), 250, 0, 12, 4, 0);
        }
    }
    public void addClaim(OfflinePlayer offlinePlayer, Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        List<String> longList = getUserdata().getConfig(offlinePlayer).getStringList("chunks.worlds." + worldName);
        longList.add(String.valueOf(chunk.getChunkKey()));
        getUserdata().setStringList(offlinePlayer, "chunks.worlds." + worldName, longList);
    }
    public void removeClaim(OfflinePlayer offlinePlayer, Chunk chunk) {
        getEconomy().depositPlayer(offlinePlayer, getConfig().getDouble("economy.refund"));
        String worldName = chunk.getWorld().getName();
        List<String> longList = getUserdata().getConfig(offlinePlayer).getStringList("chunks.worlds." + worldName);
        longList.remove(String.valueOf(chunk.getChunkKey()));
        getUserdata().setStringList(offlinePlayer, "chunks.worlds." + worldName, longList);
    }
    public int getClaimCount(OfflinePlayer offlinePlayer) {
        Set<String> worlds = getUserdata().getConfig(offlinePlayer).getConfigurationSection("chunks.worlds").getKeys(false);
        if (worlds.isEmpty()) {
            return 0;
        } else {
            List<Integer> test = new ArrayList<>();
            for (String world : worlds) {
                int size = getUserdata().getConfig(offlinePlayer).getStringList("chunks.worlds." + world).size();
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
        for (String uuidString : getUserdata().getConfig(offlinePlayer).getStringList("chunks.members")) {
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer member = getServer().getOfflinePlayer(uuid);
            offlinePlayerList.add(member);
        }
        return offlinePlayerList;
    }
    public List<String> getMembersUUIDString(OfflinePlayer offlinePlayer) {
        List<String> uuidStringList = new ArrayList<>();
        uuidStringList.addAll(getUserdata().getConfig(offlinePlayer).getStringList("chunks.members"));
        return uuidStringList;
    }
    public List<OfflinePlayer> getBanned(OfflinePlayer offlinePlayer) {
        List<OfflinePlayer> offlinePlayerList = new ArrayList<>();
        for (String uuidString : getUserdata().getConfig(offlinePlayer).getStringList("chunks.banned")) {
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer member = getServer().getOfflinePlayer(uuid);
            offlinePlayerList.add(member);
        }
        return offlinePlayerList;
    }
    public List<String> getBannedUUIDString(OfflinePlayer offlinePlayer) {
        return new ArrayList<>(getUserdata().getConfig(offlinePlayer).getStringList("chunks.banned"));
    }
    public void chunkView(Player player, OfflinePlayer offlinePlayer) {
        if (getUserdata().getConfig(offlinePlayer).isList("chunks." + player.getWorld().getName())) {
            for (String longString : getUserdata().getConfig(offlinePlayer).getStringList("chunks.worlds." + player.getWorld().getName())) {
                Chunk chunk = getWorlds().getChunk(player.getWorld(), Long.parseLong(longString));
                if (chunk.isLoaded()) {
                    claimEffect(player, chunk, offlinePlayer);
                }
            }
        }
    }
    public boolean isDisableBlockPlace() {
        return getConfig().getBoolean("settings.disable-block-place");
    }
    public boolean isDisableBlockFertilize() {
        return getConfig().getBoolean("settings.disable-block-fertilize");
    }
    public boolean isDisableBlockBreak() {
        return getConfig().getBoolean("settings.disable-block-break");
    }
    public boolean isDisabledHarvestBlocks(Block block) {
        return getConfig().getStringList("settings.disabled-harvest-blocks").contains(block.getType().toString());
    }
    public boolean isDisabledInteractPhysicalBlocks(Block block) {
        return getConfig().getStringList("settings.disabled-interact-physical-blocks").contains(block.getType().toString());
    }
    public boolean isDisabledInteractBlocks(Block block) {
        return getConfig().getStringList("settings.disabled-interact-blocks").contains(block.getType().toString());
    }
    public boolean isDisableCauldronLevelChange() {
        return getConfig().getBoolean("settings.disable-cauldron-level-change");
    }
    public boolean isDisabledChangeBlocks(Block block) {
        return getConfig().getStringList("settings.disabled-change-blocks").contains(block.getType().toString());
    }
    public boolean isDisableBuckets(Material material) {
        return getConfig().getStringList("settings.disable-buckets").contains(material.toString());
    }
    public boolean isDisableShearBlocks(Block block) {
        return getConfig().getStringList("settings.disable-shear-blocks").contains(block.getType().toString());
    }
    public boolean isDisableSignChange() {
        return getConfig().getBoolean("settings.disable-sign-change");
    }
    private void reloadChunks() {
        File folder = new File(getDataFolder(), "chunks");
        if (folder.exists() | folder.isDirectory()) {
            for (String worlds : folder.list()) {
                File folders = new File(getDataFolder(), "chunks/" + worlds);
                if (folders.exists() | folders.isDirectory()) {
                    for (File files : folders.listFiles()) {
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
            List<String> worlds = new ArrayList<>();
            worlds.add("world");
            worlds.add("world_nether");
            worlds.add("world_the_end");
            config.set("worlds", worlds);
            config.set("economy.cost", 1000.0);
            config.set("economy.refund", 750.0);
            config.set("economy.multiply", 25);
            config.set("claim.max-claims", 32);
            config.set("claim.redstone-only-inside", false);
            config.set("claim.particle", "TOTEM");
            config.set("claim.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("claim.sound.volume", 0.75);
            config.set("claim.sound.pitch", 1.0);
            config.set("unclaim.particle", "SPELL_INSTANT");
            config.set("unclaim.sound.type", "BLOCK_AMETHYST_BLOCK_BREAK");
            config.set("unclaim.sound.volume", 0.75);
            config.set("unclaim.sound.pitch", 1.0);
            config.set("settings.disable-block-break", true);
            config.set("settings.disable-block-fertilize", true);
            config.set("settings.disable-block-place", true);
            config.set("settings.disable-cauldron-level-change", true);
            List<String> dipd = new ArrayList<>();
            dipd.add("FARMLAND");
            dipd.add("TURTLE_EGG");
            dipd.add("SNIFFER_EGG");
            dipd.add("OAK_PRESSURE_PLATE");
            dipd.add("SPRUCE_PRESSURE_PLATE");
            dipd.add("BIRCH_PRESSURE_PLATE");
            dipd.add("JUNGLE_PRESSURE_PLATE");
            dipd.add("ACACIA_PRESSURE_PLATE");
            dipd.add("DARK_OAK_PRESSURE_PLATE");
            dipd.add("MANGROVE_PRESSURE_PLATE");
            dipd.add("CHERRY_PRESSURE_PLATE");
            dipd.add("BAMBOO_PRESSURE_PLATE");
            dipd.add("CRIMSON_PRESSURE_PLATE");
            dipd.add("WARPED_PRESSURE_PLATE");
            dipd.add("STONE_PRESSURE_PLATE");
            dipd.add("POLISHED_BLACKSTONE_PRESSURE_PLATE");
            dipd.add("HEAVY_WEIGHTED_PRESSURE_PLATE");
            dipd.add("LIGHT_WEIGHTED_PRESSURE_PLATE");
            dipd.sort(String::compareToIgnoreCase);
            config.set("settings.disabled-interact-physical-blocks", dipd);
            List<String> dib = new ArrayList<>();
            dib.add("OAK_FENCE_GATE");
            dib.add("OAK_DOOR");
            dib.add("OAK_TRAPDOOR");
            dib.add("OAK_BUTTON");
            dib.add("SPRUCE_FENCE_GATE");
            dib.add("SPRUCE_DOOR");
            dib.add("SPRUCE_TRAPDOOR");
            dib.add("SPRUCE_BUTTON");
            dib.add("BIRCH_FENCE_GATE");
            dib.add("BIRCH_DOOR");
            dib.add("BIRCH_TRAPDOOR");
            dib.add("BIRCH_BUTTON");
            dib.add("JUNGLE_FENCE_GATE");
            dib.add("JUNGLE_DOOR");
            dib.add("JUNGLE_TRAPDOOR");
            dib.add("JUNGLE_BUTTON");
            dib.add("ACACIA_FENCE_GATE");
            dib.add("ACACIA_DOOR");
            dib.add("ACACIA_TRAPDOOR");
            dib.add("ACACIA_BUTTON");
            dib.add("DARK_OAK_FENCE_GATE");
            dib.add("DARK_OAK_DOOR");
            dib.add("DARK_OAK_TRAPDOOR");
            dib.add("DARK_OAK_BUTTON");
            dib.add("MANGROVE_FENCE_GATE");
            dib.add("MANGROVE_DOOR");
            dib.add("MANGROVE_TRAPDOOR");
            dib.add("MANGROVE_BUTTON");
            dib.add("CHERRY_FENCE_GATE");
            dib.add("CHERRY_DOOR");
            dib.add("CHERRY_TRAPDOOR");
            dib.add("CHERRY_BUTTON");
            dib.add("BAMBOO_FENCE_GATE");
            dib.add("BAMBOO_DOOR");
            dib.add("BAMBOO_TRAPDOOR");
            dib.add("BAMBOO_BUTTON");
            dib.add("CRIMSON_FENCE_GATE");
            dib.add("CRIMSON_DOOR");
            dib.add("CRIMSON_TRAPDOOR");
            dib.add("CRIMSON_BUTTON");
            dib.add("WARPED_FENCE_GATE");
            dib.add("WARPED_DOOR");
            dib.add("WARPED_TRAPDOOR");
            dib.add("WARPED_BUTTON");
            dib.add("STONE_BUTTON");
            dib.add("POLISHED_BLACKSTONE_BUTTON");
            dib.add("COPPER_DOOR");
            dib.add("COPPER_TRAPDOOR");
            dib.add("EXPOSED_COPPER_DOOR");
            dib.add("EXPOSED_COPPER_TRAPDOOR");
            dib.add("WEATHERED_COPPER_DOOR");
            dib.add("WEATHERED_COPPER_TRAPDOOR");
            dib.add("OXIDIZED_COPPER_DOOR");
            dib.add("OXIDIZED_COPPER_TRAPDOOR");
            dib.add("WAXED_COPPER_DOOR");
            dib.add("WAXED_COPPER_TRAPDOOR");
            dib.add("WAXED_EXPOSED_COPPER_DOOR");
            dib.add("WAXED_EXPOSED_COPPER_TRAPDOOR");
            dib.add("WAXED_WEATHERED_COPPER_DOOR");
            dib.add("WAXED_WEATHERED_COPPER_TRAPDOOR");
            dib.add("WAXED_OXIDIZED_COPPER_DOOR");
            dib.add("WAXED_OXIDIZED_COPPER_TRAPDOOR");
            dib.add("SHULKER_BOX");
            dib.add("WHITE_SHULKER_BOX");
            dib.add("LIGHT_GRAY_SHULKER_BOX");
            dib.add("GRAY_SHULKER_BOX");
            dib.add("BLACK_SHULKER_BOX");
            dib.add("BROWN_SHULKER_BOX");
            dib.add("RED_SHULKER_BOX");
            dib.add("ORANGE_SHULKER_BOX");
            dib.add("YELLOW_SHULKER_BOX");
            dib.add("LIME_SHULKER_BOX");
            dib.add("GREEN_SHULKER_BOX");
            dib.add("CYAN_SHULKER_BOX");
            dib.add("LIGHT_BLUE_SHULKER_BOX");
            dib.add("BLUE_SHULKER_BOX");
            dib.add("PURPLE_SHULKER_BOX");
            dib.add("MAGENTA_SHULKER_BOX");
            dib.add("PING_SHULKER_BOX");
            dib.add("WHITE_BED");
            dib.add("LIGHT_GRAY_BED");
            dib.add("GRAY_BED");
            dib.add("BLACK_BED");
            dib.add("BROWN_BED");
            dib.add("RED_BED");
            dib.add("ORANGE_BED");
            dib.add("YELLOW_BED");
            dib.add("LIME_BED");
            dib.add("GREEN_BED");
            dib.add("CYAN_BED");
            dib.add("LIGHT_BLUE_BED");
            dib.add("BLUE_BED");
            dib.add("PURPLE_BED");
            dib.add("MAGENTA_BED");
            dib.add("PINK_BED");
            dib.add("CANDLE");
            dib.add("WHITE_CANDLE");
            dib.add("LIGHT_GRAY_CANDLE");
            dib.add("GRAY_CANDLE");
            dib.add("BLACK_CANDLE");
            dib.add("BROWN_CANDLE");
            dib.add("RED_CANDLE");
            dib.add("ORANGE_CANDLE");
            dib.add("YELLOW_CANDLE");
            dib.add("LIME_CANDLE");
            dib.add("GREEN_CANDLE");
            dib.add("CYAN_CANDLE");
            dib.add("LIGHT_BLUE_CANDLE");
            dib.add("BLUE_CANDLE");
            dib.add("PURPLE_CANDLE");
            dib.add("MAGENTA_CANDLE");
            dib.add("PINK_CANDLE");
            dib.add("ANVIL");
            dib.add("CHIPPED_ANVIL");
            dib.add("DAMAGED_ANVIL");
            dib.add("JUKEBOX");
            dib.add("BREWING_STAND");
            dib.add("BELL");
            dib.add("BEACON");
            dib.add("FLOWER_POT");
            dib.add("DECORATED_POT");
            dib.add("CHISELED_BOOKSHELF");
            dib.add("LECTERN");
            dib.add("CHEST");
            dib.add("BARREL");
            dib.add("VAULT");
            dib.add("REDSTONE_WIRE");
            dib.add("REPEATER");
            dib.add("COMPARATOR");
            dib.add("LEVER");
            dib.add("DAYLIGHT_DETECTOR");
            dib.add("DISPENSER");
            dib.add("DROPPER");
            dib.add("CRAFTER");
            dib.add("HOPPER");
            dib.add("TRAPPED_CHEST");
            dib.add("TRIAL_SPAWNER");
            dib.sort(String::compareToIgnoreCase);
            config.set("settings.disabled-interact-blocks", dib);
            List<String> dhb = new ArrayList<>();
            dhb.add("CAVE_VINES_PLANT");
            dhb.add("CAVE_VINES");
            dhb.add("SWEET_BERRY_BUSH");
            dhb.add("BEEHIVE");
            dhb.add("BEE_NEST");
            dhb.sort(String::compareToIgnoreCase);
            config.set("settings.disabled-harvest-blocks", dhb);
            List<String> dcb = new ArrayList<>();
            dcb.add("GRASS_BLOCK");
            dcb.add("DIRT");
            dcb.add("COARSE_DIRT");
            dcb.add("ROOTED_DIRT");
            dcb.add("PODZOL");
            dcb.add("MYCELIUM");
            dcb.add("DIRT_PATH");
            dcb.add("OAK_LOG");
            dcb.add("OAK_WOOD");
            dcb.add("SPRUCE_LOG");
            dcb.add("SPRUCE_WOOD");
            dcb.add("BIRCH_LOG");
            dcb.add("BIRCH_WOOD");
            dcb.add("JUNGLE_LOG");
            dcb.add("JUNGLE_WOOD");
            dcb.add("ACACIA_LOG");
            dcb.add("ACACIA_WOOD");
            dcb.add("DARK_OAK_LOG");
            dcb.add("DARK_OAK_WOOD");
            dcb.add("MANGROVE_LOG");
            dcb.add("MANGROVE_WOOD");
            dcb.add("CHERRY_LOG");
            dcb.add("CHERRY_WOOD");
            dcb.add("BAMBOO_BLOCK");
            dcb.add("CRIMSON_STEM");
            dcb.add("CRIMSON_HYPHAE");
            dcb.add("WARPED_STEM");
            dcb.add("WARPED_HYPHAE");
            dcb.add("PUMPKIN");
            dcb.add("TRIAL_SPAWNER");
            dcb.add("VAULT");
            dcb.add("COPPER_BLOCK");
            dcb.add("CHISELED_COPPER");
            dcb.add("COPPER_GRATE");
            dcb.add("CUT_COPPER");
            dcb.add("CUT_COPPER_STAIRS");
            dcb.add("CUT_COPPER_SLAB");
            dcb.add("COPPER_DOOR");
            dcb.add("COPPER_TRAPDOOR");
            dcb.add("COPPER_BULB");
            dcb.add("EXPOSED_COPPER");
            dcb.add("EXPOSED_CHISELED_COPPER");
            dcb.add("EXPOSED_COPPER_GRATE");
            dcb.add("EXPOSED_CUT_COPPER");
            dcb.add("EXPOSED_CUT_COPPER_STAIRS");
            dcb.add("EXPOSED_CUT_COPPER_SLAB");
            dcb.add("EXPOSED_COPPER_DOOR");
            dcb.add("EXPOSED_COPPER_TRAPDOOR");
            dcb.add("EXPOSED_COPPER_BULB");
            dcb.add("WEATHERED_COPPER");
            dcb.add("WEATHERED_CHISELED_COPPER");
            dcb.add("WEATHERED_COPPER_GRATE");
            dcb.add("WEATHERED_CUT_COPPER");
            dcb.add("WEATHERED_CUT_COPPER_STAIRS");
            dcb.add("WEATHERED_CUT_COPPER_SLAB");
            dcb.add("WEATHERED_COPPER_DOOR");
            dcb.add("WEATHERED_COPPER_TRAPDOOR");
            dcb.add("WEATHERED_COPPER_BULB");
            dcb.add("OXIDIZED_COPPER");
            dcb.add("OXIDIZED_CHISELED_COPPER");
            dcb.add("OXIDIZED_COPPER_GRATE");
            dcb.add("OXIDIZED_CUT_COPPER");
            dcb.add("OXIDIZED_CUT_COPPER_STAIRS");
            dcb.add("OXIDIZED_CUT_COPPER_SLAB");
            dcb.add("OXIDIZED_COPPER_DOOR");
            dcb.add("OXIDIZED_COPPER_TRAPDOOR");
            dcb.add("OXIDIZED_COPPER_BULB");
            dcb.add("WAXED_COPPER_BLOCK");
            dcb.add("WAXED_");
            dcb.sort(String::compareToIgnoreCase);
            config.set("settings.disabled-change-blocks", dcb);
            List<String> db = new ArrayList<>();
            db.add("BUCKET");
            db.add("WATER_BUCKET");
            db.add("COD_BUCKET");
            db.add("SALMON_BUCKET");
            db.add("TROPICAL_FISH_BUCKET");
            db.add("PUFFERFISH_BUCKET");
            db.add("AXOLOTL_BUCKET");
            db.add("TADPOLE_BUCKET");
            db.add("LAVA_BUCKET");
            db.add("POWDER_SNOW_BUCKET");
            db.add("MILK_BUCKET");
            db.sort(String::compareToIgnoreCase);
            config.set("settings.disabled-buckets", db);
            List<String> dsb = new ArrayList<>();
            dsb.add("BEE_NEST");
            dsb.add("BEEHIVE");
            dsb.sort(String::compareToIgnoreCase);
            config.set("settings.disabled-shear-blocks", dsb);
            config.set("settings.disable-sign-change", true);
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
        reloadChunks();
    }
}