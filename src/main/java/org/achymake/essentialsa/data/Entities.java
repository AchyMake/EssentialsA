package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public record Entities(EssentialsA plugin) {
    private File getDataFolder() {
        return plugin.getDataFolder();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public File getFile(String path) {
        return new File(getDataFolder(), path);
    }
    public File getFile(EntityType entityType) {
        return getFile("entity/" + entityType + ".yml");
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
        return getFile(entity).exists();
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
    public boolean isFriendly(Entity entity) {
        return !getConfig(entity).getBoolean("hostile");
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
    public void setAI(Entity target, boolean value) {
        switch (target) {
            case Allay entity -> entity.setAI(value);
            case Armadillo entity -> entity.setAI(value);
            case ArmorStand entity -> entity.setAI(value);
            case Axolotl entity -> entity.setAI(value);
            case Bat entity -> entity.setAI(value);
            case Bee entity -> entity.setAI(value);
            case Blaze entity -> entity.setAI(value);
            case Bogged entity -> entity.setAI(value);
            case Breeze entity -> entity.setAI(value);
            case Camel entity -> entity.setAI(value);
            case Cat entity -> entity.setAI(value);
            case CaveSpider entity -> entity.setAI(value);
            case Chicken entity -> entity.setAI(value);
            case Cod entity -> entity.setAI(value);
            case MushroomCow entity -> entity.setAI(value);
            case Cow entity -> entity.setAI(value);
            case Creeper entity -> entity.setAI(value);
            case Dolphin entity -> entity.setAI(value);
            case Donkey entity -> entity.setAI(value);
            case Drowned entity -> entity.setAI(value);
            case ElderGuardian entity -> entity.setAI(value);
            case EnderDragon entity -> entity.setAI(value);
            case Enderman entity -> entity.setAI(value);
            case Endermite entity -> entity.setAI(value);
            case Evoker entity -> entity.setAI(value);
            case Fox entity -> entity.setAI(value);
            case Frog entity -> entity.setAI(value);
            case Ghast entity -> entity.setAI(value);
            case Giant entity -> entity.setAI(value);
            case GlowSquid entity -> entity.setAI(value);
            case Goat entity -> entity.setAI(value);
            case Guardian entity -> entity.setAI(value);
            case Hoglin entity -> entity.setAI(value);
            case Horse entity -> entity.setAI(value);
            case Husk entity -> entity.setAI(value);
            case Illusioner entity -> entity.setAI(value);
            case IronGolem entity -> entity.setAI(value);
            case TraderLlama entity -> entity.setAI(value);
            case Llama entity -> entity.setAI(value);
            case MagmaCube entity -> entity.setAI(value);
            case Mule entity -> entity.setAI(value);
            case Ocelot entity -> entity.setAI(value);
            case Panda entity -> entity.setAI(value);
            case Parrot entity -> entity.setAI(value);
            case Phantom entity -> entity.setAI(value);
            case PigZombie entity -> entity.setAI(value);
            case Pig entity -> entity.setAI(value);
            case Piglin entity -> entity.setAI(value);
            case PiglinBrute entity -> entity.setAI(value);
            case Pillager entity -> entity.setAI(value);
            case Player entity -> entity.setAI(value);
            case PolarBear entity -> entity.setAI(value);
            case PufferFish entity -> entity.setAI(value);
            case Rabbit entity -> entity.setAI(value);
            case Ravager entity -> entity.setAI(value);
            case Salmon entity -> entity.setAI(value);
            case Sheep entity -> entity.setAI(value);
            case Shulker entity -> entity.setAI(value);
            case Silverfish entity -> entity.setAI(value);
            case Skeleton entity -> entity.setAI(value);
            case SkeletonHorse entity -> entity.setAI(value);
            case Slime entity -> entity.setAI(value);
            case Sniffer entity -> entity.setAI(value);
            case Snowman entity -> entity.setAI(value);
            case Spider entity -> entity.setAI(value);
            case Squid entity -> entity.setAI(value);
            case Stray entity -> entity.setAI(value);
            case Strider entity -> entity.setAI(value);
            case Tadpole entity -> entity.setAI(value);
            case TropicalFish entity -> entity.setAI(value);
            case Turtle entity -> entity.setAI(value);
            case Vex entity -> entity.setAI(value);
            case ZombieVillager entity -> entity.setAI(value);
            case Villager entity -> entity.setAI(value);
            case Vindicator entity -> entity.setAI(value);
            case WanderingTrader entity -> entity.setAI(value);
            case Warden entity -> entity.setAI(value);
            case Witch entity -> entity.setAI(value);
            case Wither entity -> entity.setAI(value);
            case WitherSkeleton entity -> entity.setAI(value);
            case Wolf entity -> entity.setAI(value);
            case Zombie entity -> entity.setAI(value);
            case ZombieHorse entity -> entity.setAI(value);
            default -> {
            }
        };
    }
    public void setSneaking(Entity target, boolean value) {
        switch (target) {
            case Allay entity -> entity.setSneaking(value);
            case Armadillo entity -> entity.setSneaking(value);
            case ArmorStand entity -> entity.setSneaking(value);
            case Axolotl entity -> entity.setSneaking(value);
            case Bat entity -> entity.setSneaking(value);
            case Bee entity -> entity.setSneaking(value);
            case Blaze entity -> entity.setSneaking(value);
            case Bogged entity -> entity.setSneaking(value);
            case Breeze entity -> entity.setSneaking(value);
            case Camel entity -> entity.setSneaking(value);
            case Cat entity -> entity.setSitting(value);
            case CaveSpider entity -> entity.setSneaking(value);
            case Chicken entity -> entity.setSneaking(value);
            case Cod entity -> entity.setSneaking(value);
            case MushroomCow entity -> entity.setSneaking(value);
            case Cow entity -> entity.setSneaking(value);
            case Creeper entity -> entity.setSneaking(value);
            case Dolphin entity -> entity.setSneaking(value);
            case Donkey entity -> entity.setSneaking(value);
            case Drowned entity -> entity.setSneaking(value);
            case ElderGuardian entity -> entity.setSneaking(value);
            case EnderDragon entity -> entity.setSneaking(value);
            case Enderman entity -> entity.setSneaking(value);
            case Endermite entity -> entity.setSneaking(value);
            case Evoker entity -> entity.setSneaking(value);
            case Fox entity -> entity.setCrouching(value);
            case Frog entity -> entity.setSneaking(value);
            case Ghast entity -> entity.setSneaking(value);
            case Giant entity -> entity.setSneaking(value);
            case GlowSquid entity -> entity.setSneaking(value);
            case Goat entity -> entity.setSneaking(value);
            case Guardian entity -> entity.setSneaking(value);
            case Hoglin entity -> entity.setSneaking(value);
            case Horse entity -> entity.setSneaking(value);
            case Husk entity -> entity.setSneaking(value);
            case Illusioner entity -> entity.setSneaking(value);
            case IronGolem entity -> entity.setSneaking(value);
            case TraderLlama entity -> entity.setSneaking(value);
            case Llama entity -> entity.setSneaking(value);
            case MagmaCube entity -> entity.setSneaking(value);
            case Mule entity -> entity.setSneaking(value);
            case Ocelot entity -> entity.setSneaking(value);
            case Panda entity -> entity.setSitting(value);
            case Parrot entity -> entity.setSitting(value);
            case Phantom entity -> entity.setSneaking(value);
            case PigZombie entity -> entity.setSneaking(value);
            case Pig entity -> entity.setSneaking(value);
            case Piglin entity -> entity.setSneaking(value);
            case PiglinBrute entity -> entity.setSneaking(value);
            case Pillager entity -> entity.setSneaking(value);
            case Player entity -> entity.setSneaking(value);
            case PolarBear entity -> entity.setStanding(!value);
            case PufferFish entity -> entity.setSneaking(value);
            case Rabbit entity -> entity.setSneaking(value);
            case Ravager entity -> entity.setSneaking(value);
            case Salmon entity -> entity.setSneaking(value);
            case Sheep entity -> entity.setSneaking(value);
            case Shulker entity -> entity.setSneaking(value);
            case Silverfish entity -> entity.setSneaking(value);
            case Skeleton entity -> entity.setSneaking(value);
            case SkeletonHorse entity -> entity.setSneaking(value);
            case Slime entity -> entity.setSneaking(value);
            case Sniffer entity -> entity.setSneaking(value);
            case Snowman entity -> entity.setSneaking(value);
            case Spider entity -> entity.setSneaking(value);
            case Squid entity -> entity.setSneaking(value);
            case Stray entity -> entity.setSneaking(value);
            case Strider entity -> entity.setSneaking(value);
            case Tadpole entity -> entity.setSneaking(value);
            case TropicalFish entity -> entity.setSneaking(value);
            case Turtle entity -> entity.setSneaking(value);
            case Vex entity -> entity.setSneaking(value);
            case ZombieVillager entity -> entity.setSneaking(value);
            case Villager entity -> entity.setSneaking(value);
            case Vindicator entity -> entity.setSneaking(value);
            case WanderingTrader entity -> entity.setSneaking(value);
            case Warden entity -> entity.setSneaking(value);
            case Witch entity -> entity.setSneaking(value);
            case Wither entity -> entity.setSneaking(value);
            case WitherSkeleton entity -> entity.setSneaking(value);
            case Wolf entity -> entity.setSitting(value);
            case Zombie entity -> entity.setSneaking(value);
            case ZombieHorse entity -> entity.setSneaking(value);
            default -> {
            }
        };
    }
    public void attack(Entity e, Entity target) {
        switch (e) {
            case Allay entity -> entity.attack(target);
            case Armadillo entity -> entity.attack(target);
            case ArmorStand entity -> entity.attack(target);
            case Axolotl entity -> entity.attack(target);
            case Bat entity -> entity.attack(target);
            case Bee entity -> entity.attack(target);
            case Blaze entity -> entity.attack(target);
            case Bogged entity -> entity.attack(target);
            case Breeze entity -> entity.attack(target);
            case Camel entity -> entity.attack(target);
            case Cat entity -> entity.attack(target);
            case CaveSpider entity -> entity.attack(target);
            case Chicken entity -> entity.attack(target);
            case Cod entity -> entity.attack(target);
            case MushroomCow entity -> entity.attack(target);
            case Cow entity -> entity.attack(target);
            case Creeper entity -> entity.attack(target);
            case Dolphin entity -> entity.attack(target);
            case Donkey entity -> entity.attack(target);
            case Drowned entity -> entity.attack(target);
            case ElderGuardian entity -> entity.attack(target);
            case EnderDragon entity -> entity.attack(target);
            case Enderman entity -> entity.attack(target);
            case Endermite entity -> entity.attack(target);
            case Evoker entity -> entity.attack(target);
            case Fox entity -> entity.attack(target);
            case Frog entity -> entity.attack(target);
            case Ghast entity -> entity.attack(target);
            case Giant entity -> entity.attack(target);
            case GlowSquid entity -> entity.attack(target);
            case Goat entity -> entity.attack(target);
            case Guardian entity -> entity.attack(target);
            case Hoglin entity -> entity.attack(target);
            case Horse entity -> entity.attack(target);
            case Husk entity -> entity.attack(target);
            case Illusioner entity -> entity.attack(target);
            case IronGolem entity -> entity.attack(target);
            case TraderLlama entity -> entity.attack(target);
            case Llama entity -> entity.attack(target);
            case MagmaCube entity -> entity.attack(target);
            case Mule entity -> entity.attack(target);
            case Ocelot entity -> entity.attack(target);
            case Panda entity -> entity.attack(target);
            case Parrot entity -> entity.attack(target);
            case Phantom entity -> entity.attack(target);
            case PigZombie entity -> entity.attack(target);
            case Pig entity -> entity.attack(target);
            case Piglin entity -> entity.attack(target);
            case PiglinBrute entity -> entity.attack(target);
            case Pillager entity -> entity.attack(target);
            case Player entity -> entity.attack(target);
            case PolarBear entity -> entity.attack(target);
            case PufferFish entity -> entity.attack(target);
            case Rabbit entity -> entity.attack(target);
            case Ravager entity -> entity.attack(target);
            case Salmon entity -> entity.attack(target);
            case Sheep entity -> entity.attack(target);
            case Shulker entity -> entity.attack(target);
            case Silverfish entity -> entity.attack(target);
            case Skeleton entity -> entity.attack(target);
            case SkeletonHorse entity -> entity.attack(target);
            case Slime entity -> entity.attack(target);
            case Sniffer entity -> entity.attack(target);
            case Snowman entity -> entity.attack(target);
            case Spider entity -> entity.attack(target);
            case Squid entity -> entity.attack(target);
            case Stray entity -> entity.attack(target);
            case Strider entity -> entity.attack(target);
            case Tadpole entity -> entity.attack(target);
            case TropicalFish entity -> entity.attack(target);
            case Turtle entity -> entity.attack(target);
            case Vex entity -> entity.attack(target);
            case ZombieVillager entity -> entity.attack(target);
            case Villager entity -> entity.attack(target);
            case Vindicator entity -> entity.attack(target);
            case WanderingTrader entity -> entity.attack(target);
            case Warden entity -> entity.attack(target);
            case Witch entity -> entity.attack(target);
            case Wither entity -> entity.attack(target);
            case WitherSkeleton entity -> entity.attack(target);
            case Wolf entity -> entity.attack(target);
            case Zombie entity -> entity.attack(target);
            case ZombieHorse entity -> entity.attack(target);
            default -> {
            }
        };
    }
    public void launchProjectile(Entity e, Class classValue, Vector vector) {
        switch (e) {
            case Allay entity -> entity.launchProjectile(classValue, vector);
            case Armadillo entity -> entity.launchProjectile(classValue, vector);
            case ArmorStand entity -> entity.launchProjectile(classValue, vector);
            case Axolotl entity -> entity.launchProjectile(classValue, vector);
            case Bat entity -> entity.launchProjectile(classValue, vector);
            case Bee entity -> entity.launchProjectile(classValue, vector);
            case Blaze entity -> entity.launchProjectile(classValue, vector);
            case Bogged entity -> entity.launchProjectile(classValue, vector);
            case Breeze entity -> entity.launchProjectile(classValue, vector);
            case Camel entity -> entity.launchProjectile(classValue, vector);
            case Cat entity -> entity.launchProjectile(classValue, vector);
            case CaveSpider entity -> entity.launchProjectile(classValue, vector);
            case Chicken entity -> entity.launchProjectile(classValue, vector);
            case Cod entity -> entity.launchProjectile(classValue, vector);
            case MushroomCow entity -> entity.launchProjectile(classValue, vector);
            case Cow entity -> entity.launchProjectile(classValue, vector);
            case Creeper entity -> entity.launchProjectile(classValue, vector);
            case Dolphin entity -> entity.launchProjectile(classValue, vector);
            case Donkey entity -> entity.launchProjectile(classValue, vector);
            case Drowned entity -> entity.launchProjectile(classValue, vector);
            case ElderGuardian entity -> entity.launchProjectile(classValue, vector);
            case EnderDragon entity -> entity.launchProjectile(classValue, vector);
            case Enderman entity -> entity.launchProjectile(classValue, vector);
            case Endermite entity -> entity.launchProjectile(classValue, vector);
            case Evoker entity -> entity.launchProjectile(classValue, vector);
            case Fox entity -> entity.launchProjectile(classValue, vector);
            case Frog entity -> entity.launchProjectile(classValue, vector);
            case Ghast entity -> entity.launchProjectile(classValue, vector);
            case Giant entity -> entity.launchProjectile(classValue, vector);
            case GlowSquid entity -> entity.launchProjectile(classValue, vector);
            case Goat entity -> entity.launchProjectile(classValue, vector);
            case Guardian entity -> entity.launchProjectile(classValue, vector);
            case Hoglin entity -> entity.launchProjectile(classValue, vector);
            case Horse entity -> entity.launchProjectile(classValue, vector);
            case Husk entity -> entity.launchProjectile(classValue, vector);
            case Illusioner entity -> entity.launchProjectile(classValue, vector);
            case IronGolem entity -> entity.launchProjectile(classValue, vector);
            case TraderLlama entity -> entity.launchProjectile(classValue, vector);
            case Llama entity -> entity.launchProjectile(classValue, vector);
            case MagmaCube entity -> entity.launchProjectile(classValue, vector);
            case Mule entity -> entity.launchProjectile(classValue, vector);
            case Ocelot entity -> entity.launchProjectile(classValue, vector);
            case Panda entity -> entity.launchProjectile(classValue, vector);
            case Parrot entity -> entity.launchProjectile(classValue, vector);
            case Phantom entity -> entity.launchProjectile(classValue, vector);
            case PigZombie entity -> entity.launchProjectile(classValue, vector);
            case Pig entity -> entity.launchProjectile(classValue, vector);
            case Piglin entity -> entity.launchProjectile(classValue, vector);
            case PiglinBrute entity -> entity.launchProjectile(classValue, vector);
            case Pillager entity -> entity.launchProjectile(classValue, vector);
            case Player entity -> entity.launchProjectile(classValue, vector);
            case PolarBear entity -> entity.launchProjectile(classValue, vector);
            case PufferFish entity -> entity.launchProjectile(classValue, vector);
            case Rabbit entity -> entity.launchProjectile(classValue, vector);
            case Ravager entity -> entity.launchProjectile(classValue, vector);
            case Salmon entity -> entity.launchProjectile(classValue, vector);
            case Sheep entity -> entity.launchProjectile(classValue, vector);
            case Shulker entity -> entity.launchProjectile(classValue, vector);
            case Silverfish entity -> entity.launchProjectile(classValue, vector);
            case Skeleton entity -> entity.launchProjectile(classValue, vector);
            case SkeletonHorse entity -> entity.launchProjectile(classValue, vector);
            case Slime entity -> entity.launchProjectile(classValue, vector);
            case Sniffer entity -> entity.launchProjectile(classValue, vector);
            case Snowman entity -> entity.launchProjectile(classValue, vector);
            case Spider entity -> entity.launchProjectile(classValue, vector);
            case Squid entity -> entity.launchProjectile(classValue, vector);
            case Stray entity -> entity.launchProjectile(classValue, vector);
            case Strider entity -> entity.launchProjectile(classValue, vector);
            case Tadpole entity -> entity.launchProjectile(classValue, vector);
            case TropicalFish entity -> entity.launchProjectile(classValue, vector);
            case Turtle entity -> entity.launchProjectile(classValue, vector);
            case Vex entity -> entity.launchProjectile(classValue, vector);
            case ZombieVillager entity -> entity.launchProjectile(classValue, vector);
            case Villager entity -> entity.launchProjectile(classValue, vector);
            case Vindicator entity -> entity.launchProjectile(classValue, vector);
            case WanderingTrader entity -> entity.launchProjectile(classValue, vector);
            case Warden entity -> entity.launchProjectile(classValue, vector);
            case Witch entity -> entity.launchProjectile(classValue, vector);
            case Wither entity -> entity.launchProjectile(classValue, vector);
            case WitherSkeleton entity -> entity.launchProjectile(classValue, vector);
            case Wolf entity -> entity.launchProjectile(classValue, vector);
            case Zombie entity -> entity.launchProjectile(classValue, vector);
            case ZombieHorse entity -> entity.launchProjectile(classValue, vector);
            default -> {
            }
        };
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