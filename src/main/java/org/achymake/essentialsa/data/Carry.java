package org.achymake.essentialsa.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.logging.Level;

public record Carry(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile(String path) {
        return new File(getDataFolder(), path);
    }
    public File getFile(Entity entity) {
        return getFile("entity/" + entity.getType() + ".yml");
    }
    public boolean exist(Entity entity) {
        return getFile(entity).exists();
    }
    public FileConfiguration getConfig(Entity entity) {
        return YamlConfiguration.loadConfiguration(getFile(entity));
    }
    public boolean isEnable(Entity entity) {
        return getConfig(entity).getBoolean("carry.enable");
    }
    public boolean isAllowCarry(Block block) {
        try {
            BlockVector3 pt1 = BlockVector3.at(block.getX(), block.getY(), block.getZ());
            BlockVector3 pt2 = BlockVector3.at(block.getX(), block.getY(), block.getZ());
            ProtectedCuboidRegion region = new ProtectedCuboidRegion("_", pt1, pt2);
            RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(block.getWorld()));
            if (regionManager != null) {
                for (ProtectedRegion regionIn : regionManager.getApplicableRegions(region)) {
                    StateFlag.State flag = regionIn.getFlag(plugin.getFlagCarry());
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
    public void carry(Player player, Entity entity, boolean swingArm) {
        if (hasPassenger(entity)) {
            entity.eject();
        }
        getEntities().setScale(entity, 0.5);
        if (player.getPassenger() == null) {
            player.addPassenger(entity);
            addCarryTask(player);
        }
        if (swingArm) {
            player.swingMainHand();
        }
        if (getPassenger(player).getType().equals(EntityType.PLAYER)) {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getName());
        } else if (getPassenger(player).getCustomName() == null) {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getEntities().getName(getPassenger(player)));
        } else {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getCustomName());
        }
    }
    public void stack(Player player, Entity entity) {
        Entity passenger = getPassenger(player);
        if (passenger == null)return;
        if (!player.hasPermission("essentials.carry.stack"))return;
        getEntities().setScale(passenger, 1);
        if (hasPassenger(entity)) {
            getPassenger(entity).addPassenger(passenger);
            player.swingMainHand();
        } else {
            entity.addPassenger(passenger);
            player.swingMainHand();
        }
    }
    public void removeMount(Player player, Entity entity, Block block) {
        double x = block.getLocation().getX() + 0.5;
        double y = block.getLocation().getY() + 1;
        double z = block.getLocation().getZ() + 0.5;
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        Location location = new Location(block.getWorld(), x, y, z, yaw, pitch);
        getEntities().setScale(entity, 1);
        entity.teleport(location);
        plugin.getScheduler().cancelTask(plugin.getUserdata().getTaskID(player, "carry"));
        plugin.getUserdata().removeTaskID(player, "carry");
    }
    public void removeMount(Player player) {
        Entity passenger = getPassenger(player);
        if (passenger != null) {
            getEntities().setScale(passenger, 1);
            passenger.leaveVehicle();
            plugin.getScheduler().cancelTask(plugin.getUserdata().getTaskID(player, "carry"));
            plugin.getUserdata().removeTaskID(player, "carry");
        }
    }
    public Entity getMount(Entity entity) {
        if (entity.isInsideVehicle()) {
            return entity.getVehicle();
        } else return null;
    }
    public boolean hasPassenger(Entity entity) {
        return entity.getPassenger() != null;
    }
    public Entity getPassenger(Entity entity) {
        if (hasPassenger(entity)) {
            if (hasPassenger(entity.getPassenger())) {
                return getPassenger(entity.getPassenger());
            } else {
                return entity.getPassenger();
            }
        } else {
            return null;
        }
    }
    public Entity getPassenger(Player player) {
        if (player.getPassenger() != null) {
            return player.getPassenger();
        } else {
            return null;
        }
    }
    public void addEffects(Player player) {
        if (getEntities().isAdult(getPassenger(player))) {
            if (getConfig(getPassenger(player)).getInt("carry.weight.adult") > 0) {
                if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, getConfig(getPassenger(player)).getInt("carry.weight.adult") - 1));
                }
                if (!player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, getConfig(getPassenger(player)).getInt("carry.weight.adult") - 1));
                }
            }
        } else if (getConfig(getPassenger(player)).getInt("carry.weight.baby") > 0) {
            if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, getConfig(getPassenger(player)).getInt("carry.weight.baby") - 1));
            }
            if (!player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, getConfig(getPassenger(player)).getInt("carry.weight.baby") - 1));
            }
        }
    }
    private void addCarryTask(Player player) {
        int taskID = plugin.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (getPassenger(player).getType().equals(EntityType.PLAYER)) {
                    getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getName());
                } else if (getPassenger(player).getCustomName() == null) {
                    getMessage().sendActionBar(player, "&6You are carrying&f " + getEntities().getName(getPassenger(player)));
                } else {
                    getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getCustomName());
                }
                plugin.getUserdata().removeTaskID(player, "vanish");
                addCarryTask(player);
            }
        }, 50).getTaskId();
        plugin.getUserdata().addTaskID(player, "carry", taskID);
    }
}
