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
    public File getFile() {
        return new File(getDataFolder(), "entities.yml");
    }
    public boolean exist() {
        return getFile().exists();
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(getFile());
    }
    public boolean isEnable(Entity entity) {
        return getConfig().isConfigurationSection(entity.getType().toString());
    }
    public String getName(Entity entity) {
        return getConfig().getString(entity.getType() + ".name");
    }
    public String getName(String entityString) {
        return getConfig().getString(entityString.toUpperCase() + ".name");
    }
    public int getChunkLimit(Entity entity) {
        return getConfig().getInt(entity.getType() + ".chunk-limit");
    }
    public boolean disableSpawn(Entity entity) {
        return getConfig().getBoolean(entity.getType() + ".disable-spawn");
    }
    public boolean disableBlockForm(Entity entity) {
        return getConfig().getBoolean(entity.getType() + ".disable-block-form");
    }
    public boolean disableBlockDamage(Entity entity) {
        return getConfig().getBoolean(entity.getType() + ".disable-block-damage");
    }
    public boolean disableBlockChange(Entity entity) {
        return getConfig().getBoolean(entity.getType() + ".disable-block-change");
    }
    public boolean disableBlockInteract(Entity entity, Block block) {
        return getConfig().getBoolean(entity.getType() + ".disable-block-interact." + block.getType());
    }
    public boolean disableTarget(Entity entity, Entity target) {
        return getConfig().getBoolean(entity.getType() + ".disable-target." + target.getType());
    }
    public boolean disableDamage(Entity damager, Entity entity) {
        return getConfig().getBoolean(damager.getType() + ".disable-damage." + entity.getType());
    }
    public boolean isHostile(Entity entity) {
        return getConfig().getBoolean(entity.getType() + ".hostile");
    }
    private PersistentDataContainer getData(Entity entity) {
        return entity.getPersistentDataContainer();
    }
    public boolean isEnableCarry(Entity entity) {
        return getConfig().getBoolean(entity.getType() + ".carry.enable");
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
    public void addMount(Player player, Entity entity) {
        getData(entity).set(NamespacedKey.minecraft("mount"), PersistentDataType.STRING, player.getUniqueId().toString());
        getData(player).set(NamespacedKey.minecraft("passenger"), PersistentDataType.STRING, entity.getUniqueId().toString());
        player.addPassenger(entity);
        setScale(entity, 0.5);
        player.hideEntity(plugin, entity);
    }
    public void removeMount(Player player, Entity entity) {
        player.showEntity(plugin, entity);
        setScale(entity, 1);
        entity.teleport(player.getLocation());
        getData(entity).remove(NamespacedKey.minecraft("mount"));
        getData(player).remove(NamespacedKey.minecraft("passenger"));
    }
    public void removeMount(Player player, Entity entity, Block block) {
        player.showEntity(plugin, entity);
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
    public boolean hasPassenger(Player player) {
        return player.getPassenger() != null;
    }
    public Entity getPassenger(Player player) {
        if (hasPassenger(player)) {
            return player.getPassenger();
        } else {
            return null;
        }
    }
    public void addEffects(Player player) {
        if (isAdult(getPassenger(player))) {
            if (getConfig().getInt(getPassenger(player).getType() + ".carry.weight.adult") > 0) {
                if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, getConfig().getInt(getPassenger(player).getType() + ".carry.weight.adult") - 1));
                }
                if (!player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, getConfig().getInt(getPassenger(player).getType() + ".carry.weight.adult") - 1));
                }
            }
        } else if (getConfig().getInt("entities." + getPassenger(player).getType() + ".carry.weight.baby") > 0) {
            if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, getConfig().getInt(getPassenger(player).getType() + ".carry.weight.baby") - 1));
            }
            if (!player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, getConfig().getInt(getPassenger(player).getType() + ".carry.weight.baby") - 1));
            }
        }
        if (getPassenger(player).getType().equals(EntityType.PLAYER)) {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getPassenger(player).getName());
        } else if (getPassenger(player).getCustomName() == null) {
            getMessage().sendActionBar(player, "&6You are carrying&f " + getConfig().getString(getPassenger(player).getType() + ".name"));
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
    public void reload() {
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (exist()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            config.set("ALLAY.name", "Allay");
            config.set("ALLAY.hostile", false);
            config.set("ALLAY.chunk-limit", 8);
            config.set("ALLAY.disable-spawn", false);
            config.set("ALLAY.disable-block-damage", true);
            config.set("ALLAY.disable-block-form", true);
            config.set("ALLAY.disable-block-change", true);
            config.set("ALLAY.disable-block-interact.FARMLAND", true);
            config.set("ALLAY.disable-block-interact.TURTLE_EGG", true);
            config.set("ALLAY.disable-block-interact.SNIFFER_EGG", true);
            config.set("ALLAY.disable-target.VILLAGER", false);
            config.set("ALLAY.disable-damage.VILLAGER", false);
            config.set("ALLAY.disable-damage.ITEM", true);
            config.set("ALLAY.carry.enable", true);
            config.set("ALLAY.carry.weight.adult", 1);
            config.set("ALLAY.carry.weight.baby", 0);

            config.set("AREA_CLOUD_EFFECT.name", "Area Cloud Effect");
            config.set("AREA_CLOUD_EFFECT.hostile", false);
            config.set("AREA_CLOUD_EFFECT.chunk-limit", -1);
            config.set("AREA_CLOUD_EFFECT.disable-spawn", false);
            config.set("AREA_CLOUD_EFFECT.disable-block-damage", false);
            config.set("AREA_CLOUD_EFFECT.disable-block-form", false);
            config.set("AREA_CLOUD_EFFECT.disable-block-change", false);

            config.set("ARMADILLO.name", "Armadillo");
            config.set("ARMADILLO.hostile", false);
            config.set("ARMADILLO.chunk-limit", 8);
            config.set("ARMADILLO.disable-spawn", false);
            config.set("ARMADILLO.disable-block-damage", true);
            config.set("ARMADILLO.disable-block-form", true);
            config.set("ARMADILLO.disable-block-change", true);
            config.set("ARMADILLO.disable-block-interact.FARMLAND", true);
            config.set("ARMADILLO.disable-block-interact.TURTLE_EGG", true);
            config.set("ARMADILLO.disable-block-interact.SNIFFER_EGG", true);
            config.set("ARMADILLO.disable-target.VILLAGER", false);
            config.set("ARMADILLO.disable-damage.VILLAGER", false);
            config.set("ARMADILLO.disable-damage.ITEM", true);
            config.set("ARMADILLO.carry.enable", true);
            config.set("ARMADILLO.carry.weight.adult", 1);
            config.set("ARMADILLO.carry.weight.baby", 0);

            config.set("ARMOR_STAND.name", "Armor Stand");
            config.set("ARMOR_STAND.hostile", false);
            config.set("ARMOR_STAND.chunk-limit", -1);
            config.set("ARMOR_STAND.disable-spawn", false);
            config.set("ARMOR_STAND.disable-block-damage", false);
            config.set("ARMOR_STAND.disable-block-form", false);
            config.set("ARMOR_STAND.disable-block-change", false);

            config.set("ARROW.name", "Arrow");
            config.set("ARROW.hostile", true);
            config.set("ARROW.chunk-limit", -1);
            config.set("ARROW.disable-spawn", false);
            config.set("ARROW.disable-block-damage", false);
            config.set("ARROW.disable-block-form", false);
            config.set("ARROW.disable-block-change", false);

            config.set("AXOLOTL.name", "Axolotl");
            config.set("AXOLOTL.hostile", false);
            config.set("AXOLOTL.chunk-limit", 8);
            config.set("AXOLOTL.disable-spawn", false);
            config.set("AXOLOTL.disable-block-damage", true);
            config.set("AXOLOTL.disable-block-form", true);
            config.set("AXOLOTL.disable-block-change", true);
            config.set("AXOLOTL.disable-block-interact.FARMLAND", true);
            config.set("AXOLOTL.disable-block-interact.TURTLE_EGG", true);
            config.set("AXOLOTL.disable-block-interact.SNIFFER_EGG", true);
            config.set("AXOLOTL.disable-target.VILLAGER", false);
            config.set("AXOLOTL.disable-damage.VILLAGER", false);
            config.set("AXOLOTL.disable-damage.ITEM", true);
            config.set("AXOLOTL.carry.enable", true);
            config.set("AXOLOTL.carry.weight.adult", 1);
            config.set("AXOLOTL.carry.weight.baby", 0);

            config.set("BAT.name", "Bat");
            config.set("BAT.hostile", false);
            config.set("BAT.chunk-limit", 8);
            config.set("BAT.disable-spawn", false);
            config.set("BAT.disable-block-damage", true);
            config.set("BAT.disable-block-form", true);
            config.set("BAT.disable-block-change", true);
            config.set("BAT.disable-block-interact.FARMLAND", true);
            config.set("BAT.disable-block-interact.TURTLE_EGG", true);
            config.set("BAT.disable-block-interact.SNIFFER_EGG", true);
            config.set("BAT.disable-target.VILLAGER", false);
            config.set("BAT.disable-damage.VILLAGER", false);
            config.set("BAT.disable-damage.ITEM", true);
            config.set("BAT.carry.enable", true);
            config.set("BAT.carry.weight.adult", 1);
            config.set("BAT.carry.weight.baby", 0);

            config.set("BEE.name", "Bee");
            config.set("BEE.hostile", false);
            config.set("BEE.chunk-limit", 8);
            config.set("BEE.disable-spawn", false);
            config.set("BEE.disable-block-damage", true);
            config.set("BEE.disable-block-form", true);
            config.set("BEE.disable-block-change", true);
            config.set("BEE.disable-block-interact.FARMLAND", true);
            config.set("BEE.disable-block-interact.TURTLE_EGG", true);
            config.set("BEE.disable-block-interact.SNIFFER_EGG", true);
            config.set("BEE.disable-target.VILLAGER", false);
            config.set("BEE.disable-damage.VILLAGER", false);
            config.set("BEE.disable-damage.ITEM", true);
            config.set("BEE.carry.enable", true);
            config.set("BEE.carry.weight.adult", 1);
            config.set("BEE.carry.weight.baby", 0);

            config.set("BLAZE.name", "Blaze");
            config.set("BLAZE.hostile", true);
            config.set("BLAZE.chunk-limit", 8);
            config.set("BLAZE.disable-spawn", false);
            config.set("BLAZE.disable-block-damage", true);
            config.set("BLAZE.disable-block-form", true);
            config.set("BLAZE.disable-block-change", true);
            config.set("BLAZE.disable-block-interact.FARMLAND", true);
            config.set("BLAZE.disable-block-interact.TURTLE_EGG", true);
            config.set("BLAZE.disable-block-interact.SNIFFER_EGG", true);
            config.set("BLAZE.disable-target.VILLAGER", false);
            config.set("BLAZE.disable-damage.VILLAGER", false);
            config.set("BLAZE.disable-damage.ITEM", true);
            config.set("BLAZE.carry.enable", true);
            config.set("BLAZE.carry.weight.adult", 1);
            config.set("BLAZE.carry.weight.baby", 0);

            config.set("BLOCK_DISPLAY.name", "Block Display");
            config.set("BLOCK_DISPLAY.hostile", false);
            config.set("BLOCK_DISPLAY.chunk-limit", -1);
            config.set("BLOCK_DISPLAY.disable-spawn", false);
            config.set("BLOCK_DISPLAY.disable-block-damage", false);
            config.set("BLOCK_DISPLAY.disable-block-form", false);
            config.set("BLOCK_DISPLAY.disable-block-change", false);

            config.set("BOAT.name", "Boat");
            config.set("BOAT.hostile", false);
            config.set("BOAT.chunk-limit", 8);
            config.set("BOAT.disable-spawn", false);
            config.set("BOAT.disable-block-damage", false);
            config.set("BOAT.disable-block-form", false);
            config.set("BOAT.disable-block-change", false);
            config.set("BOAT.disable-block-interact.FARMLAND", true);
            config.set("BOAT.disable-block-interact.TURTLE_EGG", true);
            config.set("BOAT.disable-block-interact.SNIFFER_EGG", true);
            config.set("BOAT.disable-target.VILLAGER", false);
            config.set("BOAT.disable-damage.VILLAGER", false);
            config.set("BOAT.disable-damage.ITEM", true);

            config.set("BOGGED.name", "Bogged");
            config.set("BOGGED.hostile", true);
            config.set("BOGGED.chunk-limit", 8);
            config.set("BOGGED.disable-spawn", false);
            config.set("BOGGED.disable-block-damage", true);
            config.set("BOGGED.disable-block-form", true);
            config.set("BOGGED.disable-block-change", true);
            config.set("BOGGED.disable-block-interact.FARMLAND", true);
            config.set("BOGGED.disable-block-interact.TURTLE_EGG", true);
            config.set("BOGGED.disable-block-interact.SNIFFER_EGG", true);
            config.set("BOGGED.disable-target.VILLAGER", false);
            config.set("BOGGED.disable-damage.VILLAGER", false);
            config.set("BOGGED.disable-damage.ITEM", true);
            config.set("BOGGED.carry.enable", true);
            config.set("BOGGED.carry.weight.adult", 1);
            config.set("BOGGED.carry.weight.baby", 0);

            config.set("BREEZE.name", "Breeze");
            config.set("BREEZE.hostile", true);
            config.set("BREEZE.chunk-limit", 8);
            config.set("BREEZE.disable-spawn", false);
            config.set("BREEZE.disable-block-damage", true);
            config.set("BREEZE.disable-block-form", true);
            config.set("BREEZE.disable-block-change", true);
            config.set("BREEZE.disable-block-interact.FARMLAND", true);
            config.set("BREEZE.disable-block-interact.TURTLE_EGG", true);
            config.set("BREEZE.disable-block-interact.SNIFFER_EGG", true);
            config.set("BREEZE.disable-target.VILLAGER", false);
            config.set("BREEZE.disable-damage.VILLAGER", false);
            config.set("BREEZE.disable-damage.ITEM", true);
            config.set("BREEZE.carry.enable", true);
            config.set("BREEZE.carry.weight.adult", 1);
            config.set("BREEZE.carry.weight.baby", 0);

            config.set("BREEZE_WIND_CHARGE.name", "Breeze Wind Charge");
            config.set("BREEZE_WIND_CHARGE.hostile", true);
            config.set("BREEZE_WIND_CHARGE.chunk-limit", -1);
            config.set("BREEZE_WIND_CHARGE.disable-spawn", false);
            config.set("BREEZE_WIND_CHARGE.disable-block-damage", true);
            config.set("BREEZE_WIND_CHARGE.disable-block-form", true);
            config.set("BREEZE_WIND_CHARGE.disable-block-change", true);
            config.set("BREEZE_WIND_CHARGE.disable-block-interact.FARMLAND", true);
            config.set("BREEZE_WIND_CHARGE.disable-block-interact.TURTLE_EGG", true);
            config.set("BREEZE_WIND_CHARGE.disable-block-interact.SNIFFER_EGG", true);
            config.set("BREEZE_WIND_CHARGE.disable-target.VILLAGER", false);
            config.set("BREEZE_WIND_CHARGE.disable-damage.VILLAGER", true);
            config.set("BREEZE_WIND_CHARGE.disable-damage.ITEM", true);

            config.set("CAMEL.name", "Camel");
            config.set("CAMEL.hostile", false);
            config.set("CAMEL.chunk-limit", 8);
            config.set("CAMEL.disable-spawn", false);
            config.set("CAMEL.disable-block-damage", true);
            config.set("CAMEL.disable-block-form", true);
            config.set("CAMEL.disable-block-change", true);
            config.set("CAMEL.disable-block-interact.FARMLAND", true);
            config.set("CAMEL.disable-block-interact.TURTLE_EGG", true);
            config.set("CAMEL.disable-block-interact.SNIFFER_EGG", true);
            config.set("CAMEL.disable-target.VILLAGER", false);
            config.set("CAMEL.disable-damage.VILLAGER", false);
            config.set("CAMEL.disable-damage.ITEM", true);
            config.set("CAMEL.carry.enable", true);
            config.set("CAMEL.carry.weight.adult", 1);
            config.set("CAMEL.carry.weight.baby", 0);

            config.set("CAT.name", "Cat");
            config.set("CAT.hostile", false);
            config.set("CAT.chunk-limit", 8);
            config.set("CAT.disable-spawn", false);
            config.set("CAT.disable-block-damage", true);
            config.set("CAT.disable-block-form", true);
            config.set("CAT.disable-block-change", true);
            config.set("CAT.disable-block-interact.FARMLAND", true);
            config.set("CAT.disable-block-interact.TURTLE_EGG", true);
            config.set("CAT.disable-block-interact.SNIFFER_EGG", true);
            config.set("CAT.disable-target.VILLAGER", false);
            config.set("CAT.disable-damage.VILLAGER", false);
            config.set("CAT.disable-damage.ITEM", true);
            config.set("CAT.carry.enable", true);
            config.set("CAT.carry.weight.adult", 1);
            config.set("CAT.carry.weight.baby", 0);

            config.set("CAVE_SPIDER.name", "Cave Spider");
            config.set("CAVE_SPIDER.hostile", true);
            config.set("CAVE_SPIDER.chunk-limit", 8);
            config.set("CAVE_SPIDER.disable-spawn", false);
            config.set("CAVE_SPIDER.disable-block-damage", true);
            config.set("CAVE_SPIDER.disable-block-form", true);
            config.set("CAVE_SPIDER.disable-block-change", true);
            config.set("CAVE_SPIDER.disable-block-interact.FARMLAND", true);
            config.set("CAVE_SPIDER.disable-block-interact.TURTLE_EGG", true);
            config.set("CAVE_SPIDER.disable-block-interact.SNIFFER_EGG", true);
            config.set("CAVE_SPIDER.disable-target.VILLAGER", false);
            config.set("CAVE_SPIDER.disable-damage.VILLAGER", false);
            config.set("CAVE_SPIDER.disable-damage.ITEM", true);
            config.set("CAVE_SPIDER.carry.enable", true);
            config.set("CAVE_SPIDER.carry.weight.adult", 1);
            config.set("CAVE_SPIDER.carry.weight.baby", 0);

            config.set("CHEST_BOAT.name", "Chest Boat");
            config.set("CHEST_BOAT.hostile", false);
            config.set("CHEST_BOAT.chunk-limit", 8);
            config.set("CHEST_BOAT.disable-spawn", false);
            config.set("CHEST_BOAT.disable-block-damage", true);
            config.set("CHEST_BOAT.disable-block-form", true);
            config.set("CHEST_BOAT.disable-block-change", true);
            config.set("CHEST_BOAT.disable-block-interact.FARMLAND", true);
            config.set("CHEST_BOAT.disable-block-interact.TURTLE_EGG", true);
            config.set("CHEST_BOAT.disable-block-interact.SNIFFER_EGG", true);
            config.set("CHEST_BOAT.disable-target.VILLAGER", false);
            config.set("CHEST_BOAT.disable-damage.VILLAGER", false);
            config.set("CHEST_BOAT.disable-damage.ITEM", true);

            config.set("CHEST_MINECART.name", "Chest Minecart");
            config.set("CHEST_MINECART.hostile", false);
            config.set("CHEST_MINECART.chunk-limit", 8);
            config.set("CHEST_MINECART.disable-spawn", false);
            config.set("CHEST_MINECART.disable-block-damage", true);
            config.set("CHEST_MINECART.disable-block-form", true);
            config.set("CHEST_MINECART.disable-block-change", true);
            config.set("CHEST_MINECART.disable-block-interact.FARMLAND", true);
            config.set("CHEST_MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("CHEST_MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("CHEST_MINECART.disable-target.VILLAGER", false);
            config.set("CHEST_MINECART.disable-damage.VILLAGER", false);
            config.set("CHEST_MINECART.disable-damage.ITEM", true);

            config.set("CHICKEN.name", "Chicken");
            config.set("CHICKEN.hostile", false);
            config.set("CHICKEN.chunk-limit", 8);
            config.set("CHICKEN.disable-spawn", false);
            config.set("CHICKEN.disable-block-damage", true);
            config.set("CHICKEN.disable-block-form", true);
            config.set("CHICKEN.disable-block-change", true);
            config.set("CHICKEN.disable-block-interact.FARMLAND", true);
            config.set("CHICKEN.disable-block-interact.TURTLE_EGG", true);
            config.set("CHICKEN.disable-block-interact.SNIFFER_EGG", true);
            config.set("CHICKEN.disable-target.VILLAGER", false);
            config.set("CHICKEN.disable-damage.VILLAGER", false);
            config.set("CHICKEN.disable-damage.ITEM", true);
            config.set("CHICKEN.carry.enable", true);
            config.set("CHICKEN.carry.weight.adult", 1);
            config.set("CHICKEN.carry.weight.baby", 0);

            config.set("COD.name", "Cod");
            config.set("COD.hostile", false);
            config.set("COD.chunk-limit", 8);
            config.set("COD.disable-spawn", false);
            config.set("COD.disable-block-damage", true);
            config.set("COD.disable-block-form", true);
            config.set("COD.disable-block-change", true);
            config.set("COD.disable-block-interact.FARMLAND", true);
            config.set("COD.disable-block-interact.TURTLE_EGG", true);
            config.set("COD.disable-block-interact.SNIFFER_EGG", true);
            config.set("COD.disable-target.VILLAGER", false);
            config.set("COD.disable-damage.VILLAGER", false);
            config.set("COD.disable-damage.ITEM", true);

            config.set("COMMAND_BLOCK_MINECART.name", "Command Block Minecart");
            config.set("COMMAND_BLOCK_MINECART.hostile", false);
            config.set("COMMAND_BLOCK_MINECART.chunk-limit", -1);
            config.set("COMMAND_BLOCK_MINECART.disable-spawn", false);
            config.set("COMMAND_BLOCK_MINECART.disable-block-damage", true);
            config.set("COMMAND_BLOCK_MINECART.disable-block-form", true);
            config.set("COMMAND_BLOCK_MINECART.disable-block-change", true);
            config.set("COMMAND_BLOCK_MINECART.disable-block-interact.FARMLAND", true);
            config.set("COMMAND_BLOCK_MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("COMMAND_BLOCK_MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("COMMAND_BLOCK_MINECART.disable-target.VILLAGER", false);
            config.set("COMMAND_BLOCK_MINECART.disable-damage.VILLAGER", false);
            config.set("COMMAND_BLOCK_MINECART.disable-damage.ITEM", true);

            config.set("COW.name", "Cow");
            config.set("COW.hostile", false);
            config.set("COW.chunk-limit", 8);
            config.set("COW.disable-spawn", false);
            config.set("COW.disable-block-damage", true);
            config.set("COW.disable-block-form", true);
            config.set("COW.disable-block-change", true);
            config.set("COW.disable-block-interact.FARMLAND", true);
            config.set("COW.disable-block-interact.TURTLE_EGG", true);
            config.set("COW.disable-block-interact.SNIFFER_EGG", true);
            config.set("COW.disable-target.VILLAGER", false);
            config.set("COW.disable-damage.VILLAGER", false);
            config.set("COW.disable-damage.ITEM", true);
            config.set("COW.carry.enable", true);
            config.set("COW.carry.weight.adult", 1);
            config.set("COW.carry.weight.baby", 0);

            config.set("CREEPER.name", "Creeper");
            config.set("CREEPER.hostile", true);
            config.set("CREEPER.chunk-limit", 8);
            config.set("CREEPER.disable-spawn", false);
            config.set("CREEPER.disable-block-damage", true);
            config.set("CREEPER.disable-block-form", true);
            config.set("CREEPER.disable-block-change", true);
            config.set("CREEPER.disable-block-interact.FARMLAND", true);
            config.set("CREEPER.disable-block-interact.TURTLE_EGG", true);
            config.set("CREEPER.disable-block-interact.SNIFFER_EGG", true);
            config.set("CREEPER.disable-target.VILLAGER", false);
            config.set("CREEPER.disable-damage.VILLAGER", false);
            config.set("CREEPER.disable-damage.ITEM", true);

            config.set("DOLPHIN.name", "Dolphin");
            config.set("DOLPHIN.hostile", false);
            config.set("DOLPHIN.chunk-limit", 8);
            config.set("DOLPHIN.disable-spawn", false);
            config.set("DOLPHIN.disable-block-damage", true);
            config.set("DOLPHIN.disable-block-form", true);
            config.set("DOLPHIN.disable-block-change", true);
            config.set("DOLPHIN.disable-block-interact.FARMLAND", true);
            config.set("DOLPHIN.disable-block-interact.TURTLE_EGG", true);
            config.set("DOLPHIN.disable-block-interact.SNIFFER_EGG", true);
            config.set("DOLPHIN.disable-target.VILLAGER", false);
            config.set("DOLPHIN.disable-damage.VILLAGER", false);
            config.set("DOLPHIN.disable-damage.ITEM", true);
            config.set("DOLPHIN.carry.enable", true);
            config.set("DOLPHIN.carry.weight.adult", 1);
            config.set("DOLPHIN.carry.weight.baby", 0);

            config.set("DONKEY.name", "Donkey");
            config.set("DONKEY.hostile", false);
            config.set("DONKEY.chunk-limit", 8);
            config.set("DONKEY.disable-spawn", false);
            config.set("DONKEY.disable-block-damage", true);
            config.set("DONKEY.disable-block-form", true);
            config.set("DONKEY.disable-block-change", true);
            config.set("DONKEY.disable-block-interact.FARMLAND", true);
            config.set("DONKEY.disable-block-interact.TURTLE_EGG", true);
            config.set("DONKEY.disable-block-interact.SNIFFER_EGG", true);
            config.set("DONKEY.disable-target.VILLAGER", false);
            config.set("DONKEY.disable-damage.VILLAGER", false);
            config.set("DONKEY.disable-damage.ITEM", true);
            config.set("DONKEY.carry.enable", true);
            config.set("DONKEY.carry.weight.adult", 1);
            config.set("DONKEY.carry.weight.baby", 0);

            config.set("DRAGON_FIREBALL.name", "Dragon Fireball");
            config.set("DRAGON_FIREBALL.hostile", true);
            config.set("DRAGON_FIREBALL.chunk-limit", -1);
            config.set("DRAGON_FIREBALL.disable-spawn", false);
            config.set("DRAGON_FIREBALL.disable-block-damage", true);
            config.set("DRAGON_FIREBALL.disable-block-form", true);
            config.set("DRAGON_FIREBALL.disable-block-change", true);
            config.set("DRAGON_FIREBALL.disable-block-interact.FARMLAND", true);
            config.set("DRAGON_FIREBALL.disable-block-interact.TURTLE_EGG", true);
            config.set("DRAGON_FIREBALL.disable-block-interact.SNIFFER_EGG", true);
            config.set("DRAGON_FIREBALL.disable-target.VILLAGER", false);
            config.set("DRAGON_FIREBALL.disable-damage.VILLAGER", false);
            config.set("DRAGON_FIREBALL.disable-damage.ITEM", true);

            config.set("DROWNED.name", "Drowned");
            config.set("DROWNED.hostile", true);
            config.set("DROWNED.chunk-limit", 8);
            config.set("DROWNED.disable-spawn", false);
            config.set("DROWNED.disable-block-damage", true);
            config.set("DROWNED.disable-block-form", true);
            config.set("DROWNED.disable-block-change", true);
            config.set("DROWNED.disable-block-interact.FARMLAND", true);
            config.set("DROWNED.disable-block-interact.TURTLE_EGG", true);
            config.set("DROWNED.disable-block-interact.SNIFFER_EGG", true);
            config.set("DROWNED.disable-target.VILLAGER", false);
            config.set("DROWNED.disable-damage.VILLAGER", false);
            config.set("DROWNED.disable-damage.ITEM", true);
            config.set("DROWNED.carry.enable", true);
            config.set("DROWNED.carry.weight.adult", 1);
            config.set("DROWNED.carry.weight.baby", 0);

            config.set("EGG.name", "Egg");
            config.set("EGG.hostile", false);
            config.set("EGG.chunk-limit", 8);
            config.set("EGG.disable-spawn", false);
            config.set("EGG.disable-block-damage", true);
            config.set("EGG.disable-block-form", true);
            config.set("EGG.disable-block-change", true);
            config.set("EGG.disable-block-interact.FARMLAND", true);
            config.set("EGG.disable-block-interact.TURTLE_EGG", true);
            config.set("EGG.disable-block-interact.SNIFFER_EGG", true);
            config.set("EGG.disable-target.VILLAGER", false);
            config.set("EGG.disable-damage.VILLAGER", false);
            config.set("EGG.disable-damage.ITEM", true);

            config.set("ELDER_GUARDIAN.name", "Elder Guardian");
            config.set("ELDER_GUARDIAN.hostile", true);
            config.set("ELDER_GUARDIAN.chunk-limit", 8);
            config.set("ELDER_GUARDIAN.disable-spawn", false);
            config.set("ELDER_GUARDIAN.disable-block-damage", true);
            config.set("ELDER_GUARDIAN.disable-block-form", true);
            config.set("ELDER_GUARDIAN.disable-block-change", true);
            config.set("ELDER_GUARDIAN.disable-block-interact.FARMLAND", true);
            config.set("ELDER_GUARDIAN.disable-block-interact.TURTLE_EGG", true);
            config.set("ELDER_GUARDIAN.disable-block-interact.SNIFFER_EGG", true);
            config.set("ELDER_GUARDIAN.disable-target.VILLAGER", false);
            config.set("ELDER_GUARDIAN.disable-damage.VILLAGER", false);
            config.set("ELDER_GUARDIAN.disable-damage.ITEM", true);
            config.set("ELDER_GUARDIAN.carry.enable", true);
            config.set("ELDER_GUARDIAN.carry.weight.adult", 1);
            config.set("ELDER_GUARDIAN.carry.weight.baby", 0);

            config.set("END_CRYSTAL.name", "End Crystal");
            config.set("END_CRYSTAL.hostile", true);
            config.set("END_CRYSTAL.chunk-limit", 8);
            config.set("END_CRYSTAL.disable-spawn", false);
            config.set("END_CRYSTAL.disable-block-damage", true);
            config.set("END_CRYSTAL.disable-block-form", true);
            config.set("END_CRYSTAL.disable-block-change", true);
            config.set("END_CRYSTAL.disable-block-interact.FARMLAND", true);
            config.set("END_CRYSTAL.disable-block-interact.TURTLE_EGG", true);
            config.set("END_CRYSTAL.disable-block-interact.SNIFFER_EGG", true);
            config.set("END_CRYSTAL.disable-target.VILLAGER", false);
            config.set("END_CRYSTAL.disable-damage.VILLAGER", false);
            config.set("END_CRYSTAL.disable-damage.ITEM", true);

            config.set("ENDER_DRAGON.name", "Ender Dragon");
            config.set("ENDER_DRAGON.hostile", true);
            config.set("ENDER_DRAGON.chunk-limit", 4);
            config.set("ENDER_DRAGON.disable-spawn", false);
            config.set("ENDER_DRAGON.disable-block-damage", true);
            config.set("ENDER_DRAGON.disable-block-form", true);
            config.set("ENDER_DRAGON.disable-block-change", true);
            config.set("ENDER_DRAGON.disable-block-interact.FARMLAND", true);
            config.set("ENDER_DRAGON.disable-block-interact.TURTLE_EGG", true);
            config.set("ENDER_DRAGON.disable-block-interact.SNIFFER_EGG", true);
            config.set("ENDER_DRAGON.disable-target.VILLAGER", false);
            config.set("ENDER_DRAGON.disable-damage.VILLAGER", false);
            config.set("ENDER_DRAGON.disable-damage.ITEM", true);
            config.set("ENDER_DRAGON.carry.enable", true);
            config.set("ENDER_DRAGON.carry.weight.adult", 1);
            config.set("ENDER_DRAGON.carry.weight.baby", 0);

            config.set("ENDER_PEARL.name", "Ender Pearl");
            config.set("ENDER_PEARL.hostile", false);
            config.set("ENDER_PEARL.chunk-limit", -1);
            config.set("ENDER_PEARL.disable-spawn", false);
            config.set("ENDER_PEARL.disable-block-damage", true);
            config.set("ENDER_PEARL.disable-block-form", true);
            config.set("ENDER_PEARL.disable-block-change", true);
            config.set("ENDER_PEARL.disable-block-interact.FARMLAND", true);
            config.set("ENDER_PEARL.disable-block-interact.TURTLE_EGG", true);
            config.set("ENDER_PEARL.disable-block-interact.SNIFFER_EGG", true);
            config.set("ENDER_PEARL.disable-target.VILLAGER", false);
            config.set("ENDER_PEARL.disable-damage.VILLAGER", false);
            config.set("ENDER_PEARL.disable-damage.ITEM", true);

            config.set("ENDERMAN.name", "Enderman");
            config.set("ENDERMAN.hostile", true);
            config.set("ENDERMAN.chunk-limit", 8);
            config.set("ENDERMAN.disable-spawn", false);
            config.set("ENDERMAN.disable-block-damage", true);
            config.set("ENDERMAN.disable-block-form", true);
            config.set("ENDERMAN.disable-block-change", true);
            config.set("ENDERMAN.disable-block-interact.FARMLAND", true);
            config.set("ENDERMAN.disable-block-interact.TURTLE_EGG", true);
            config.set("ENDERMAN.disable-block-interact.SNIFFER_EGG", true);
            config.set("ENDERMAN.disable-target.VILLAGER", false);
            config.set("ENDERMAN.disable-damage.VILLAGER", false);
            config.set("ENDERMAN.disable-damage.ITEM", true);
            config.set("ENDERMAN.carry.enable", true);
            config.set("ENDERMAN.carry.weight.adult", 1);
            config.set("ENDERMAN.carry.weight.baby", 0);

            config.set("ENDERMITE.name", "Endermite");
            config.set("ENDERMITE.hostile", true);
            config.set("ENDERMITE.chunk-limit", 16);
            config.set("ENDERMITE.disable-spawn", false);
            config.set("ENDERMITE.disable-block-damage", true);
            config.set("ENDERMITE.disable-block-form", true);
            config.set("ENDERMITE.disable-block-change", true);
            config.set("ENDERMITE.disable-block-interact.FARMLAND", true);
            config.set("ENDERMITE.disable-block-interact.TURTLE_EGG", true);
            config.set("ENDERMITE.disable-block-interact.SNIFFER_EGG", true);
            config.set("ENDERMITE.disable-target.VILLAGER", false);
            config.set("ENDERMITE.disable-damage.VILLAGER", false);
            config.set("ENDERMITE.disable-damage.ITEM", true);
            config.set("ENDERMITE.carry.enable", true);
            config.set("ENDERMITE.carry.weight.adult", 1);
            config.set("ENDERMITE.carry.weight.baby", 0);

            config.set("EVOKER.name", "Evoker");
            config.set("EVOKER.hostile", true);
            config.set("EVOKER.chunk-limit", 8);
            config.set("EVOKER.disable-spawn", false);
            config.set("EVOKER.disable-block-damage", true);
            config.set("EVOKER.disable-block-form", true);
            config.set("EVOKER.disable-block-change", true);
            config.set("EVOKER.disable-block-interact.FARMLAND", true);
            config.set("EVOKER.disable-block-interact.TURTLE_EGG", true);
            config.set("EVOKER.disable-block-interact.SNIFFER_EGG", true);
            config.set("EVOKER.disable-target.VILLAGER", false);
            config.set("EVOKER.disable-damage.VILLAGER", false);
            config.set("EVOKER.disable-damage.ITEM", true);
            config.set("EVOKER.carry.enable", true);
            config.set("EVOKER.carry.weight.adult", 1);
            config.set("EVOKER.carry.weight.baby", 0);

            config.set("EVOKER_FANGS.name", "Evoker Fangs");
            config.set("EVOKER_FANGS.hostile", true);
            config.set("EVOKER_FANGS.chunk-limit", -1);
            config.set("EVOKER_FANGS.disable-spawn", false);
            config.set("EVOKER_FANGS.disable-block-damage", true);
            config.set("EVOKER_FANGS.disable-block-form", true);
            config.set("EVOKER_FANGS.disable-block-change", true);
            config.set("EVOKER_FANGS.disable-block-interact.FARMLAND", true);
            config.set("EVOKER_FANGS.disable-block-interact.TURTLE_EGG", true);
            config.set("EVOKER_FANGS.disable-block-interact.SNIFFER_EGG", true);
            config.set("EVOKER_FANGS.disable-target.VILLAGER", false);
            config.set("EVOKER_FANGS.disable-damage.VILLAGER", false);
            config.set("EVOKER_FANGS.disable-damage.ITEM", true);

            config.set("EXPERIENCE_BOTTLE.name", "Experience Bottle");
            config.set("EXPERIENCE_BOTTLE.hostile", true);
            config.set("EXPERIENCE_BOTTLE.chunk-limit", -1);
            config.set("EXPERIENCE_BOTTLE.disable-spawn", false);
            config.set("EXPERIENCE_BOTTLE.disable-block-damage", true);
            config.set("EXPERIENCE_BOTTLE.disable-block-form", true);
            config.set("EXPERIENCE_BOTTLE.disable-block-change", true);
            config.set("EXPERIENCE_BOTTLE.disable-block-interact.FARMLAND", true);
            config.set("EXPERIENCE_BOTTLE.disable-block-interact.TURTLE_EGG", true);
            config.set("EXPERIENCE_BOTTLE.disable-block-interact.SNIFFER_EGG", true);
            config.set("EXPERIENCE_BOTTLE.disable-target.VILLAGER", false);
            config.set("EXPERIENCE_BOTTLE.disable-damage.VILLAGER", false);
            config.set("EXPERIENCE_BOTTLE.disable-damage.ITEM", true);

            config.set("EXPERIENCE_ORB.name", "Experience Orb");
            config.set("EXPERIENCE_ORB.hostile", true);
            config.set("EXPERIENCE_ORB.chunk-limit", -1);
            config.set("EXPERIENCE_ORB.disable-spawn", false);
            config.set("EXPERIENCE_ORB.disable-block-damage", true);
            config.set("EXPERIENCE_ORB.disable-block-form", true);
            config.set("EXPERIENCE_ORB.disable-block-change", true);
            config.set("EXPERIENCE_ORB.disable-block-interact.FARMLAND", true);
            config.set("EXPERIENCE_ORB.disable-block-interact.TURTLE_EGG", true);
            config.set("EXPERIENCE_ORB.disable-block-interact.SNIFFER_EGG", true);
            config.set("EXPERIENCE_ORB.disable-target.VILLAGER", false);
            config.set("EXPERIENCE_ORB.disable-damage.VILLAGER", false);
            config.set("EXPERIENCE_ORB.disable-damage.ITEM", true);

            config.set("EYE_OF_ENDER.name", "Eye of Ender");
            config.set("EYE_OF_ENDER.hostile", false);
            config.set("EYE_OF_ENDER.chunk-limit", -1);
            config.set("EYE_OF_ENDER.disable-spawn", false);
            config.set("EYE_OF_ENDER.disable-block-damage", true);
            config.set("EYE_OF_ENDER.disable-block-form", true);
            config.set("EYE_OF_ENDER.disable-block-change", true);
            config.set("EYE_OF_ENDER.disable-block-interact.FARMLAND", true);
            config.set("EYE_OF_ENDER.disable-block-interact.TURTLE_EGG", true);
            config.set("EYE_OF_ENDER.disable-block-interact.SNIFFER_EGG", true);
            config.set("EYE_OF_ENDER.disable-target.VILLAGER", false);
            config.set("EYE_OF_ENDER.disable-damage.VILLAGER", false);
            config.set("EYE_OF_ENDER.disable-damage.ITEM", true);

            config.set("FALLING_BLOCK.name", "Falling Block");
            config.set("FALLING_BLOCK.hostile", true);
            config.set("FALLING_BLOCK.chunk-limit", -1);
            config.set("FALLING_BLOCK.disable-spawn", false);
            config.set("FALLING_BLOCK.disable-block-damage", true);
            config.set("FALLING_BLOCK.disable-block-form", true);
            config.set("FALLING_BLOCK.disable-block-change", true);
            config.set("FALLING_BLOCK.disable-block-interact.FARMLAND", true);
            config.set("FALLING_BLOCK.disable-block-interact.TURTLE_EGG", true);
            config.set("FALLING_BLOCK.disable-block-interact.SNIFFER_EGG", true);
            config.set("FALLING_BLOCK.disable-target.VILLAGER", false);
            config.set("FALLING_BLOCK.disable-damage.VILLAGER", false);
            config.set("FALLING_BLOCK.disable-damage.ITEM", true);

            config.set("FIREBALL.name", "Fireball");
            config.set("FIREBALL.hostile", true);
            config.set("FIREBALL.chunk-limit", -1);
            config.set("FIREBALL.disable-spawn", false);
            config.set("FIREBALL.disable-block-damage", true);
            config.set("FIREBALL.disable-block-form", true);
            config.set("FIREBALL.disable-block-change", true);
            config.set("FIREBALL.disable-block-interact.FARMLAND", true);
            config.set("FIREBALL.disable-block-interact.TURTLE_EGG", true);
            config.set("FIREBALL.disable-block-interact.SNIFFER_EGG", true);
            config.set("FIREBALL.disable-target.VILLAGER", false);
            config.set("FIREBALL.disable-damage.VILLAGER", false);
            config.set("FIREBALL.disable-damage.ITEM", true);

            config.set("FIREWORK_ROCKET.name", "Firework Rocket");
            config.set("FIREWORK_ROCKET.hostile", true);
            config.set("FIREWORK_ROCKET.chunk-limit", -1);
            config.set("FIREWORK_ROCKET.disable-spawn", false);
            config.set("FIREWORK_ROCKET.disable-block-damage", true);
            config.set("FIREWORK_ROCKET.disable-block-form", true);
            config.set("FIREWORK_ROCKET.disable-block-change", true);
            config.set("FIREWORK_ROCKET.disable-block-interact.FARMLAND", true);
            config.set("FIREWORK_ROCKET.disable-block-interact.TURTLE_EGG", true);
            config.set("FIREWORK_ROCKET.disable-block-interact.SNIFFER_EGG", true);
            config.set("FIREWORK_ROCKET.disable-target.VILLAGER", false);
            config.set("FIREWORK_ROCKET.disable-damage.VILLAGER", false);
            config.set("FIREWORK_ROCKET.disable-damage.ITEM", true);

            config.set("FISHING_BOBBER.name", "Fishing Bobber");
            config.set("FISHING_BOBBER.hostile", false);
            config.set("FISHING_BOBBER.chunk-limit", -1);
            config.set("FISHING_BOBBER.disable-spawn", false);
            config.set("FISHING_BOBBER.disable-block-damage", true);
            config.set("FISHING_BOBBER.disable-block-form", true);
            config.set("FISHING_BOBBER.disable-block-change", true);
            config.set("FISHING_BOBBER.disable-block-interact.FARMLAND", true);
            config.set("FISHING_BOBBER.disable-block-interact.TURTLE_EGG", true);
            config.set("FISHING_BOBBER.disable-block-interact.SNIFFER_EGG", true);
            config.set("FISHING_BOBBER.disable-target.VILLAGER", false);
            config.set("FISHING_BOBBER.disable-damage.VILLAGER", false);
            config.set("FISHING_BOBBER.disable-damage.ITEM", true);

            config.set("FOX.name", "Fox");
            config.set("FOX.hostile", false);
            config.set("FOX.chunk-limit", 8);
            config.set("FOX.disable-spawn", false);
            config.set("FOX.disable-block-damage", true);
            config.set("FOX.disable-block-form", true);
            config.set("FOX.disable-block-change", true);
            config.set("FOX.disable-block-interact.FARMLAND", true);
            config.set("FOX.disable-block-interact.TURTLE_EGG", true);
            config.set("FOX.disable-block-interact.SNIFFER_EGG", true);
            config.set("FOX.disable-target.VILLAGER", false);
            config.set("FOX.disable-damage.VILLAGER", false);
            config.set("FOX.disable-damage.ITEM", true);
            config.set("FOX.carry.enable", true);
            config.set("FOX.carry.weight.adult", 1);
            config.set("FOX.carry.weight.baby", 0);

            config.set("FROG.name", "Frog");
            config.set("FROG.hostile", false);
            config.set("FROG.chunk-limit", 8);
            config.set("FROG.disable-spawn", false);
            config.set("FROG.disable-block-damage", true);
            config.set("FROG.disable-block-form", true);
            config.set("FROG.disable-block-change", true);
            config.set("FROG.disable-block-interact.FARMLAND", true);
            config.set("FROG.disable-block-interact.TURTLE_EGG", true);
            config.set("FROG.disable-block-interact.SNIFFER_EGG", true);
            config.set("FROG.disable-target.VILLAGER", false);
            config.set("FROG.disable-damage.VILLAGER", false);
            config.set("FROG.disable-damage.ITEM", true);
            config.set("FROG.carry.enable", true);
            config.set("FROG.carry.weight.adult", 1);
            config.set("FROG.carry.weight.baby", 0);

            config.set("FURNACE_MINECART.name", "Furnace Minecart");
            config.set("FURNACE_MINECART.hostile", false);
            config.set("FURNACE_MINECART.chunk-limit", 8);
            config.set("FURNACE_MINECART.disable-spawn", false);
            config.set("FURNACE_MINECART.disable-block-damage", true);
            config.set("FURNACE_MINECART.disable-block-form", true);
            config.set("FURNACE_MINECART.disable-block-change", true);
            config.set("FURNACE_MINECART.disable-block-interact.FARMLAND", true);
            config.set("FURNACE_MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("FURNACE_MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("FURNACE_MINECART.disable-target.VILLAGER", false);
            config.set("FURNACE_MINECART.disable-damage.VILLAGER", false);
            config.set("FURNACE_MINECART.disable-damage.ITEM", true);

            config.set("GHAST.name", "Ghast");
            config.set("GHAST.hostile", true);
            config.set("GHAST.chunk-limit", 8);
            config.set("GHAST.disable-spawn", false);
            config.set("GHAST.disable-block-damage", true);
            config.set("GHAST.disable-block-form", true);
            config.set("GHAST.disable-block-change", true);
            config.set("GHAST.disable-block-interact.FARMLAND", true);
            config.set("GHAST.disable-block-interact.TURTLE_EGG", true);
            config.set("GHAST.disable-block-interact.SNIFFER_EGG", true);
            config.set("GHAST.disable-target.VILLAGER", false);
            config.set("GHAST.disable-damage.VILLAGER", false);
            config.set("GHAST.disable-damage.ITEM", true);
            config.set("GHAST.carry.enable", true);
            config.set("GHAST.carry.weight.adult", 1);
            config.set("GHAST.carry.weight.baby", 0);

            config.set("GIANT.name", "Giant");
            config.set("GIANT.hostile", true);
            config.set("GIANT.chunk-limit", 8);
            config.set("GIANT.disable-spawn", false);
            config.set("GIANT.disable-block-damage", true);
            config.set("GIANT.disable-block-form", true);
            config.set("GIANT.disable-block-change", true);
            config.set("GIANT.disable-block-interact.FARMLAND", true);
            config.set("GIANT.disable-block-interact.TURTLE_EGG", true);
            config.set("GIANT.disable-block-interact.SNIFFER_EGG", true);
            config.set("GIANT.disable-target.VILLAGER", false);
            config.set("GIANT.disable-damage.VILLAGER", false);
            config.set("GIANT.disable-damage.ITEM", true);
            config.set("GIANT.carry.enable", true);
            config.set("GIANT.carry.weight.adult", 1);
            config.set("GIANT.carry.weight.baby", 0);

            config.set("GLOW_ITEM_FRAME.name", "Glow Item Frame");
            config.set("GLOW_ITEM_FRAME.hostile", false);
            config.set("GLOW_ITEM_FRAME.chunk-limit", -1);
            config.set("GLOW_ITEM_FRAME.disable-spawn", false);
            config.set("GLOW_ITEM_FRAME.disable-block-damage", true);
            config.set("GLOW_ITEM_FRAME.disable-block-form", true);
            config.set("GLOW_ITEM_FRAME.disable-block-change", true);
            config.set("GLOW_ITEM_FRAME.disable-block-interact.FARMLAND", true);
            config.set("GLOW_ITEM_FRAME.disable-block-interact.TURTLE_EGG", true);
            config.set("GLOW_ITEM_FRAME.disable-block-interact.SNIFFER_EGG", true);
            config.set("GLOW_ITEM_FRAME.disable-target.VILLAGER", false);
            config.set("GLOW_ITEM_FRAME.disable-damage.VILLAGER", false);
            config.set("GLOW_ITEM_FRAME.disable-damage.ITEM", true);

            config.set("GLOW_SQUID.name", "Glow Squid");
            config.set("GLOW_SQUID.hostile", false);
            config.set("GLOW_SQUID.chunk-limit", 8);
            config.set("GLOW_SQUID.disable-spawn", false);
            config.set("GLOW_SQUID.disable-block-damage", true);
            config.set("GLOW_SQUID.disable-block-form", true);
            config.set("GLOW_SQUID.disable-block-change", true);
            config.set("GLOW_SQUID.disable-block-interact.FARMLAND", true);
            config.set("GLOW_SQUID.disable-block-interact.TURTLE_EGG", true);
            config.set("GLOW_SQUID.disable-block-interact.SNIFFER_EGG", true);
            config.set("GLOW_SQUID.disable-target.VILLAGER", false);
            config.set("GLOW_SQUID.disable-damage.VILLAGER", false);
            config.set("GLOW_SQUID.disable-damage.ITEM", true);
            config.set("GLOW_SQUID.carry.enable", true);
            config.set("GLOW_SQUID.carry.weight.adult", 1);
            config.set("GLOW_SQUID.carry.weight.baby", 0);

            config.set("GOAT.name", "Goat");
            config.set("GOAT.hostile", false);
            config.set("GOAT.chunk-limit", 8);
            config.set("GOAT.disable-spawn", false);
            config.set("GOAT.disable-block-damage", true);
            config.set("GOAT.disable-block-form", true);
            config.set("GOAT.disable-block-change", true);
            config.set("GOAT.disable-block-interact.FARMLAND", true);
            config.set("GOAT.disable-block-interact.TURTLE_EGG", true);
            config.set("GOAT.disable-block-interact.SNIFFER_EGG", true);
            config.set("GOAT.disable-target.VILLAGER", false);
            config.set("GOAT.disable-damage.VILLAGER", false);
            config.set("GOAT.disable-damage.ITEM", true);
            config.set("GOAT.carry.enable", true);
            config.set("GOAT.carry.weight.adult", 1);
            config.set("GOAT.carry.weight.baby", 0);

            config.set("GUARDIAN.name", "Guardian");
            config.set("GUARDIAN.hostile", true);
            config.set("GUARDIAN.chunk-limit", 8);
            config.set("GUARDIAN.disable-spawn", false);
            config.set("GUARDIAN.disable-block-damage", true);
            config.set("GUARDIAN.disable-block-form", true);
            config.set("GUARDIAN.disable-block-change", true);
            config.set("GUARDIAN.disable-block-interact.FARMLAND", true);
            config.set("GUARDIAN.disable-block-interact.TURTLE_EGG", true);
            config.set("GUARDIAN.disable-block-interact.SNIFFER_EGG", true);
            config.set("GUARDIAN.disable-target.VILLAGER", false);
            config.set("GUARDIAN.disable-damage.VILLAGER", false);
            config.set("GUARDIAN.disable-damage.ITEM", true);
            config.set("GUARDIAN.carry.enable", true);
            config.set("GUARDIAN.carry.weight.adult", 1);
            config.set("GUARDIAN.carry.weight.baby", 0);

            config.set("HOGLIN.name", "Hoglin");
            config.set("HOGLIN.hostile", true);
            config.set("HOGLIN.chunk-limit", 8);
            config.set("HOGLIN.disable-spawn", false);
            config.set("HOGLIN.disable-block-damage", true);
            config.set("HOGLIN.disable-block-form", true);
            config.set("HOGLIN.disable-block-change", true);
            config.set("HOGLIN.disable-block-interact.FARMLAND", true);
            config.set("HOGLIN.disable-block-interact.TURTLE_EGG", true);
            config.set("HOGLIN.disable-block-interact.SNIFFER_EGG", true);
            config.set("HOGLIN.disable-target.VILLAGER", false);
            config.set("HOGLIN.disable-damage.VILLAGER", false);
            config.set("HOGLIN.disable-damage.ITEM", true);
            config.set("HOGLIN.carry.enable", true);
            config.set("HOGLIN.carry.weight.adult", 1);
            config.set("HOGLIN.carry.weight.baby", 0);

            config.set("HOPPER_MINECART.name", "Hopper Minecart");
            config.set("HOPPER_MINECART.hostile", false);
            config.set("HOPPER_MINECART.chunk-limit", -1);
            config.set("HOPPER_MINECART.disable-spawn", false);
            config.set("HOPPER_MINECART.disable-block-damage", true);
            config.set("HOPPER_MINECART.disable-block-form", true);
            config.set("HOPPER_MINECART.disable-block-change", true);
            config.set("HOPPER_MINECART.disable-block-interact.FARMLAND", true);
            config.set("HOPPER_MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("HOPPER_MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("HOPPER_MINECART.disable-target.VILLAGER", false);
            config.set("HOPPER_MINECART.disable-damage.VILLAGER", false);
            config.set("HOPPER_MINECART.disable-damage.ITEM", true);

            config.set("HORSE.name", "Horse");
            config.set("HORSE.hostile", false);
            config.set("HORSE.chunk-limit", 8);
            config.set("HORSE.disable-spawn", false);
            config.set("HORSE.disable-block-damage", true);
            config.set("HORSE.disable-block-form", true);
            config.set("HORSE.disable-block-change", true);
            config.set("HORSE.disable-block-interact.FARMLAND", true);
            config.set("HORSE.disable-block-interact.TURTLE_EGG", true);
            config.set("HORSE.disable-block-interact.SNIFFER_EGG", true);
            config.set("HORSE.disable-target.VILLAGER", false);
            config.set("HORSE.disable-damage.VILLAGER", false);
            config.set("HORSE.disable-damage.ITEM", true);
            config.set("HORSE.carry.enable", true);
            config.set("HORSE.carry.weight.adult", 1);
            config.set("HORSE.carry.weight.baby", 0);

            config.set("HUSK.name", "Husk");
            config.set("HUSK.hostile", true);
            config.set("HUSK.chunk-limit", 8);
            config.set("HUSK.disable-spawn", false);
            config.set("HUSK.disable-block-damage", true);
            config.set("HUSK.disable-block-form", true);
            config.set("HUSK.disable-block-change", true);
            config.set("HUSK.disable-block-interact.FARMLAND", true);
            config.set("HUSK.disable-block-interact.TURTLE_EGG", true);
            config.set("HUSK.disable-block-interact.SNIFFER_EGG", true);
            config.set("HUSK.disable-target.VILLAGER", false);
            config.set("HUSK.disable-damage.VILLAGER", false);
            config.set("HUSK.disable-damage.ITEM", true);
            config.set("HUSK.carry.enable", true);
            config.set("HUSK.carry.weight.adult", 1);
            config.set("HUSK.carry.weight.baby", 0);

            config.set("ILLUSIONER.name", "Illusioner");
            config.set("ILLUSIONER.hostile", true);
            config.set("ILLUSIONER.chunk-limit", 8);
            config.set("ILLUSIONER.disable-spawn", false);
            config.set("ILLUSIONER.disable-block-damage", true);
            config.set("ILLUSIONER.disable-block-form", true);
            config.set("ILLUSIONER.disable-block-change", true);
            config.set("ILLUSIONER.disable-block-interact.FARMLAND", true);
            config.set("ILLUSIONER.disable-block-interact.TURTLE_EGG", true);
            config.set("ILLUSIONER.disable-block-interact.SNIFFER_EGG", true);
            config.set("ILLUSIONER.disable-target.VILLAGER", false);
            config.set("ILLUSIONER.disable-damage.VILLAGER", false);
            config.set("ILLUSIONER.disable-damage.ITEM", true);
            config.set("ILLUSIONER.carry.enable", true);
            config.set("ILLUSIONER.carry.weight.adult", 1);
            config.set("ILLUSIONER.carry.weight.baby", 0);

            config.set("INTERACTION.name", "Interaction");
            config.set("INTERACTION.hostile", false);
            config.set("INTERACTION.chunk-limit", -1);
            config.set("INTERACTION.disable-spawn", false);
            config.set("INTERACTION.disable-block-damage", true);
            config.set("INTERACTION.disable-block-form", true);
            config.set("INTERACTION.disable-block-change", true);
            config.set("INTERACTION.disable-block-interact.FARMLAND", true);
            config.set("INTERACTION.disable-block-interact.TURTLE_EGG", true);
            config.set("INTERACTION.disable-block-interact.SNIFFER_EGG", true);
            config.set("INTERACTION.disable-target.VILLAGER", false);
            config.set("INTERACTION.disable-damage.VILLAGER", false);
            config.set("INTERACTION.disable-damage.ITEM", true);

            config.set("IRON_GOLEM.name", "Iron Golem");
            config.set("IRON_GOLEM.hostile", true);
            config.set("IRON_GOLEM.chunk-limit", 8);
            config.set("IRON_GOLEM.disable-spawn", false);
            config.set("IRON_GOLEM.disable-block-damage", true);
            config.set("IRON_GOLEM.disable-block-form", true);
            config.set("IRON_GOLEM.disable-block-change", true);
            config.set("IRON_GOLEM.disable-block-interact.FARMLAND", true);
            config.set("IRON_GOLEM.disable-block-interact.TURTLE_EGG", true);
            config.set("IRON_GOLEM.disable-block-interact.SNIFFER_EGG", true);
            config.set("IRON_GOLEM.disable-target.VILLAGER", false);
            config.set("IRON_GOLEM.disable-damage.VILLAGER", false);
            config.set("IRON_GOLEM.disable-damage.ITEM", true);
            config.set("IRON_GOLEM.carry.enable", true);
            config.set("IRON_GOLEM.carry.weight.adult", 1);
            config.set("IRON_GOLEM.carry.weight.baby", 0);

            config.set("ITEM.name", "Item");
            config.set("ITEM.hostile", true);
            config.set("ITEM.chunk-limit", -1);
            config.set("ITEM.disable-spawn", false);
            config.set("ITEM.disable-block-damage", true);
            config.set("ITEM.disable-block-form", true);
            config.set("ITEM.disable-block-change", true);
            config.set("ITEM.disable-block-interact.FARMLAND", true);
            config.set("ITEM.disable-block-interact.TURTLE_EGG", true);
            config.set("ITEM.disable-block-interact.SNIFFER_EGG", true);
            config.set("ITEM.disable-target.VILLAGER", false);
            config.set("ITEM.disable-damage.VILLAGER", false);
            config.set("ITEM.disable-damage.ITEM", true);

            config.set("ITEM_DISPLAY.name", "Item Display");
            config.set("ITEM_DISPLAY.hostile", false);
            config.set("ITEM_DISPLAY.chunk-limit", -1);
            config.set("ITEM_DISPLAY.disable-spawn", false);
            config.set("ITEM_DISPLAY.disable-block-damage", true);
            config.set("ITEM_DISPLAY.disable-block-form", true);
            config.set("ITEM_DISPLAY.disable-block-change", true);
            config.set("ITEM_DISPLAY.disable-block-interact.FARMLAND", true);
            config.set("ITEM_DISPLAY.disable-block-interact.TURTLE_EGG", true);
            config.set("ITEM_DISPLAY.disable-block-interact.SNIFFER_EGG", true);
            config.set("ITEM_DISPLAY.disable-target.VILLAGER", false);
            config.set("ITEM_DISPLAY.disable-damage.VILLAGER", false);
            config.set("ITEM_DISPLAY.disable-damage.ITEM", true);

            config.set("ITEM_FRAME.name", "Item Frame");
            config.set("ITEM_FRAME.hostile", false);
            config.set("ITEM_FRAME.chunk-limit", -1);
            config.set("ITEM_FRAME.disable-spawn", false);
            config.set("ITEM_FRAME.disable-block-damage", true);
            config.set("ITEM_FRAME.disable-block-form", true);
            config.set("ITEM_FRAME.disable-block-change", true);
            config.set("ITEM_FRAME.disable-block-interact.FARMLAND", true);
            config.set("ITEM_FRAME.disable-block-interact.TURTLE_EGG", true);
            config.set("ITEM_FRAME.disable-block-interact.SNIFFER_EGG", true);
            config.set("ITEM_FRAME.disable-target.VILLAGER", false);
            config.set("ITEM_FRAME.disable-damage.VILLAGER", false);
            config.set("ITEM_FRAME.disable-damage.ITEM", true);

            config.set("LEASH_KNOT.name", "Leash Knot");
            config.set("LEASH_KNOT.hostile", false);
            config.set("LEASH_KNOT.chunk-limit", -1);
            config.set("LEASH_KNOT.disable-spawn", false);
            config.set("LEASH_KNOT.disable-block-damage", true);
            config.set("LEASH_KNOT.disable-block-form", true);
            config.set("LEASH_KNOT.disable-block-change", true);
            config.set("LEASH_KNOT.disable-block-interact.FARMLAND", true);
            config.set("LEASH_KNOT.disable-block-interact.TURTLE_EGG", true);
            config.set("LEASH_KNOT.disable-block-interact.SNIFFER_EGG", true);
            config.set("LEASH_KNOT.disable-target.VILLAGER", false);
            config.set("LEASH_KNOT.disable-damage.VILLAGER", false);
            config.set("LEASH_KNOT.disable-damage.ITEM", true);

            config.set("LIGHTING_BOLT.name", "Lighting Bolt");
            config.set("LIGHTING_BOLT.hostile", true);
            config.set("LIGHTING_BOLT.chunk-limit", -1);
            config.set("LIGHTING_BOLT.disable-spawn", false);
            config.set("LIGHTING_BOLT.disable-block-damage", true);
            config.set("LIGHTING_BOLT.disable-block-form", true);
            config.set("LIGHTING_BOLT.disable-block-change", true);
            config.set("LIGHTING_BOLT.disable-block-interact.FARMLAND", true);
            config.set("LIGHTING_BOLT.disable-block-interact.TURTLE_EGG", true);
            config.set("LIGHTING_BOLT.disable-block-interact.SNIFFER_EGG", true);
            config.set("LIGHTING_BOLT.disable-target.VILLAGER", false);
            config.set("LIGHTING_BOLT.disable-damage.VILLAGER", false);
            config.set("LIGHTING_BOLT.disable-damage.ITEM", true);

            config.set("LLAMA.name", "Llama");
            config.set("LLAMA.hostile", false);
            config.set("LLAMA.chunk-limit", 8);
            config.set("LLAMA.disable-spawn", false);
            config.set("LLAMA.disable-block-damage", true);
            config.set("LLAMA.disable-block-form", true);
            config.set("LLAMA.disable-block-change", true);
            config.set("LLAMA.disable-block-interact.FARMLAND", true);
            config.set("LLAMA.disable-block-interact.TURTLE_EGG", true);
            config.set("LLAMA.disable-block-interact.SNIFFER_EGG", true);
            config.set("LLAMA.disable-target.VILLAGER", false);
            config.set("LLAMA.disable-damage.VILLAGER", false);
            config.set("LLAMA.disable-damage.ITEM", true);
            config.set("LLAMA.carry.enable", true);
            config.set("LLAMA.carry.weight.adult", 1);
            config.set("LLAMA.carry.weight.baby", 0);

            config.set("LLAMA_SPIT.name", "Llama Spit");
            config.set("LLAMA_SPIT.hostile", true);
            config.set("LLAMA_SPIT.chunk-limit", 8);
            config.set("LLAMA_SPIT.disable-spawn", false);
            config.set("LLAMA_SPIT.disable-block-damage", true);
            config.set("LLAMA_SPIT.disable-block-form", true);
            config.set("LLAMA_SPIT.disable-block-change", true);
            config.set("LLAMA_SPIT.disable-block-interact.FARMLAND", true);
            config.set("LLAMA_SPIT.disable-block-interact.TURTLE_EGG", true);
            config.set("LLAMA_SPIT.disable-block-interact.SNIFFER_EGG", true);
            config.set("LLAMA_SPIT.disable-target.VILLAGER", false);
            config.set("LLAMA_SPIT.disable-damage.VILLAGER", false);
            config.set("LLAMA_SPIT.disable-damage.ITEM", true);

            config.set("MAGMA_CUBE.name", "Magma Cube");
            config.set("MAGMA_CUBE.hostile", true);
            config.set("MAGMA_CUBE.chunk-limit", 8);
            config.set("MAGMA_CUBE.disable-spawn", false);
            config.set("MAGMA_CUBE.disable-block-damage", true);
            config.set("MAGMA_CUBE.disable-block-form", true);
            config.set("MAGMA_CUBE.disable-block-change", true);
            config.set("MAGMA_CUBE.disable-block-interact.FARMLAND", true);
            config.set("MAGMA_CUBE.disable-block-interact.TURTLE_EGG", true);
            config.set("MAGMA_CUBE.disable-block-interact.SNIFFER_EGG", true);
            config.set("MAGMA_CUBE.disable-target.VILLAGER", false);
            config.set("MAGMA_CUBE.disable-damage.VILLAGER", false);
            config.set("MAGMA_CUBE.disable-damage.ITEM", true);
            config.set("MAGMA_CUBE.carry.enable", true);
            config.set("MAGMA_CUBE.carry.weight.adult", 1);
            config.set("MAGMA_CUBE.carry.weight.baby", 0);

            config.set("MARKER.name", "Marker");
            config.set("MARKER.hostile", false);
            config.set("MARKER.chunk-limit", -1);
            config.set("MARKER.disable-spawn", false);
            config.set("MARKER.disable-block-damage", true);
            config.set("MARKER.disable-block-form", true);
            config.set("MARKER.disable-block-change", true);
            config.set("MARKER.disable-block-interact.FARMLAND", true);
            config.set("MARKER.disable-block-interact.TURTLE_EGG", true);
            config.set("MARKER.disable-block-interact.SNIFFER_EGG", true);
            config.set("MARKER.disable-target.VILLAGER", false);
            config.set("MARKER.disable-damage.VILLAGER", false);
            config.set("MARKER.disable-damage.ITEM", true);

            config.set("MINECART.name", "Minecart");
            config.set("MINECART.hostile", false);
            config.set("MINECART.chunk-limit", 8);
            config.set("MINECART.disable-spawn", false);
            config.set("MINECART.disable-block-damage", true);
            config.set("MINECART.disable-block-form", true);
            config.set("MINECART.disable-block-change", true);
            config.set("MINECART.disable-block-interact.FARMLAND", true);
            config.set("MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("MINECART.disable-target.VILLAGER", false);
            config.set("MINECART.disable-damage.VILLAGER", false);
            config.set("MINECART.disable-damage.ITEM", true);

            config.set("MOOSHROOM.name", "Mooshroom");
            config.set("MOOSHROOM.hostile", false);
            config.set("MOOSHROOM.chunk-limit", 8);
            config.set("MOOSHROOM.disable-spawn", false);
            config.set("MOOSHROOM.disable-block-damage", true);
            config.set("MOOSHROOM.disable-block-form", true);
            config.set("MOOSHROOM.disable-block-change", true);
            config.set("MOOSHROOM.disable-block-interact.FARMLAND", true);
            config.set("MOOSHROOM.disable-block-interact.TURTLE_EGG", true);
            config.set("MOOSHROOM.disable-block-interact.SNIFFER_EGG", true);
            config.set("MOOSHROOM.disable-target.VILLAGER", false);
            config.set("MOOSHROOM.disable-damage.VILLAGER", false);
            config.set("MOOSHROOM.disable-damage.ITEM", true);
            config.set("MOOSHROOM.carry.enable", true);
            config.set("MOOSHROOM.carry.weight.adult", 1);
            config.set("MOOSHROOM.carry.weight.baby", 0);

            config.set("MULE.name", "Mule");
            config.set("MULE.hostile", false);
            config.set("MULE.chunk-limit", 8);
            config.set("MULE.disable-spawn", false);
            config.set("MULE.disable-block-damage", true);
            config.set("MULE.disable-block-form", true);
            config.set("MULE.disable-block-change", true);
            config.set("MULE.disable-block-interact.FARMLAND", true);
            config.set("MULE.disable-block-interact.TURTLE_EGG", true);
            config.set("MULE.disable-block-interact.SNIFFER_EGG", true);
            config.set("MULE.disable-target.VILLAGER", false);
            config.set("MULE.disable-damage.VILLAGER", false);
            config.set("MULE.disable-damage.ITEM", true);
            config.set("MULE.carry.enable", true);
            config.set("MULE.carry.weight.adult", 1);
            config.set("MULE.carry.weight.baby", 0);

            config.set("OCELOT.name", "Ocelot");
            config.set("OCELOT.hostile", false);
            config.set("OCELOT.chunk-limit", 8);
            config.set("OCELOT.disable-spawn", false);
            config.set("OCELOT.disable-block-damage", true);
            config.set("OCELOT.disable-block-form", true);
            config.set("OCELOT.disable-block-change", true);
            config.set("OCELOT.disable-block-interact.FARMLAND", true);
            config.set("OCELOT.disable-block-interact.TURTLE_EGG", true);
            config.set("OCELOT.disable-block-interact.SNIFFER_EGG", true);
            config.set("OCELOT.disable-target.VILLAGER", false);
            config.set("OCELOT.disable-damage.VILLAGER", false);
            config.set("OCELOT.disable-damage.ITEM", true);
            config.set("OCELOT.carry.enable", true);
            config.set("OCELOT.carry.weight.adult", 1);
            config.set("OCELOT.carry.weight.baby", 0);

            config.set("OMINOUS_ITEM_SPAWNER.name", "Ominous Item Spawner");
            config.set("OMINOUS_ITEM_SPAWNER.hostile", false);
            config.set("OMINOUS_ITEM_SPAWNER.chunk-limit", -1);
            config.set("OMINOUS_ITEM_SPAWNER.disable-spawn", false);
            config.set("OMINOUS_ITEM_SPAWNER.disable-block-damage", true);
            config.set("OMINOUS_ITEM_SPAWNER.disable-block-form", true);
            config.set("OMINOUS_ITEM_SPAWNER.disable-block-change", true);
            config.set("OMINOUS_ITEM_SPAWNER.disable-block-interact.FARMLAND", true);
            config.set("OMINOUS_ITEM_SPAWNER.disable-block-interact.TURTLE_EGG", true);
            config.set("OMINOUS_ITEM_SPAWNER.disable-block-interact.SNIFFER_EGG", true);
            config.set("OMINOUS_ITEM_SPAWNER.disable-target.VILLAGER", false);
            config.set("OMINOUS_ITEM_SPAWNER.disable-damage.VILLAGER", false);
            config.set("OMINOUS_ITEM_SPAWNER.disable-damage.ITEM", true);

            config.set("PAINTING.name", "Painting");
            config.set("PAINTING.hostile", false);
            config.set("PAINTING.chunk-limit", -1);
            config.set("PAINTING.disable-spawn", false);
            config.set("PAINTING.disable-block-damage", true);
            config.set("PAINTING.disable-block-form", true);
            config.set("PAINTING.disable-block-change", true);
            config.set("PAINTING.disable-block-interact.FARMLAND", true);
            config.set("PAINTING.disable-block-interact.TURTLE_EGG", true);
            config.set("PAINTING.disable-block-interact.SNIFFER_EGG", true);
            config.set("PAINTING.disable-target.VILLAGER", false);
            config.set("PAINTING.disable-damage.VILLAGER", false);
            config.set("PAINTING.disable-damage.ITEM", true);

            config.set("PANDA.name", "Panda");
            config.set("PANDA.hostile", false);
            config.set("PANDA.chunk-limit", 8);
            config.set("PANDA.disable-spawn", false);
            config.set("PANDA.disable-block-damage", true);
            config.set("PANDA.disable-block-form", true);
            config.set("PANDA.disable-block-change", true);
            config.set("PANDA.disable-block-interact.FARMLAND", true);
            config.set("PANDA.disable-block-interact.TURTLE_EGG", true);
            config.set("PANDA.disable-block-interact.SNIFFER_EGG", true);
            config.set("PANDA.disable-target.VILLAGER", false);
            config.set("PANDA.disable-damage.VILLAGER", false);
            config.set("PANDA.disable-damage.ITEM", true);
            config.set("PANDA.carry.enable", true);
            config.set("PANDA.carry.weight.adult", 1);
            config.set("PANDA.carry.weight.baby", 0);

            config.set("PARROT.name", "Parrot");
            config.set("PARROT.hostile", false);
            config.set("PARROT.chunk-limit", 8);
            config.set("PARROT.disable-spawn", false);
            config.set("PARROT.disable-block-damage", true);
            config.set("PARROT.disable-block-form", true);
            config.set("PARROT.disable-block-change", true);
            config.set("PARROT.disable-block-interact.FARMLAND", true);
            config.set("PARROT.disable-block-interact.TURTLE_EGG", true);
            config.set("PARROT.disable-block-interact.SNIFFER_EGG", true);
            config.set("PARROT.disable-target.VILLAGER", false);
            config.set("PARROT.disable-damage.VILLAGER", false);
            config.set("PARROT.disable-damage.ITEM", true);
            config.set("PARROT.carry.enable", true);
            config.set("PARROT.carry.weight.adult", 1);
            config.set("PARROT.carry.weight.baby", 0);

            config.set("PHANTOM.name", "Phantom");
            config.set("PHANTOM.hostile", true);
            config.set("PHANTOM.chunk-limit", 8);
            config.set("PHANTOM.disable-spawn", false);
            config.set("PHANTOM.disable-block-damage", true);
            config.set("PHANTOM.disable-block-form", true);
            config.set("PHANTOM.disable-block-change", true);
            config.set("PHANTOM.disable-block-interact.FARMLAND", true);
            config.set("PHANTOM.disable-block-interact.TURTLE_EGG", true);
            config.set("PHANTOM.disable-block-interact.SNIFFER_EGG", true);
            config.set("PHANTOM.disable-target.VILLAGER", false);
            config.set("PHANTOM.disable-damage.VILLAGER", false);
            config.set("PHANTOM.disable-damage.ITEM", true);
            config.set("PHANTOM.carry.enable", true);
            config.set("PHANTOM.carry.weight.adult", 1);
            config.set("PHANTOM.carry.weight.baby", 0);

            config.set("PIG.name", "Pig");
            config.set("PIG.hostile", false);
            config.set("PIG.chunk-limit", 8);
            config.set("PIG.disable-spawn", false);
            config.set("PIG.disable-block-damage", true);
            config.set("PIG.disable-block-form", true);
            config.set("PIG.disable-block-change", true);
            config.set("PIG.disable-block-interact.FARMLAND", true);
            config.set("PIG.disable-block-interact.TURTLE_EGG", true);
            config.set("PIG.disable-block-interact.SNIFFER_EGG", true);
            config.set("PIG.disable-target.VILLAGER", false);
            config.set("PIG.disable-damage.VILLAGER", false);
            config.set("PIG.disable-damage.ITEM", true);
            config.set("PIG.carry.enable", true);
            config.set("PIG.carry.weight.adult", 1);
            config.set("PIG.carry.weight.baby", 0);

            config.set("PIGLIN.name", "Piglin");
            config.set("PIGLIN.hostile", true);
            config.set("PIGLIN.chunk-limit", 8);
            config.set("PIGLIN.disable-spawn", false);
            config.set("PIGLIN.disable-block-damage", true);
            config.set("PIGLIN.disable-block-form", true);
            config.set("PIGLIN.disable-block-change", true);
            config.set("PIGLIN.disable-block-interact.FARMLAND", true);
            config.set("PIGLIN.disable-block-interact.TURTLE_EGG", true);
            config.set("PIGLIN.disable-block-interact.SNIFFER_EGG", true);
            config.set("PIGLIN.disable-target.VILLAGER", false);
            config.set("PIGLIN.disable-damage.VILLAGER", false);
            config.set("PIGLIN.disable-damage.ITEM", true);
            config.set("PIGLIN.carry.enable", true);
            config.set("PIGLIN.carry.weight.adult", 1);
            config.set("PIGLIN.carry.weight.baby", 0);

            config.set("PIGLIN_BRUTE.name", "Piglin Brute");
            config.set("PIGLIN_BRUTE.hostile", true);
            config.set("PIGLIN_BRUTE.chunk-limit", 8);
            config.set("PIGLIN_BRUTE.disable-spawn", false);
            config.set("PIGLIN_BRUTE.disable-block-damage", true);
            config.set("PIGLIN_BRUTE.disable-block-form", true);
            config.set("PIGLIN_BRUTE.disable-block-change", true);
            config.set("PIGLIN_BRUTE.disable-block-interact.FARMLAND", true);
            config.set("PIGLIN_BRUTE.disable-block-interact.TURTLE_EGG", true);
            config.set("PIGLIN_BRUTE.disable-block-interact.SNIFFER_EGG", true);
            config.set("PIGLIN_BRUTE.disable-target.VILLAGER", false);
            config.set("PIGLIN_BRUTE.disable-damage.VILLAGER", false);
            config.set("PIGLIN_BRUTE.disable-damage.ITEM", true);
            config.set("PIGLIN_BRUTE.carry.enable", true);
            config.set("PIGLIN_BRUTE.carry.weight.adult", 1);
            config.set("PIGLIN_BRUTE.carry.weight.baby", 0);

            config.set("PILLAGER.name", "Pillager");
            config.set("PILLAGER.hostile", true);
            config.set("PILLAGER.chunk-limit", 8);
            config.set("PILLAGER.disable-spawn", false);
            config.set("PILLAGER.disable-block-damage", true);
            config.set("PILLAGER.disable-block-form", true);
            config.set("PILLAGER.disable-block-change", true);
            config.set("PILLAGER.disable-block-interact.FARMLAND", true);
            config.set("PILLAGER.disable-block-interact.TURTLE_EGG", true);
            config.set("PILLAGER.disable-block-interact.SNIFFER_EGG", true);
            config.set("PILLAGER.disable-target.VILLAGER", false);
            config.set("PILLAGER.disable-damage.VILLAGER", false);
            config.set("PILLAGER.disable-damage.ITEM", true);
            config.set("PILLAGER.carry.enable", true);
            config.set("PILLAGER.carry.weight.adult", 1);
            config.set("PILLAGER.carry.weight.baby", 0);

            config.set("POLAR_BEAR.name", "Polar Bear");
            config.set("POLAR_BEAR.hostile", true);
            config.set("POLAR_BEAR.chunk-limit", 8);
            config.set("POLAR_BEAR.disable-spawn", false);
            config.set("POLAR_BEAR.disable-block-damage", true);
            config.set("POLAR_BEAR.disable-block-form", true);
            config.set("POLAR_BEAR.disable-block-change", true);
            config.set("POLAR_BEAR.disable-block-interact.FARMLAND", true);
            config.set("POLAR_BEAR.disable-block-interact.TURTLE_EGG", true);
            config.set("POLAR_BEAR.disable-block-interact.SNIFFER_EGG", true);
            config.set("POLAR_BEAR.disable-target.VILLAGER", false);
            config.set("POLAR_BEAR.disable-damage.VILLAGER", false);
            config.set("POLAR_BEAR.disable-damage.ITEM", true);
            config.set("POLAR_BEAR.carry.enable", true);
            config.set("POLAR_BEAR.carry.weight.adult", 1);
            config.set("POLAR_BEAR.carry.weight.baby", 0);

            config.set("POTION.name", "Potion");
            config.set("POTION.hostile", true);
            config.set("POTION.chunk-limit", -1);
            config.set("POTION.disable-spawn", false);
            config.set("POTION.disable-block-damage", true);
            config.set("POTION.disable-block-form", true);
            config.set("POTION.disable-block-change", true);
            config.set("POTION.disable-block-interact.FARMLAND", true);
            config.set("POTION.disable-block-interact.TURTLE_EGG", true);
            config.set("POTION.disable-block-interact.SNIFFER_EGG", true);
            config.set("POTION.disable-target.VILLAGER", false);
            config.set("POTION.disable-damage.VILLAGER", false);
            config.set("POTION.disable-damage.ITEM", true);

            config.set("PUFFERFISH.name", "Pufferfish");
            config.set("PUFFERFISH.hostile", true);
            config.set("PUFFERFISH.chunk-limit", 8);
            config.set("PUFFERFISH.disable-spawn", false);
            config.set("PUFFERFISH.disable-block-damage", true);
            config.set("PUFFERFISH.disable-block-form", true);
            config.set("PUFFERFISH.disable-block-change", true);
            config.set("PUFFERFISH.disable-block-interact.FARMLAND", true);
            config.set("PUFFERFISH.disable-block-interact.TURTLE_EGG", true);
            config.set("PUFFERFISH.disable-block-interact.SNIFFER_EGG", true);
            config.set("PUFFERFISH.disable-target.VILLAGER", false);
            config.set("PUFFERFISH.disable-damage.VILLAGER", false);
            config.set("PUFFERFISH.disable-damage.ITEM", true);
            config.set("PUFFERFISH.carry.enable", true);
            config.set("PUFFERFISH.carry.weight.adult", 1);
            config.set("PUFFERFISH.carry.weight.baby", 0);

            config.set("RABBIT.name", "Rabbit");
            config.set("RABBIT.hostile", false);
            config.set("RABBIT.chunk-limit", 8);
            config.set("RABBIT.disable-spawn", false);
            config.set("RABBIT.disable-block-damage", true);
            config.set("RABBIT.disable-block-form", true);
            config.set("RABBIT.disable-block-change", true);
            config.set("RABBIT.disable-block-interact.FARMLAND", true);
            config.set("RABBIT.disable-block-interact.TURTLE_EGG", true);
            config.set("RABBIT.disable-block-interact.SNIFFER_EGG", true);
            config.set("RABBIT.disable-target.VILLAGER", false);
            config.set("RABBIT.disable-damage.VILLAGER", false);
            config.set("RABBIT.disable-damage.ITEM", true);
            config.set("RABBIT.carry.enable", true);
            config.set("RABBIT.carry.weight.adult", 1);
            config.set("RABBIT.carry.weight.baby", 0);

            config.set("RAVAGER.name", "Ravager");
            config.set("RAVAGER.hostile", true);
            config.set("RAVAGER.chunk-limit", 8);
            config.set("RAVAGER.disable-spawn", false);
            config.set("RAVAGER.disable-block-damage", true);
            config.set("RAVAGER.disable-block-form", true);
            config.set("RAVAGER.disable-block-change", true);
            config.set("RAVAGER.disable-block-interact.FARMLAND", true);
            config.set("RAVAGER.disable-block-interact.TURTLE_EGG", true);
            config.set("RAVAGER.disable-block-interact.SNIFFER_EGG", true);
            config.set("RAVAGER.disable-target.VILLAGER", false);
            config.set("RAVAGER.disable-damage.VILLAGER", false);
            config.set("RAVAGER.disable-damage.ITEM", true);
            config.set("RAVAGER.carry.enable", true);
            config.set("RAVAGER.carry.weight.adult", 1);
            config.set("RAVAGER.carry.weight.baby", 0);

            config.set("SALMON.name", "Salmon");
            config.set("SALMON.hostile", false);
            config.set("SALMON.chunk-limit", 8);
            config.set("SALMON.disable-spawn", false);
            config.set("SALMON.disable-block-damage", true);
            config.set("SALMON.disable-block-form", true);
            config.set("SALMON.disable-block-change", true);
            config.set("SALMON.disable-block-interact.FARMLAND", true);
            config.set("SALMON.disable-block-interact.TURTLE_EGG", true);
            config.set("SALMON.disable-block-interact.SNIFFER_EGG", true);
            config.set("SALMON.disable-target.VILLAGER", false);
            config.set("SALMON.disable-damage.VILLAGER", false);
            config.set("SALMON.disable-damage.ITEM", true);
            config.set("SALMON.carry.enable", true);
            config.set("SALMON.carry.weight.adult", 1);
            config.set("SALMON.carry.weight.baby", 0);

            config.set("SHEEP.name", "Sheep");
            config.set("SHEEP.hostile", false);
            config.set("SHEEP.chunk-limit", 8);
            config.set("SHEEP.disable-spawn", false);
            config.set("SHEEP.disable-block-damage", true);
            config.set("SHEEP.disable-block-form", true);
            config.set("SHEEP.disable-block-change", true);
            config.set("SHEEP.disable-block-interact.FARMLAND", true);
            config.set("SHEEP.disable-block-interact.TURTLE_EGG", true);
            config.set("SHEEP.disable-block-interact.SNIFFER_EGG", true);
            config.set("SHEEP.disable-target.VILLAGER", false);
            config.set("SHEEP.disable-damage.VILLAGER", false);
            config.set("SHEEP.disable-damage.ITEM", true);
            config.set("SHEEP.carry.enable", true);
            config.set("SHEEP.carry.weight.adult", 1);
            config.set("SHEEP.carry.weight.baby", 0);

            config.set("SHULKER.name", "Shulker");
            config.set("SHULKER.hostile", true);
            config.set("SHULKER.chunk-limit", 8);
            config.set("SHULKER.disable-spawn", false);
            config.set("SHULKER.disable-block-damage", true);
            config.set("SHULKER.disable-block-form", true);
            config.set("SHULKER.disable-block-change", true);
            config.set("SHULKER.disable-block-interact.FARMLAND", true);
            config.set("SHULKER.disable-block-interact.TURTLE_EGG", true);
            config.set("SHULKER.disable-block-interact.SNIFFER_EGG", true);
            config.set("SHULKER.disable-target.VILLAGER", false);
            config.set("SHULKER.disable-damage.VILLAGER", false);
            config.set("SHULKER.disable-damage.ITEM", true);

            config.set("SHULKER_BULLET.name", "Shulker Bullet");
            config.set("SHULKER_BULLET.hostile", true);
            config.set("SHULKER_BULLET.chunk-limit", -1);
            config.set("SHULKER_BULLET.disable-spawn", false);
            config.set("SHULKER_BULLET.disable-block-damage", true);
            config.set("SHULKER_BULLET.disable-block-form", true);
            config.set("SHULKER_BULLET.disable-block-change", true);
            config.set("SHULKER_BULLET.disable-block-interact.FARMLAND", true);
            config.set("SHULKER_BULLET.disable-block-interact.TURTLE_EGG", true);
            config.set("SHULKER_BULLET.disable-block-interact.SNIFFER_EGG", true);
            config.set("SHULKER_BULLET.disable-target.VILLAGER", false);
            config.set("SHULKER_BULLET.disable-damage.VILLAGER", false);
            config.set("SHULKER_BULLET.disable-damage.ITEM", true);

            config.set("SILVERFISH.name", "Silverfish");
            config.set("SILVERFISH.hostile", true);
            config.set("SILVERFISH.chunk-limit", 16);
            config.set("SILVERFISH.disable-spawn", false);
            config.set("SILVERFISH.disable-block-damage", true);
            config.set("SILVERFISH.disable-block-form", true);
            config.set("SILVERFISH.disable-block-change", true);
            config.set("SILVERFISH.disable-block-interact.FARMLAND", true);
            config.set("SILVERFISH.disable-block-interact.TURTLE_EGG", true);
            config.set("SILVERFISH.disable-block-interact.SNIFFER_EGG", true);
            config.set("SILVERFISH.disable-target.VILLAGER", false);
            config.set("SILVERFISH.disable-damage.VILLAGER", false);
            config.set("SILVERFISH.disable-damage.ITEM", true);
            config.set("SILVERFISH.carry.enable", true);
            config.set("SILVERFISH.carry.weight.adult", 1);
            config.set("SILVERFISH.carry.weight.baby", 0);

            config.set("SKELETON.name", "Skeleton");
            config.set("SKELETON.hostile", true);
            config.set("SKELETON.chunk-limit", 8);
            config.set("SKELETON.disable-spawn", false);
            config.set("SKELETON.disable-block-damage", true);
            config.set("SKELETON.disable-block-form", true);
            config.set("SKELETON.disable-block-change", true);
            config.set("SKELETON.disable-block-interact.FARMLAND", true);
            config.set("SKELETON.disable-block-interact.TURTLE_EGG", true);
            config.set("SKELETON.disable-block-interact.SNIFFER_EGG", true);
            config.set("SKELETON.disable-target.VILLAGER", false);
            config.set("SKELETON.disable-damage.VILLAGER", false);
            config.set("SKELETON.disable-damage.ITEM", true);
            config.set("SKELETON.carry.enable", true);
            config.set("SKELETON.carry.weight.adult", 1);
            config.set("SKELETON.carry.weight.baby", 0);

            config.set("SKELETON_HORSE.name", "Skeleton Horse");
            config.set("SKELETON_HORSE.hostile", false);
            config.set("SKELETON_HORSE.chunk-limit", 8);
            config.set("SKELETON_HORSE.disable-spawn", false);
            config.set("SKELETON_HORSE.disable-block-damage", true);
            config.set("SKELETON_HORSE.disable-block-form", true);
            config.set("SKELETON_HORSE.disable-block-change", true);
            config.set("SKELETON_HORSE.disable-block-interact.FARMLAND", true);
            config.set("SKELETON_HORSE.disable-block-interact.TURTLE_EGG", true);
            config.set("SKELETON_HORSE.disable-block-interact.SNIFFER_EGG", true);
            config.set("SKELETON_HORSE.disable-target.VILLAGER", false);
            config.set("SKELETON_HORSE.disable-damage.VILLAGER", false);
            config.set("SKELETON_HORSE.disable-damage.ITEM", true);
            config.set("SKELETON_HORSE.carry.enable", true);
            config.set("SKELETON_HORSE.carry.weight.adult", 1);
            config.set("SKELETON_HORSE.carry.weight.baby", 0);

            config.set("SLIME.name", "Slime");
            config.set("SLIME.hostile", true);
            config.set("SLIME.chunk-limit", 8);
            config.set("SLIME.disable-spawn", false);
            config.set("SLIME.disable-block-damage", true);
            config.set("SLIME.disable-block-form", true);
            config.set("SLIME.disable-block-change", true);
            config.set("SLIME.disable-block-interact.FARMLAND", true);
            config.set("SLIME.disable-block-interact.TURTLE_EGG", true);
            config.set("SLIME.disable-block-interact.SNIFFER_EGG", true);
            config.set("SLIME.disable-target.VILLAGER", false);
            config.set("SLIME.disable-damage.VILLAGER", false);
            config.set("SLIME.disable-damage.ITEM", true);
            config.set("SLIME.carry.enable", true);
            config.set("SLIME.carry.weight.adult", 1);
            config.set("SLIME.carry.weight.baby", 0);

            config.set("SMALL_FIREBALL.name", "Small Fireball");
            config.set("SMALL_FIREBALL.hostile", true);
            config.set("SMALL_FIREBALL.chunk-limit", -1);
            config.set("SMALL_FIREBALL.disable-spawn", false);
            config.set("SMALL_FIREBALL.disable-block-damage", true);
            config.set("SMALL_FIREBALL.disable-block-form", true);
            config.set("SMALL_FIREBALL.disable-block-change", true);
            config.set("SMALL_FIREBALL.disable-block-interact.FARMLAND", true);
            config.set("SMALL_FIREBALL.disable-block-interact.TURTLE_EGG", true);
            config.set("SMALL_FIREBALL.disable-block-interact.SNIFFER_EGG", true);
            config.set("SMALL_FIREBALL.disable-target.VILLAGER", false);
            config.set("SMALL_FIREBALL.disable-damage.VILLAGER", false);
            config.set("SMALL_FIREBALL.disable-damage.ITEM", true);

            config.set("SNIFFER.name", "Sniffer");
            config.set("SNIFFER.hostile", false);
            config.set("SNIFFER.chunk-limit", 8);
            config.set("SNIFFER.disable-spawn", false);
            config.set("SNIFFER.disable-block-damage", true);
            config.set("SNIFFER.disable-block-form", true);
            config.set("SNIFFER.disable-block-change", true);
            config.set("SNIFFER.disable-block-interact.FARMLAND", true);
            config.set("SNIFFER.disable-block-interact.TURTLE_EGG", true);
            config.set("SNIFFER.disable-block-interact.SNIFFER_EGG", true);
            config.set("SNIFFER.disable-target.VILLAGER", false);
            config.set("SNIFFER.disable-damage.VILLAGER", false);
            config.set("SNIFFER.disable-damage.ITEM", true);
            config.set("SNIFFER.carry.enable", true);
            config.set("SNIFFER.carry.weight.adult", 1);
            config.set("SNIFFER.carry.weight.baby", 0);

            config.set("SNOW_GOLEM.name", "Snowman");
            config.set("SNOW_GOLEM.hostile", false);
            config.set("SNOW_GOLEM.chunk-limit", 8);
            config.set("SNOW_GOLEM.disable-spawn", false);
            config.set("SNOW_GOLEM.disable-block-damage", true);
            config.set("SNOW_GOLEM.disable-block-form", false);
            config.set("SNOW_GOLEM.disable-block-change", true);
            config.set("SNOW_GOLEM.disable-block-interact.FARMLAND", true);
            config.set("SNOW_GOLEM.disable-block-interact.TURTLE_EGG", true);
            config.set("SNOW_GOLEM.disable-block-interact.SNIFFER_EGG", true);
            config.set("SNOW_GOLEM.disable-target.VILLAGER", false);
            config.set("SNOW_GOLEM.disable-damage.VILLAGER", false);
            config.set("SNOW_GOLEM.disable-damage.ITEM", true);
            config.set("SNOW_GOLEM.carry.enable", true);
            config.set("SNOW_GOLEM.carry.weight.adult", 1);
            config.set("SNOW_GOLEM.carry.weight.baby", 0);

            config.set("SNOWBALL.name", "Snowball");
            config.set("SNOWBALL.hostile", true);
            config.set("SNOWBALL.chunk-limit", -1);
            config.set("SNOWBALL.disable-spawn", false);
            config.set("SNOWBALL.disable-block-damage", true);
            config.set("SNOWBALL.disable-block-form", true);
            config.set("SNOWBALL.disable-block-change", true);
            config.set("SNOWBALL.disable-block-interact.FARMLAND", true);
            config.set("SNOWBALL.disable-block-interact.TURTLE_EGG", true);
            config.set("SNOWBALL.disable-block-interact.SNIFFER_EGG", true);
            config.set("SNOWBALL.disable-target.VILLAGER", false);
            config.set("SNOWBALL.disable-damage.VILLAGER", false);
            config.set("SNOWBALL.disable-damage.ITEM", true);

            config.set("SPAWNER_MINECART.name", "Spawner Minecart");
            config.set("SPAWNER_MINECART.hostile", false);
            config.set("SPAWNER_MINECART.chunk-limit", 8);
            config.set("SPAWNER_MINECART.disable-spawn", false);
            config.set("SPAWNER_MINECART.disable-block-damage", true);
            config.set("SPAWNER_MINECART.disable-block-form", true);
            config.set("SPAWNER_MINECART.disable-block-change", true);
            config.set("SPAWNER_MINECART.disable-block-interact.FARMLAND", true);
            config.set("SPAWNER_MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("SPAWNER_MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("SPAWNER_MINECART.disable-target.VILLAGER", false);
            config.set("SPAWNER_MINECART.disable-damage.VILLAGER", false);
            config.set("SPAWNER_MINECART.disable-damage.ITEM", true);

            config.set("SPECTRAL_ARROW.name", "Spectral Arrow");
            config.set("SPECTRAL_ARROW.hostile", true);
            config.set("SPECTRAL_ARROW.chunk-limit", -1);
            config.set("SPECTRAL_ARROW.disable-spawn", false);
            config.set("SPECTRAL_ARROW.disable-block-damage", true);
            config.set("SPECTRAL_ARROW.disable-block-form", true);
            config.set("SPECTRAL_ARROW.disable-block-change", true);
            config.set("SPECTRAL_ARROW.disable-block-interact.FARMLAND", true);
            config.set("SPECTRAL_ARROW.disable-block-interact.TURTLE_EGG", true);
            config.set("SPECTRAL_ARROW.disable-block-interact.SNIFFER_EGG", true);
            config.set("SPECTRAL_ARROW.disable-target.VILLAGER", false);
            config.set("SPECTRAL_ARROW.disable-damage.VILLAGER", false);
            config.set("SPECTRAL_ARROW.disable-damage.ITEM", true);

            config.set("SPIDER.name", "Spider");
            config.set("SPIDER.hostile", true);
            config.set("SPIDER.chunk-limit", 8);
            config.set("SPIDER.disable-spawn", false);
            config.set("SPIDER.disable-block-damage", true);
            config.set("SPIDER.disable-block-form", true);
            config.set("SPIDER.disable-block-change", true);
            config.set("SPIDER.disable-block-interact.FARMLAND", true);
            config.set("SPIDER.disable-block-interact.TURTLE_EGG", true);
            config.set("SPIDER.disable-block-interact.SNIFFER_EGG", true);
            config.set("SPIDER.disable-target.VILLAGER", false);
            config.set("SPIDER.disable-damage.VILLAGER", false);
            config.set("SPIDER.disable-damage.ITEM", true);
            config.set("SPIDER.carry.enable", true);
            config.set("SPIDER.carry.weight.adult", 1);
            config.set("SPIDER.carry.weight.baby", 0);

            config.set("SQUID.name", "Squid");
            config.set("SQUID.hostile", false);
            config.set("SQUID.chunk-limit", 8);
            config.set("SQUID.disable-spawn", false);
            config.set("SQUID.disable-block-damage", true);
            config.set("SQUID.disable-block-form", true);
            config.set("SQUID.disable-block-change", true);
            config.set("SQUID.disable-block-interact.FARMLAND", true);
            config.set("SQUID.disable-block-interact.TURTLE_EGG", true);
            config.set("SQUID.disable-block-interact.SNIFFER_EGG", true);
            config.set("SQUID.disable-target.VILLAGER", false);
            config.set("SQUID.disable-damage.VILLAGER", false);
            config.set("SQUID.disable-damage.ITEM", true);
            config.set("SQUID.carry.enable", true);
            config.set("SQUID.carry.weight.adult", 1);
            config.set("SQUID.carry.weight.baby", 0);

            config.set("STRAY.name", "Stray");
            config.set("STRAY.hostile", true);
            config.set("STRAY.chunk-limit", 8);
            config.set("STRAY.disable-spawn", false);
            config.set("STRAY.disable-block-damage", true);
            config.set("STRAY.disable-block-form", true);
            config.set("STRAY.disable-block-change", true);
            config.set("STRAY.disable-block-interact.FARMLAND", true);
            config.set("STRAY.disable-block-interact.TURTLE_EGG", true);
            config.set("STRAY.disable-block-interact.SNIFFER_EGG", true);
            config.set("STRAY.disable-target.VILLAGER", false);
            config.set("STRAY.disable-damage.VILLAGER", false);
            config.set("STRAY.disable-damage.ITEM", true);
            config.set("STRAY.carry.enable", true);
            config.set("STRAY.carry.weight.adult", 1);
            config.set("STRAY.carry.weight.baby", 0);

            config.set("STRIDER.name", "Stray");
            config.set("STRIDER.hostile", true);
            config.set("STRIDER.chunk-limit", 8);
            config.set("STRIDER.disable-spawn", false);
            config.set("STRIDER.disable-block-damage", true);
            config.set("STRIDER.disable-block-form", true);
            config.set("STRIDER.disable-block-change", true);
            config.set("STRIDER.disable-block-interact.FARMLAND", true);
            config.set("STRIDER.disable-block-interact.TURTLE_EGG", true);
            config.set("STRIDER.disable-block-interact.SNIFFER_EGG", true);
            config.set("STRIDER.disable-target.VILLAGER", false);
            config.set("STRIDER.disable-damage.VILLAGER", false);
            config.set("STRIDER.disable-damage.ITEM", true);
            config.set("STRIDER.carry.enable", true);
            config.set("STRIDER.carry.weight.adult", 1);
            config.set("STRIDER.carry.weight.baby", 0);

            config.set("TADPOLE.name", "Tadpole");
            config.set("TADPOLE.hostile", false);
            config.set("TADPOLE.chunk-limit", 8);
            config.set("TADPOLE.disable-spawn", false);
            config.set("TADPOLE.disable-block-damage", true);
            config.set("TADPOLE.disable-block-form", true);
            config.set("TADPOLE.disable-block-change", true);
            config.set("TADPOLE.disable-block-interact.FARMLAND", true);
            config.set("TADPOLE.disable-block-interact.TURTLE_EGG", true);
            config.set("TADPOLE.disable-block-interact.SNIFFER_EGG", true);
            config.set("TADPOLE.disable-target.VILLAGER", false);
            config.set("TADPOLE.disable-damage.VILLAGER", false);
            config.set("TADPOLE.disable-damage.ITEM", true);
            config.set("TADPOLE.carry.enable", true);
            config.set("TADPOLE.carry.weight.adult", 1);
            config.set("TADPOLE.carry.weight.baby", 0);

            config.set("TEXT_DISPLAY.name", "Text Display");
            config.set("TEXT_DISPLAY.hostile", false);
            config.set("TEXT_DISPLAY.chunk-limit", -1);
            config.set("TEXT_DISPLAY.disable-spawn", false);
            config.set("TEXT_DISPLAY.disable-block-damage", true);
            config.set("TEXT_DISPLAY.disable-block-form", true);
            config.set("TEXT_DISPLAY.disable-block-change", true);
            config.set("TEXT_DISPLAY.disable-block-interact.FARMLAND", true);
            config.set("TEXT_DISPLAY.disable-block-interact.TURTLE_EGG", true);
            config.set("TEXT_DISPLAY.disable-block-interact.SNIFFER_EGG", true);
            config.set("TEXT_DISPLAY.disable-target.VILLAGER", false);
            config.set("TEXT_DISPLAY.disable-damage.VILLAGER", false);
            config.set("TEXT_DISPLAY.disable-damage.ITEM", true);

            config.set("TNT.name", "TNT");
            config.set("TNT.hostile", true);
            config.set("TNT.chunk-limit", 8);
            config.set("TNT.disable-spawn", false);
            config.set("TNT.disable-block-damage", true);
            config.set("TNT.disable-block-form", true);
            config.set("TNT.disable-block-change", true);
            config.set("TNT.disable-block-interact.FARMLAND", true);
            config.set("TNT.disable-block-interact.TURTLE_EGG", true);
            config.set("TNT.disable-block-interact.SNIFFER_EGG", true);
            config.set("TNT.disable-target.VILLAGER", false);
            config.set("TNT.disable-damage.VILLAGER", false);
            config.set("TNT.disable-damage.ITEM", true);

            config.set("TNT_MINECART.name", "TNT Minecart");
            config.set("TNT_MINECART.hostile", true);
            config.set("TNT_MINECART.chunk-limit", 8);
            config.set("TNT_MINECART.disable-spawn", false);
            config.set("TNT_MINECART.disable-block-damage", true);
            config.set("TNT_MINECART.disable-block-form", true);
            config.set("TNT_MINECART.disable-block-change", true);
            config.set("TNT_MINECART.disable-block-interact.FARMLAND", true);
            config.set("TNT_MINECART.disable-block-interact.TURTLE_EGG", true);
            config.set("TNT_MINECART.disable-block-interact.SNIFFER_EGG", true);
            config.set("TNT_MINECART.disable-target.VILLAGER", false);
            config.set("TNT_MINECART.disable-damage.VILLAGER", false);
            config.set("TNT_MINECART.disable-damage.ITEM", true);

            config.set("TRADER_LLAMA.name", "Trader Llama");
            config.set("TRADER_LLAMA.hostile", false);
            config.set("TRADER_LLAMA.chunk-limit", 8);
            config.set("TRADER_LLAMA.disable-spawn", false);
            config.set("TRADER_LLAMA.disable-block-damage", true);
            config.set("TRADER_LLAMA.disable-block-form", true);
            config.set("TRADER_LLAMA.disable-block-change", true);
            config.set("TRADER_LLAMA.disable-block-interact.FARMLAND", true);
            config.set("TRADER_LLAMA.disable-block-interact.TURTLE_EGG", true);
            config.set("TRADER_LLAMA.disable-block-interact.SNIFFER_EGG", true);
            config.set("TRADER_LLAMA.disable-target.VILLAGER", false);
            config.set("TRADER_LLAMA.disable-damage.VILLAGER", false);
            config.set("TRADER_LLAMA.disable-damage.ITEM", true);
            config.set("TRADER_LLAMA.carry.enable", true);
            config.set("TRADER_LLAMA.carry.weight.adult", 1);
            config.set("TRADER_LLAMA.carry.weight.baby", 0);

            config.set("TRIDENT.name", "Trident");
            config.set("TRIDENT.hostile", true);
            config.set("TRIDENT.chunk-limit", -1);
            config.set("TRIDENT.disable-spawn", false);
            config.set("TRIDENT.disable-block-damage", true);
            config.set("TRIDENT.disable-block-form", true);
            config.set("TRIDENT.disable-block-change", true);
            config.set("TRIDENT.disable-block-interact.FARMLAND", true);
            config.set("TRIDENT.disable-block-interact.TURTLE_EGG", true);
            config.set("TRIDENT.disable-block-interact.SNIFFER_EGG", true);
            config.set("TRIDENT.disable-target.VILLAGER", false);
            config.set("TRIDENT.disable-damage.VILLAGER", false);
            config.set("TRIDENT.disable-damage.ITEM", true);

            config.set("TROPICAL_FISH.name", "Tropical Fish");
            config.set("TROPICAL_FISH.hostile", false);
            config.set("TROPICAL_FISH.chunk-limit", 8);
            config.set("TROPICAL_FISH.disable-spawn", false);
            config.set("TROPICAL_FISH.disable-block-damage", true);
            config.set("TROPICAL_FISH.disable-block-form", true);
            config.set("TROPICAL_FISH.disable-block-change", true);
            config.set("TROPICAL_FISH.disable-block-interact.FARMLAND", true);
            config.set("TROPICAL_FISH.disable-block-interact.TURTLE_EGG", true);
            config.set("TROPICAL_FISH.disable-block-interact.SNIFFER_EGG", true);
            config.set("TROPICAL_FISH.disable-target.VILLAGER", false);
            config.set("TROPICAL_FISH.disable-damage.VILLAGER", false);
            config.set("TROPICAL_FISH.disable-damage.ITEM", true);
            config.set("TROPICAL_FISH.carry.enable", true);
            config.set("TROPICAL_FISH.carry.weight.adult", 1);
            config.set("TROPICAL_FISH.carry.weight.baby", 0);

            config.set("TURTLE.name", "Turtle");
            config.set("TURTLE.hostile", false);
            config.set("TURTLE.chunk-limit", 8);
            config.set("TURTLE.disable-spawn", false);
            config.set("TURTLE.disable-block-damage", true);
            config.set("TURTLE.disable-block-form", true);
            config.set("TURTLE.disable-block-change", true);
            config.set("TURTLE.disable-block-interact.FARMLAND", true);
            config.set("TURTLE.disable-block-interact.TURTLE_EGG", true);
            config.set("TURTLE.disable-block-interact.SNIFFER_EGG", true);
            config.set("TURTLE.disable-target.VILLAGER", false);
            config.set("TURTLE.disable-damage.VILLAGER", false);
            config.set("TURTLE.disable-damage.ITEM", true);
            config.set("TURTLE.carry.enable", true);
            config.set("TURTLE.carry.weight.adult", 1);
            config.set("TURTLE.carry.weight.baby", 0);

            config.set("UNKNOWN.name", "Unknown");
            config.set("UNKNOWN.hostile", true);
            config.set("UNKNOWN.chunk-limit", -1);
            config.set("UNKNOWN.disable-spawn", false);
            config.set("UNKNOWN.disable-block-damage", true);
            config.set("UNKNOWN.disable-block-form", true);
            config.set("UNKNOWN.disable-block-change", true);
            config.set("UNKNOWN.disable-block-interact.FARMLAND", true);
            config.set("UNKNOWN.disable-block-interact.TURTLE_EGG", true);
            config.set("UNKNOWN.disable-block-interact.SNIFFER_EGG", true);
            config.set("UNKNOWN.disable-target.VILLAGER", false);
            config.set("UNKNOWN.disable-damage.VILLAGER", false);
            config.set("UNKNOWN.disable-damage.ITEM", true);

            config.set("VEX.name", "Vex");
            config.set("VEX.hostile", true);
            config.set("VEX.chunk-limit", 8);
            config.set("VEX.disable-spawn", false);
            config.set("VEX.disable-block-damage", true);
            config.set("VEX.disable-block-form", true);
            config.set("VEX.disable-block-change", true);
            config.set("VEX.disable-block-interact.FARMLAND", true);
            config.set("VEX.disable-block-interact.TURTLE_EGG", true);
            config.set("VEX.disable-block-interact.SNIFFER_EGG", true);
            config.set("VEX.disable-target.VILLAGER", false);
            config.set("VEX.disable-damage.VILLAGER", false);
            config.set("VEX.disable-damage.ITEM", true);
            config.set("VEX.carry.enable", true);
            config.set("VEX.carry.weight.adult", 1);
            config.set("VEX.carry.weight.baby", 0);

            config.set("VILLAGER.name", "Villager");
            config.set("VILLAGER.hostile", false);
            config.set("VILLAGER.chunk-limit", 8);
            config.set("VILLAGER.disable-spawn", false);
            config.set("VILLAGER.disable-block-damage", true);
            config.set("VILLAGER.disable-block-form", true);
            config.set("VILLAGER.disable-block-change", true);
            config.set("VILLAGER.disable-block-interact.FARMLAND", true);
            config.set("VILLAGER.disable-block-interact.TURTLE_EGG", true);
            config.set("VILLAGER.disable-block-interact.SNIFFER_EGG", true);
            config.set("VILLAGER.disable-target.VILLAGER", false);
            config.set("VILLAGER.disable-damage.VILLAGER", false);
            config.set("VILLAGER.disable-damage.ITEM", true);
            config.set("VILLAGER.carry.enable", true);
            config.set("VILLAGER.carry.weight.adult", 1);
            config.set("VILLAGER.carry.weight.baby", 0);

            config.set("VINDICATOR.name", "Vindicator");
            config.set("VINDICATOR.hostile", true);
            config.set("VINDICATOR.chunk-limit", 8);
            config.set("VINDICATOR.disable-spawn", false);
            config.set("VINDICATOR.disable-block-damage", true);
            config.set("VINDICATOR.disable-block-form", true);
            config.set("VINDICATOR.disable-block-change", true);
            config.set("VINDICATOR.disable-block-interact.FARMLAND", true);
            config.set("VINDICATOR.disable-block-interact.TURTLE_EGG", true);
            config.set("VINDICATOR.disable-block-interact.SNIFFER_EGG", true);
            config.set("VINDICATOR.disable-target.VILLAGER", false);
            config.set("VINDICATOR.disable-damage.VILLAGER", false);
            config.set("VINDICATOR.disable-damage.ITEM", true);
            config.set("VINDICATOR.carry.enable", true);
            config.set("VINDICATOR.carry.weight.adult", 1);
            config.set("VINDICATOR.carry.weight.baby", 0);

            config.set("WANDERING_TRADER.name", "Wandering Trader");
            config.set("WANDERING_TRADER.hostile", false);
            config.set("WANDERING_TRADER.chunk-limit", 8);
            config.set("WANDERING_TRADER.disable-spawn", false);
            config.set("WANDERING_TRADER.disable-block-damage", true);
            config.set("WANDERING_TRADER.disable-block-form", true);
            config.set("WANDERING_TRADER.disable-block-change", true);
            config.set("WANDERING_TRADER.disable-block-interact.FARMLAND", true);
            config.set("WANDERING_TRADER.disable-block-interact.TURTLE_EGG", true);
            config.set("WANDERING_TRADER.disable-block-interact.SNIFFER_EGG", true);
            config.set("WANDERING_TRADER.disable-target.VILLAGER", false);
            config.set("WANDERING_TRADER.disable-damage.VILLAGER", false);
            config.set("WANDERING_TRADER.disable-damage.ITEM", true);
            config.set("WANDERING_TRADER.carry.enable", true);
            config.set("WANDERING_TRADER.carry.weight.adult", 1);
            config.set("WANDERING_TRADER.carry.weight.baby", 0);

            config.set("WARDEN.name", "Warden");
            config.set("WARDEN.hostile", true);
            config.set("WARDEN.chunk-limit", 8);
            config.set("WARDEN.disable-spawn", false);
            config.set("WARDEN.disable-block-damage", true);
            config.set("WARDEN.disable-block-form", true);
            config.set("WARDEN.disable-block-change", true);
            config.set("WARDEN.disable-block-interact.FARMLAND", true);
            config.set("WARDEN.disable-block-interact.TURTLE_EGG", true);
            config.set("WARDEN.disable-block-interact.SNIFFER_EGG", true);
            config.set("WARDEN.disable-target.VILLAGER", false);
            config.set("WARDEN.disable-damage.VILLAGER", false);
            config.set("WARDEN.disable-damage.ITEM", true);
            config.set("WARDEN.carry.enable", true);
            config.set("WARDEN.carry.weight.adult", 1);
            config.set("WARDEN.carry.weight.baby", 0);

            config.set("WIND_CHARGE.name", "Wind Charge");
            config.set("WIND_CHARGE.hostile", true);
            config.set("WIND_CHARGE.chunk-limit", -1);
            config.set("WIND_CHARGE.disable-spawn", false);
            config.set("WIND_CHARGE.disable-block-damage", true);
            config.set("WIND_CHARGE.disable-block-form", true);
            config.set("WIND_CHARGE.disable-block-change", true);
            config.set("WIND_CHARGE.disable-block-interact.FARMLAND", true);
            config.set("WIND_CHARGE.disable-block-interact.TURTLE_EGG", true);
            config.set("WIND_CHARGE.disable-block-interact.SNIFFER_EGG", true);
            config.set("WIND_CHARGE.disable-target.VILLAGER", false);
            config.set("WIND_CHARGE.disable-damage.VILLAGER", false);
            config.set("WIND_CHARGE.disable-damage.ITEM", true);

            config.set("WITCH.name", "Witch");
            config.set("WITCH.hostile", true);
            config.set("WITCH.chunk-limit", 8);
            config.set("WITCH.disable-spawn", false);
            config.set("WITCH.disable-block-damage", true);
            config.set("WITCH.disable-block-form", true);
            config.set("WITCH.disable-block-change", true);
            config.set("WITCH.disable-block-interact.FARMLAND", true);
            config.set("WITCH.disable-block-interact.TURTLE_EGG", true);
            config.set("WITCH.disable-block-interact.SNIFFER_EGG", true);
            config.set("WITCH.disable-target.VILLAGER", false);
            config.set("WITCH.disable-damage.VILLAGER", false);
            config.set("WITCH.disable-damage.ITEM", true);
            config.set("WITCH.carry.enable", true);
            config.set("WITCH.carry.weight.adult", 1);
            config.set("WITCH.carry.weight.baby", 0);

            config.set("WITHER.name", "Wither");
            config.set("WITHER.hostile", true);
            config.set("WITHER.chunk-limit", 8);
            config.set("WITHER.disable-spawn", false);
            config.set("WITHER.disable-block-damage", true);
            config.set("WITHER.disable-block-form", true);
            config.set("WITHER.disable-block-change", true);
            config.set("WITHER.disable-block-interact.FARMLAND", true);
            config.set("WITHER.disable-block-interact.TURTLE_EGG", true);
            config.set("WITHER.disable-block-interact.SNIFFER_EGG", true);
            config.set("WITHER.disable-target.VILLAGER", false);
            config.set("WITHER.disable-damage.VILLAGER", false);
            config.set("WITHER.disable-damage.ITEM", true);
            config.set("WITHER.carry.enable", true);
            config.set("WITHER.carry.weight.adult", 1);
            config.set("WITHER.carry.weight.baby", 0);

            config.set("WITHER_SKELETON.name", "Wither Skeleton");
            config.set("WITHER_SKELETON.hostile", true);
            config.set("WITHER_SKELETON.chunk-limit", 8);
            config.set("WITHER_SKELETON.disable-spawn", false);
            config.set("WITHER_SKELETON.disable-block-damage", true);
            config.set("WITHER_SKELETON.disable-block-form", true);
            config.set("WITHER_SKELETON.disable-block-change", true);
            config.set("WITHER_SKELETON.disable-block-interact.FARMLAND", true);
            config.set("WITHER_SKELETON.disable-block-interact.TURTLE_EGG", true);
            config.set("WITHER_SKELETON.disable-block-interact.SNIFFER_EGG", true);
            config.set("WITHER_SKELETON.disable-target.VILLAGER", false);
            config.set("WITHER_SKELETON.disable-damage.VILLAGER", false);
            config.set("WITHER_SKELETON.disable-damage.ITEM", true);
            config.set("WITHER_SKELETON.carry.enable", true);
            config.set("WITHER_SKELETON.carry.weight.adult", 1);
            config.set("WITHER_SKELETON.carry.weight.baby", 0);

            config.set("WITHER_SKULL.name", "Wither Skull");
            config.set("WITHER_SKULL.hostile", true);
            config.set("WITHER_SKULL.chunk-limit", -1);
            config.set("WITHER_SKULL.disable-spawn", false);
            config.set("WITHER_SKULL.disable-block-damage", true);
            config.set("WITHER_SKULL.disable-block-form", true);
            config.set("WITHER_SKULL.disable-block-change", true);
            config.set("WITHER_SKULL.disable-block-interact.FARMLAND", true);
            config.set("WITHER_SKULL.disable-block-interact.TURTLE_EGG", true);
            config.set("WITHER_SKULL.disable-block-interact.SNIFFER_EGG", true);
            config.set("WITHER_SKULL.disable-target.VILLAGER", false);
            config.set("WITHER_SKULL.disable-damage.VILLAGER", false);
            config.set("WITHER_SKULL.disable-damage.ITEM", true);

            config.set("WOLF.name", "Wolf");
            config.set("WOLF.hostile", false);
            config.set("WOLF.chunk-limit", 2);
            config.set("WOLF.disable-spawn", false);
            config.set("WOLF.disable-block-damage", true);
            config.set("WOLF.disable-block-form", true);
            config.set("WOLF.disable-block-change", true);
            config.set("WOLF.disable-block-interact.FARMLAND", true);
            config.set("WOLF.disable-block-interact.TURTLE_EGG", true);
            config.set("WOLF.disable-block-interact.SNIFFER_EGG", true);
            config.set("WOLF.disable-target.VILLAGER", false);
            config.set("WOLF.disable-damage.VILLAGER", false);
            config.set("WOLF.disable-damage.ITEM", true);
            config.set("WOLF.carry.enable", true);
            config.set("WOLF.carry.weight.adult", 1);
            config.set("WOLF.carry.weight.baby", 0);

            config.set("ZOGLIN.name", "Zoglin");
            config.set("ZOGLIN.hostile", true);
            config.set("ZOGLIN.chunk-limit", 8);
            config.set("ZOGLIN.disable-spawn", false);
            config.set("ZOGLIN.disable-block-damage", true);
            config.set("ZOGLIN.disable-block-form", true);
            config.set("ZOGLIN.disable-block-change", true);
            config.set("ZOGLIN.disable-block-interact.FARMLAND", true);
            config.set("ZOGLIN.disable-block-interact.TURTLE_EGG", true);
            config.set("ZOGLIN.disable-block-interact.SNIFFER_EGG", true);
            config.set("ZOGLIN.disable-target.VILLAGER", false);
            config.set("ZOGLIN.disable-damage.VILLAGER", false);
            config.set("ZOGLIN.disable-damage.ITEM", true);
            config.set("ZOGLIN.carry.enable", true);
            config.set("ZOGLIN.carry.weight.adult", 1);
            config.set("ZOGLIN.carry.weight.baby", 0);

            config.set("ZOMBIE.name", "Zombie");
            config.set("ZOMBIE.hostile", true);
            config.set("ZOMBIE.chunk-limit", 8);
            config.set("ZOMBIE.disable-spawn", false);
            config.set("ZOMBIE.disable-block-damage", true);
            config.set("ZOMBIE.disable-block-form", true);
            config.set("ZOMBIE.disable-block-change", true);
            config.set("ZOMBIE.disable-block-interact.FARMLAND", true);
            config.set("ZOMBIE.disable-block-interact.TURTLE_EGG", true);
            config.set("ZOMBIE.disable-block-interact.SNIFFER_EGG", true);
            config.set("ZOMBIE.disable-target.VILLAGER", false);
            config.set("ZOMBIE.disable-damage.VILLAGER", false);
            config.set("ZOMBIE.disable-damage.ITEM", true);
            config.set("ZOMBIE.carry.enable", true);
            config.set("ZOMBIE.carry.weight.adult", 1);
            config.set("ZOMBIE.carry.weight.baby", 0);

            config.set("ZOMBIE_HORSE.name", "Zombie Horse");
            config.set("ZOMBIE_HORSE.hostile", false);
            config.set("ZOMBIE_HORSE.chunk-limit", 8);
            config.set("ZOMBIE_HORSE.disable-spawn", false);
            config.set("ZOMBIE_HORSE.disable-block-damage", true);
            config.set("ZOMBIE_HORSE.disable-block-form", true);
            config.set("ZOMBIE_HORSE.disable-block-change", true);
            config.set("ZOMBIE_HORSE.disable-block-interact.FARMLAND", true);
            config.set("ZOMBIE_HORSE.disable-block-interact.TURTLE_EGG", true);
            config.set("ZOMBIE_HORSE.disable-block-interact.SNIFFER_EGG", true);
            config.set("ZOMBIE_HORSE.disable-target.VILLAGER", false);
            config.set("ZOMBIE_HORSE.disable-damage.VILLAGER", false);
            config.set("ZOMBIE_HORSE.disable-damage.ITEM", true);
            config.set("ZOMBIE_HORSE.carry.enable", true);
            config.set("ZOMBIE_HORSE.carry.weight.adult", 1);
            config.set("ZOMBIE_HORSE.carry.weight.baby", 0);

            config.set("ZOMBIE_VILLAGER.name", "Zombie Villager");
            config.set("ZOMBIE_VILLAGER.hostile", true);
            config.set("ZOMBIE_VILLAGER.chunk-limit", 8);
            config.set("ZOMBIE_VILLAGER.disable-spawn", false);
            config.set("ZOMBIE_VILLAGER.disable-block-damage", true);
            config.set("ZOMBIE_VILLAGER.disable-block-form", true);
            config.set("ZOMBIE_VILLAGER.disable-block-change", true);
            config.set("ZOMBIE_VILLAGER.disable-block-interact.FARMLAND", true);
            config.set("ZOMBIE_VILLAGER.disable-block-interact.TURTLE_EGG", true);
            config.set("ZOMBIE_VILLAGER.disable-block-interact.SNIFFER_EGG", true);
            config.set("ZOMBIE_VILLAGER.disable-target.VILLAGER", false);
            config.set("ZOMBIE_VILLAGER.disable-damage.VILLAGER", false);
            config.set("ZOMBIE_VILLAGER.disable-damage.ITEM", true);
            config.set("ZOMBIE_VILLAGER.carry.enable", true);
            config.set("ZOMBIE_VILLAGER.carry.weight.adult", 1);
            config.set("ZOMBIE_VILLAGER.carry.weight.baby", 0);

            config.set("ZOMBIFIED_PIGLIN.name", "Zombified Piglin");
            config.set("ZOMBIFIED_PIGLIN.hostile", true);
            config.set("ZOMBIFIED_PIGLIN.chunk-limit", 8);
            config.set("ZOMBIFIED_PIGLIN.disable-spawn", false);
            config.set("ZOMBIFIED_PIGLIN.disable-block-damage", true);
            config.set("ZOMBIFIED_PIGLIN.disable-block-form", true);
            config.set("ZOMBIFIED_PIGLIN.disable-block-change", true);
            config.set("ZOMBIFIED_PIGLIN.disable-block-interact.FARMLAND", true);
            config.set("ZOMBIFIED_PIGLIN.disable-block-interact.TURTLE_EGG", true);
            config.set("ZOMBIFIED_PIGLIN.disable-block-interact.SNIFFER_EGG", true);
            config.set("ZOMBIFIED_PIGLIN.disable-target.VILLAGER", false);
            config.set("ZOMBIFIED_PIGLIN.disable-damage.VILLAGER", false);
            config.set("ZOMBIFIED_PIGLIN.disable-damage.ITEM", true);
            config.set("ZOMBIFIED_PIGLIN.carry.enable", true);
            config.set("ZOMBIFIED_PIGLIN.carry.weight.adult", 1);
            config.set("ZOMBIFIED_PIGLIN.carry.weight.baby", 0);
            try {
                config.save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
}