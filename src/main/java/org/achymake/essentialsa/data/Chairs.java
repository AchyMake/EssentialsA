package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public record Chairs(EssentialsA plugin) {
    public PersistentDataContainer getData(Player player) {
        return player.getPersistentDataContainer();
    }
    public PersistentDataContainer getData(Block block) {
        return block.getWorld().getPersistentDataContainer();
    }
    public boolean hasChair(Player player) {
        return getData(player).has(NamespacedKey.minecraft("essentials.chair"));
    }
    public void setChair(Player player, ArmorStand armorStand) {
        getData(player).set(NamespacedKey.minecraft("essentials.chair"), PersistentDataType.STRING, armorStand.getUniqueId().toString());
    }
    public ArmorStand getChair(Player player) {
        if (hasChair(player)) {
            return (ArmorStand) player.getServer().getEntity(UUID.fromString(getData(player).get(NamespacedKey.minecraft("essentials.chair"), PersistentDataType.STRING)));
        } else {
            return null;
        }
    }
    public boolean isOccupied(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        return getData(block).has(NamespacedKey.minecraft("essentials." + x + "." + y + "." + z), PersistentDataType.BOOLEAN);
    }
    public void setOccupied(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        getData(block).set(NamespacedKey.minecraft("essentials." + x + "." + y + "." + z), PersistentDataType.BOOLEAN, true);
    }
    public void removeOccupied(Block block) {
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        getData(block).remove(NamespacedKey.minecraft("essentials." + x + "." + y + "." + z));
    }
    public void setLastLocation(Player player) {
        getData(player).set(NamespacedKey.minecraft("essentials.last-location.x"), PersistentDataType.DOUBLE, player.getLocation().x());
        getData(player).set(NamespacedKey.minecraft("essentials.last-location.y"), PersistentDataType.DOUBLE, player.getLocation().y());
        getData(player).set(NamespacedKey.minecraft("essentials.last-location.z"), PersistentDataType.DOUBLE, player.getLocation().z());
    }
    public Location getLastLocation(Player player) {
        double x = getData(player).get(NamespacedKey.minecraft("essentials.last-location.x"), PersistentDataType.DOUBLE);
        double y = getData(player).get(NamespacedKey.minecraft("essentials.last-location.y"), PersistentDataType.DOUBLE);
        double z = getData(player).get(NamespacedKey.minecraft("essentials.last-location.z"), PersistentDataType.DOUBLE);
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        return new Location(player.getWorld(), x, y, z, yaw, pitch);
    }
    public void dismount(Player player, Block block) {
        if (getChair(player) != null) {
            getChair(player).remove();
        }
        getData(player).remove(NamespacedKey.minecraft("essentials.chair"));
        player.teleport(getLastLocation(player));
        getData(player).remove(NamespacedKey.minecraft("essentials.last-location.x"));
        getData(player).remove(NamespacedKey.minecraft("essentials.last-location.y"));
        getData(player).remove(NamespacedKey.minecraft("essentials.last-location.z"));
        removeOccupied(block);
    }
    public Stairs getStair(Block block) {
        return (Stairs) block.getBlockData();
    }
    public Slab getSlab(Block block) {
        return (Slab) block.getBlockData();
    }
    public boolean isAboveAir(Block block) {
        return block.getLocation().add(0,1,0).getBlock().getType().isAir();
    }
    public boolean isBottom(Slab slab) {
        return slab.getType().equals(Slab.Type.BOTTOM);
    }
    public boolean isBottom(Stairs stairs) {
        return stairs.getHalf().equals(Bisected.Half.BOTTOM);
    }
    public boolean isEast(Stairs stairs) {
        return stairs.getFacing().equals(BlockFace.EAST);
    }
    public boolean isNorth(Stairs stairs) {
        return stairs.getFacing().equals(BlockFace.NORTH);
    }
    public boolean isSouth(Stairs stairs) {
        return stairs.getFacing().equals(BlockFace.SOUTH);
    }
    public boolean isWest(Stairs stairs) {
        return stairs.getFacing().equals(BlockFace.WEST);
    }
    public boolean isStraight(Stairs stairs) {
        return stairs.getShape().equals(Stairs.Shape.STRAIGHT);
    }
    public boolean isInnerLeft(Stairs stairs) {
        return stairs.getShape().equals(Stairs.Shape.INNER_LEFT);
    }
    public boolean isInnerRight(Stairs stairs) {
        return stairs.getShape().equals(Stairs.Shape.INNER_RIGHT);
    }
    public void sit(Player player, Block block) {
        if (Tag.CARPETS.isTagged(block.getType())) {
            if (!player.hasPermission("essentials.sit.carpets"))return;
            setLastLocation(player);
            setOccupied(block);
            Location location = block.getLocation().add(0.5,-0.95,0.5);
            location.setYaw(player.getLocation().getYaw() + 180.0F);
            location.setPitch(0.0F);
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.addPassenger(player);
            setChair(player, armorStand);
        } else if (block.getType().equals(Material.HAY_BLOCK)) {
            if (!player.hasPermission("essentials.sit.hay_block"))return;
            setLastLocation(player);
            setOccupied(block);
            Location location = block.getLocation().add(0.5,-0.25,0.5);
            location.setYaw(player.getLocation().getYaw() + 180.0F);
            location.setPitch(0.0F);
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.addPassenger(player);
            setChair(player, armorStand);
        } else if (block.getType().equals(Material.SCAFFOLDING)) {
            if (!player.hasPermission("essentials.sit.scaffolding"))return;
            setLastLocation(player);
            setOccupied(block);
            Location location = block.getLocation().add(0.5,0.0,0.5);
            location.setYaw(player.getLocation().getYaw() + 180.0F);
            location.setPitch(0.0F);
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.addPassenger(player);
            setChair(player, armorStand);
        } else if (Tag.SLABS.isTagged(block.getType())) {
            if (!isBottom(getSlab(block)))return;
            if (!player.hasPermission("essentials.sit.slabs"))return;
            setLastLocation(player);
            setOccupied(block);
            Location location = block.getLocation().add(0.5,-0.5,0.5);
            location.setYaw(player.getLocation().getYaw() + 180.0F);
            location.setPitch(0.0F);
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.addPassenger(player);
            setChair(player, armorStand);
        } else if (Tag.STAIRS.isTagged(block.getType())) {
            if (!isBottom(getStair(block)))return;
            if (!player.hasPermission("essentials.sit.stairs"))return;
            if (isEast(getStair(block))) {
                if (isStraight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(90.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerLeft(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(25.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerRight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(155.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                }
            } else if (isNorth(getStair(block))) {
                if (isStraight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(0.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerLeft(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(-65.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerRight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(65.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                }
            } else if (isSouth(getStair(block))) {
                if (isStraight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(180.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerLeft(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(115.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerRight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(-115.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                }
            } else if (isWest(getStair(block))) {
                if (isStraight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(-90.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerLeft(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(-155.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                } else if (isInnerRight(getStair(block))) {
                    setLastLocation(player);
                    setOccupied(block);
                    Location location = block.getLocation().add(0.5,-0.4,0.5);
                    location.setYaw(-25.0F);
                    location.setPitch(0.0F);
                    ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setGravity(false);
                    armorStand.setSmall(true);
                    armorStand.addPassenger(player);
                    setChair(player, armorStand);
                }
            }
        }
    }
}