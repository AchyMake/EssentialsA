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
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public record Entities(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile(EntityType entityType) {
        return new File(getDataFolder(), "entity/" + entityType + ".yml");
    }
    public boolean exist(Entity entity) {
        return getFile(entity.getType()).exists();
    }
    public FileConfiguration getConfig(Entity entity) {
        return YamlConfiguration.loadConfiguration(getFile(entity.getType()));
    }
    public boolean isEnable(Entity entity) {
        return getFile(entity.getType()).exists();
    }
    public String getName(Entity entity) {
        return getConfig(entity).getString("name");
    }
    public int getChunkLimit(Entity entity) {
        return getConfig(entity).getInt("chunk-limit");
    }
    public boolean disableSpawn(Entity entity) {
        return getConfig(entity).getBoolean("disable-spawn");
    }
    public boolean disableBlockForm(Entity entity) {
        return getConfig(entity).getBoolean("disable-block-form");
    }
    public boolean disableBlockDamage(Entity entity) {
        return getConfig(entity).getBoolean("disable-block-damage");
    }
    public boolean disableBlockChange(Entity entity) {
        return getConfig(entity).getBoolean("disable-block-change");
    }
    public boolean disableBlockInteract(Entity entity, Block block) {
        return getConfig(entity).getBoolean("disable-block-interact." + block.getType());
    }
    public boolean disableTarget(Entity entity, Entity target) {
        return getConfig(entity).getBoolean("disable-target." + target.getType());
    }
    public boolean disableDamage(Entity damager, Entity entity) {
        return getConfig(damager).getBoolean("disable-damage." + entity.getType());
    }
    public boolean isHostile(Entity entity) {
        return getConfig(entity).getBoolean("hostile");
    }
    public boolean isEnableCarry(Entity entity) {
        return getConfig(entity).getBoolean("carry.enable");
    }
    private PersistentDataContainer getData(Entity entity) {
        return entity.getPersistentDataContainer();
    }
    public void vanishEntity(Player player, Entity entity, boolean value) {
        if (value) {
            player.hideEntity(plugin, entity);
        } else {
            player.showEntity(plugin, entity);
        }
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
    public void carry(Player player, Entity entity) {
        if (hasPassenger(entity)) {
            if (player.getPassenger() != null)return;
            addMount(player, getPassenger(entity));
            player.swingMainHand();
        } else {
            if (player.getPassenger() != null)return;
            addMount(player, entity);
            player.swingMainHand();
        }
    }
    public void stack(Player player, Entity entity) {
        if (player.getPassenger() == null)return;
        if (!player.hasPermission("essentials.carry.stack"))return;
        vanishEntity(player, player.getPassenger(), false);
        setScale(player.getPassenger(), 1);
        if (hasPassenger(entity)) {
            getPassenger(entity).addPassenger(player.getPassenger());
            player.swingMainHand();
        } else {
            entity.addPassenger(player.getPassenger());
            player.swingMainHand();
        }
    }
    public void addMount(Player player, Entity entity) {
        getData(entity).set(NamespacedKey.minecraft("mount"), PersistentDataType.STRING, player.getUniqueId().toString());
        getData(player).set(NamespacedKey.minecraft("passenger"), PersistentDataType.STRING, entity.getUniqueId().toString());
        player.addPassenger(entity);
        setScale(entity, 0.5);
        vanishEntity(player, entity, true);
    }
    public void removeMount(Player player, Entity entity) {
        vanishEntity(player, entity, false);
        setScale(entity, 1);
        entity.teleport(player.getLocation());
        getData(entity).remove(NamespacedKey.minecraft("mount"));
        getData(player).remove(NamespacedKey.minecraft("passenger"));
    }
    public void removeMount(Player player, Entity entity, Block block) {
        vanishEntity(player, entity, false);
        double x = block.getLocation().getX() + 0.5;
        double y = block.getLocation().getY() + 1;
        double z = block.getLocation().getZ() + 0.5;
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        Location location = new Location(block.getWorld(), x, y, z, yaw, pitch);
        setScale(entity, 1);
        entity.teleport(location);
        getData(entity).remove(NamespacedKey.minecraft("mount"));
        getData(player).remove(NamespacedKey.minecraft("passenger"));
    }
    public boolean hasMount(Entity entity) {
        return getData(entity).has(NamespacedKey.minecraft("mount"), PersistentDataType.STRING);
    }
    public Player getMount(Entity entity) {
        if (hasMount(entity)) {
            return getServer().getPlayer(UUID.fromString(getData(entity).get(NamespacedKey.minecraft("mount"), PersistentDataType.STRING)));
        } else {
            return null;
        }
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
    public void addEffects(Player player) {
        if (isAdult(getPassenger(player))) {
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
        if (getPassenger(player).getType().equals(EntityType.PLAYER)) {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getName());
        } else if (getPassenger(player).getCustomName() == null) {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getName(getPassenger(player)));
        } else {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getCustomName());
        }
    }
    public boolean isAdult(Entity entity) {
        return switch (entity) {
            case Axolotl axolotl -> axolotl.isAdult();
            case Armadillo armadillo -> armadillo.isAdult();
            case Bee bee -> bee.isAdult();
            case Cat cat -> cat.isAdult();
            case Chicken chicken -> chicken.isAdult();
            case MushroomCow mushroomCow -> mushroomCow.isAdult();
            case Cow cow -> cow.isAdult();
            case Donkey donkey -> donkey.isAdult();
            case Drowned drowned -> drowned.isAdult();
            case Fox fox -> fox.isAdult();
            case Frog frog -> frog.isAdult();
            case Goat goat -> goat.isAdult();
            case Hoglin hoglin -> hoglin.isAdult();
            case Horse horse -> horse.isAdult();
            case Husk husk -> husk.isAdult();
            case TraderLlama traderLlama -> traderLlama.isAdult();
            case Llama llama -> llama.isAdult();
            case Mule mule -> mule.isAdult();
            case Ocelot ocelot -> ocelot.isAdult();
            case Panda panda -> panda.isAdult();
            case Parrot parrot -> parrot.isAdult();
            case Pig pig -> pig.isAdult();
            case Piglin piglin -> piglin.isAdult();
            case PiglinBrute piglinBrute -> piglinBrute.isAdult();
            case PolarBear polarBear -> polarBear.isAdult();
            case Rabbit rabbit -> rabbit.isAdult();
            case Sniffer sniffer -> sniffer.isAdult();
            case Sheep sheep -> sheep.isAdult();
            case SkeletonHorse skeletonHorse -> skeletonHorse.isAdult();
            case Strider strider -> strider.isAdult();
            case Turtle turtle -> turtle.isAdult();
            case Villager villager -> villager.isAdult();
            case WanderingTrader wanderingTrader -> wanderingTrader.isAdult();
            case Wolf wolf -> wolf.isAdult();
            case Zoglin zoglin -> zoglin.isAdult();
            case ZombieVillager zombieVillager -> zombieVillager.isAdult();
            case PigZombie pigZombie -> pigZombie.isAdult();
            case Zombie zombie -> zombie.isAdult();
            case ZombieHorse zombieHorse -> zombieHorse.isAdult();
            case null, default -> true;
        };
    }
    public void setScale(Entity target, double value) {
        switch (target) {
            case Allay entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Armadillo entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case ArmorStand entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Axolotl entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Bat entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Bee entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Blaze entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Bogged entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Breeze entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Camel entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Cat entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case CaveSpider entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Chicken entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Cod entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case MushroomCow entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Cow entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Creeper entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Dolphin entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Donkey entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Drowned entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case ElderGuardian entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case EnderDragon entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Enderman entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Endermite entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Evoker entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Fox entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Frog entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Ghast entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Giant entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case GlowSquid entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Goat entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Guardian entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Hoglin entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Horse entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Husk entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Illusioner entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case IronGolem entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case TraderLlama entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Llama entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case MagmaCube entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Mule entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Ocelot entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Panda entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Parrot entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Phantom entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case PigZombie entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Pig entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Piglin entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case PiglinBrute entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Pillager entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Player entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case PolarBear entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case PufferFish entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Rabbit entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Ravager entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Salmon entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Sheep entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Shulker entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Silverfish entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Skeleton entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case SkeletonHorse entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Slime entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Sniffer entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Snowman entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Spider entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Squid entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Stray entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Strider entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Tadpole entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case TropicalFish entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Turtle entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Vex entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case ZombieVillager entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Villager entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Vindicator entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case WanderingTrader entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Warden entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Witch entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Wither entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case WitherSkeleton entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Wolf entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case Zombie entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            case ZombieHorse entity -> entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
            default -> {
            }
        };
    }
    public boolean isNPC(Entity entity) {
        return getData(entity).has(NamespacedKey.minecraft("npc"), PersistentDataType.BOOLEAN);
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
        getData(villager).set(NamespacedKey.minecraft("npc"), PersistentDataType.BOOLEAN, true);
        getMessage().send(player, "&6You created&f " + name + "&6 villager");
    }
    public boolean hasCommand(Entity entity) {
        return getData(entity).has(NamespacedKey.minecraft("command"), PersistentDataType.STRING);
    }
    public String getCommand(Entity entity) {
        return getData(entity).get(NamespacedKey.minecraft("command"), PersistentDataType.STRING);
    }
    public boolean isCommandConsole(Entity entity) {
        return getData(entity).get(NamespacedKey.minecraft("command-type"), PersistentDataType.STRING).equalsIgnoreCase("console");
    }
    public boolean isCommandPlayer(Entity entity) {
        return getData(entity).get(NamespacedKey.minecraft("command-type"), PersistentDataType.STRING).equalsIgnoreCase("player");
    }
    public boolean hasSelected(Player player) {
        if (player.getTargetEntity(3) instanceof Villager villager) {
            return isNPC(villager);
        } else {
            return false;
        }
    }
    public Villager getSelected(Player player) {
        if (player.getTargetEntity(3) instanceof Villager villager) {
            if (isNPC(villager)) {
                return villager;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    public void reload() {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.equals(EntityType.PLAYER))return;
            File file = getFile(entityType);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (file.exists()) {
                try {
                    config.load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
            } else {
                config.set("name", entityType.getName());
                config.set("hostile", false);
                config.set("chunk-limit", -1);
                config.set("disable-spawn", false);
                config.set("disable-block-damage", false);
                config.set("disable-block-form", false);
                config.set("disable-block-change", false);
                config.set("disable-block-interact.FARMLAND", true);
                config.set("disable-block-interact.TURTLE_EGG", true);
                config.set("disable-block-interact.SNIFFER_EGG", true);
                config.set("disable-target.VILLAGER", false);
                config.set("disable-damage.VILLAGER", false);
                config.set("disable-damage.ITEM", true);
                config.set("carry.enable", false);
                config.set("carry.weight.adult", 1);
                config.set("carry.weight.baby", 0);
                try {
                    config.save(file);
                } catch (IOException e) {
                    getMessage().sendLog(Level.WARNING, e.getMessage());
                }
            }
        }
    }
}