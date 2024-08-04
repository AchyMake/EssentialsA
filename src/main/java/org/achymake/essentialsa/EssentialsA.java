package org.achymake.essentialsa;

import org.achymake.essentialsa.api.PlaceholderProvider;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.commands.*;
import org.achymake.essentialsa.commands.VillagerCommand;
import org.achymake.essentialsa.commands.WorldCommand;
import org.achymake.essentialsa.data.*;
import org.achymake.essentialsa.listeners.*;
import org.achymake.essentialsa.net.UpdateChecker;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public final class EssentialsA extends JavaPlugin {
    private static EssentialsA instance;
    private static Chairs chairs;
    private static ChestShop chestShop;
    private static Database database;
    private static Economy economy;
    private static Entities entities;
    private static Jail jail;
    private static Kits kits;
    private static Levels levels;
    private static Message message;
    private static Spawn spawn;
    private static Userdata userdata;
    private static Villagers villagers;
    private static Warps warps;
    private static Worlds worlds;
    private static Worth worth;
    private static UpdateChecker updateChecker;
    private final List<Player> vanished = new ArrayList<>();
    private final List<Player> chunkEditors = new ArrayList<>();
    private final List<Player> chestShopEditors = new ArrayList<>();
    private final HashMap<String, Long> commandCooldown = new HashMap<>();
    private final HashMap<String, Long> kitCooldown = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        message = new Message(this);
        chairs = new Chairs(this);
        chestShop = new ChestShop(this);
        database = new Database(this);
        economy = new Economy(this);
        entities = new Entities(this);
        jail = new Jail(this);
        kits = new Kits(this);
        levels = new Levels(this);
        spawn = new Spawn(this);
        userdata = new Userdata(this);
        villagers = new Villagers(this);
        warps = new Warps(this);
        worlds = new Worlds(this);
        worth = new Worth(this);
        updateChecker = new UpdateChecker(this);
        getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, getEconomy(), this, ServicePriority.Normal);
        new PlaceholderProvider().register();
        commands();
        events();
        reload();
        getWorlds().setupWorlds();
        getMessage().sendLog(Level.INFO, "Enabled " + getDescription().getName() + " " + getDescription().getVersion());
        getUpdateChecker().getUpdate();
    }
    @Override
    public void onDisable() {
        if (!getVanished().isEmpty()) {
            getVanished().clear();
        }
        if (!getChestShopEditors().isEmpty()) {
            getChestShopEditors().clear();
        }
        if (!getChunkEditors().isEmpty()) {
            getChunkEditors().clear();
        }
        new PlaceholderProvider().unregister();
        getServer().getScheduler().cancelTasks(this);
        getVillagers().disable();
        getMessage().sendLog(Level.INFO, "Disabled " + getDescription().getName() + " " + getDescription().getVersion());
    }
    private void commands() {
        getCommand("announcement").setExecutor(new AnnouncementCommand(this));
        getCommand("anvil").setExecutor(new AnvilCommand(this));
        getCommand("autopick").setExecutor(new AutoPickCommand(this));
        getCommand("baby").setExecutor(new BabyCommand(this));
        getCommand("back").setExecutor(new BackCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("chestshop").setExecutor(new ChestShopCommand(this));
        getCommand("color").setExecutor(new ColorCommand(this));
        getCommand("delhome").setExecutor(new DelHomeCommand(this));
        getCommand("delwarp").setExecutor(new DelWarpCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("eliminate").setExecutor(new EliminateCommand(this));
        getCommand("enchant").setExecutor(new EnchantCommand(this));
        getCommand("enderchest").setExecutor(new EnderChestCommand(this));
        getCommand("essentials").setExecutor(new EssentialsCommand(this));
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("flyspeed").setExecutor(new FlySpeedCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("gamemode").setExecutor(new GameModeCommand(this));
        getCommand("give").setExecutor(new GiveCommand(this));
        getCommand("gma").setExecutor(new GMACommand(this));
        getCommand("gmc").setExecutor(new GMCCommand(this));
        getCommand("gms").setExecutor(new GMSCommand(this));
        getCommand("gmsp").setExecutor(new GMSPCommand(this));
        getCommand("hat").setExecutor(new HatCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("help").setExecutor(new HelpCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("homes").setExecutor(new HomesCommand(this));
        getCommand("information").setExecutor(new InformationCommand(this));
        getCommand("inventory").setExecutor(new InventoryCommand(this));
        getCommand("jail").setExecutor(new JailCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("motd").setExecutor(new MOTDCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("nickname").setExecutor(new NicknameCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("pvp").setExecutor(new PVPCommand(this));
        getCommand("repair").setExecutor(new RepairCommand(this));
        getCommand("respond").setExecutor(new RespondCommand(this));
        getCommand("rtp").setExecutor(new RTPCommand(this));
        getCommand("rules").setExecutor(new RulesCommand(this));
        getCommand("scale").setExecutor(new ScaleCommand(this));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("setjail").setExecutor(new SetJailCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("setworth").setExecutor(new SetWorthCommand(this));
        getCommand("skull").setExecutor(new SkullCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("spawner").setExecutor(new SpawnerCommand(this));
        getCommand("store").setExecutor(new StoreCommand(this));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpahere").setExecutor(new TPAHereCommand(this));
        getCommand("tpcancel").setExecutor(new TPCancelCommand(this));
        getCommand("tp").setExecutor(new TPCommand(this));
        getCommand("tpdeny").setExecutor(new TPDenyCommand(this));
        getCommand("tphere").setExecutor(new TPHereCommand(this));
        getCommand("unban").setExecutor(new UnBanCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("villager").setExecutor(new VillagerCommand(this));
        getCommand("walkspeed").setExecutor(new WalkSpeedCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("whisper").setExecutor(new WhisperCommand(this));
        getCommand("workbench").setExecutor(new WorkbenchCommand(this));
        getCommand("world").setExecutor(new WorldCommand(this));
        getCommand("worth").setExecutor(new WorthCommand(this));
    }
    private void events() {
        getManager().registerEvents(new AsyncPlayerChat(this), this);
        getManager().registerEvents(new BlockBreak(this), this);
        getManager().registerEvents(new BlockDropItem(this), this);
        getManager().registerEvents(new BlockFertilize(this), this);
        getManager().registerEvents(new BlockPlace(this), this);
        getManager().registerEvents(new BlockReceiveGame(this), this);
        getManager().registerEvents(new BlockRedstone(this), this);
        getManager().registerEvents(new EntityBlockForm(this), this);
        getManager().registerEvents(new EntityBreed(this), this);
        getManager().registerEvents(new EntityChangeBlock(this), this);
        getManager().registerEvents(new EntityDamage(this), this);
        getManager().registerEvents(new EntityDamageByEntity(this), this);
        getManager().registerEvents(new EntityDeath(this), this);
        getManager().registerEvents(new EntityDismount(this), this);
        getManager().registerEvents(new EntityEnterLoveMode(this), this);
        getManager().registerEvents(new EntityExplode(this), this);
        getManager().registerEvents(new EntityInteract(this), this);
        getManager().registerEvents(new EntityPickupItem(this), this);
        getManager().registerEvents(new EntityPortalEnter(this), this);
        getManager().registerEvents(new EntitySpawn(this), this);
        getManager().registerEvents(new EntityTarget(this), this);
        getManager().registerEvents(new EntityTargetLivingEntity(this), this);
        getManager().registerEvents(new PlayerBucketEmpty(this), this);
        getManager().registerEvents(new PlayerBucketEntity(this), this);
        getManager().registerEvents(new PlayerBucketFill(this), this);
        getManager().registerEvents(new PlayerCommandPreprocess(this), this);
        getManager().registerEvents(new PlayerDeath(this), this);
        getManager().registerEvents(new PlayerHarvestBlock(this), this);
        getManager().registerEvents(new PlayerInteract(this), this);
        getManager().registerEvents(new PlayerInteractEntity(this), this);
        getManager().registerEvents(new PlayerJoin(this), this);
        getManager().registerEvents(new PlayerJump(this), this);
        getManager().registerEvents(new PlayerLeashEntity(this), this);
        getManager().registerEvents(new PlayerLevelChange(this), this);
        getManager().registerEvents(new PlayerLogin(this), this);
        getManager().registerEvents(new PlayerMount(this), this);
        getManager().registerEvents(new PlayerMove(this), this);
        getManager().registerEvents(new PlayerPortal(this), this);
        getManager().registerEvents(new PlayerQuit(this), this);
        getManager().registerEvents(new PlayerRespawn(this), this);
        getManager().registerEvents(new PlayerShearBlock(this), this);
        getManager().registerEvents(new PlayerShearEntity(this), this);
        getManager().registerEvents(new PlayerSpawnLocation(this), this);
        getManager().registerEvents(new PlayerTeleport(this), this);
        getManager().registerEvents(new PrepareAnvil(this), this);
        getManager().registerEvents(new SignChange(this), this);
        getManager().registerEvents(new VillagerAcquireTrade(this), this);
        getManager().registerEvents(new VillagerCareerChange(this), this);
        getManager().registerEvents(new VillagerReplenishTrade(this), this);
        getManager().registerEvents(new WorldLoad(this), this);
    }
    public void reload() {
        File file = new File(getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                getConfig().load(file);
            } catch (IOException | InvalidConfigurationException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            getConfig().options().copyDefaults(true);
            try {
                getConfig().save(file);
            } catch (IOException e) {
                getMessage().sendLog(Level.WARNING, e.getMessage());
            }
        }
        getEntities().reload();
        getJail().reload();
        getKits().reload();
        getLevels().reload();
        getSpawn().reload();
        getUserdata().reload();
        getVillagers().reload();
        getWarps().reload();
        getWorlds().reload();
        getWorth().reload();
    }
    private PluginManager getManager() {
        return getServer().getPluginManager();
    }
    public BukkitScheduler getScheduler() {
        return getServer().getScheduler();
    }
    public HashMap<String, Long> getKitCooldown() {
        return kitCooldown;
    }
    public HashMap<String, Long> getCommandCooldown() {
        return commandCooldown;
    }
    public List<Player> getChunkEditors() {
        return chunkEditors;
    }
    public List<Player> getChestShopEditors() {
        return chestShopEditors;
    }
    public List<Player> getVanished() {
        return vanished;
    }
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
    public Worth getWorth() {
        return worth;
    }
    public Worlds getWorlds() {
        return worlds;
    }
    public Warps getWarps() {
        return warps;
    }
    public Villagers getVillagers() {
        return villagers;
    }
    public Userdata getUserdata() {
        return userdata;
    }
    public Spawn getSpawn() {
        return spawn;
    }
    public Message getMessage() {
        return message;
    }
    public Levels getLevels() {
        return levels;
    }
    public Kits getKits() {
        return kits;
    }
    public Jail getJail() {
        return jail;
    }
    public Entities getEntities() {
        return entities;
    }
    public Economy getEconomy() {
        return economy;
    }
    public Database getDatabase() {
        return database;
    }
    public ChestShop getChestShop() {
        return chestShop;
    }
    public Chairs getChairs() {
        return chairs;
    }
    public static EssentialsA getInstance() {
        return instance;
    }
    public String name() {
        return getDescription().getName();
    }
    public String version() {
        return getDescription().getVersion();
    }
}