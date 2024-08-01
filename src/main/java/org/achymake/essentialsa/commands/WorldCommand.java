package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public WorldCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("setspawn")) {
                    getWorlds().setSpawn(player.getWorld(), player.getLocation());
                    getMessage().send(player, player.getWorld().getName() + "&6 changed spawn point");
                    return true;
                }
                if (args[0].equalsIgnoreCase("pvp")) {
                    if (getWorlds().worldExist(player.getWorld().getName())) {
                        getWorlds().setPVP(player.getWorld(), !getWorlds().isPVP(player.getWorld()));
                        if (getWorlds().isPVP(player.getWorld())) {
                            getMessage().send(player, player.getWorld().getName() + "&6 is now pvp mode");
                        } else {
                            getMessage().send(player, player.getWorld().getName() + "&6 is no longer pvp mode");
                        }
                        return true;
                    }
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("teleport")) {
                    String worldName = args[1];
                    if (getWorlds().worldExist(worldName)) {
                        player.teleport(getWorlds().getSpawn(getServer().getWorld(worldName)));
                        getMessage().send(player, "&6Teleporting to&f " + worldName);
                    } else {
                        getMessage().send(player, worldName + "&c does not exist");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    String worldName = args[1];
                    if (getWorlds().worldExist(worldName)) {
                        File file = new File(plugin.getDataFolder(), "worlds/" + worldName + ".yml");
                        if (file.exists()) {
                            file.delete();
                        }
                        getServer().unloadWorld(args[2], true);
                        getMessage().send(player, args[2] + "&6 is saved and removed");
                    } else {
                        getMessage().send(player, args[2] + "&c does not exist");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("pvp")) {
                    String worldName = args[1];
                    if (getWorlds().worldExist(worldName)) {
                        getWorlds().setPVP(player.getWorld(), !getWorlds().isPVP(player.getWorld()));
                        if (getWorlds().isPVP(player.getWorld())) {
                            getMessage().send(player, worldName + "&6 is now pvp mode");
                        } else {
                            getMessage().send(player, worldName  + "&6 is no longer pvp mode");
                        }
                    } else {
                        getMessage().send(player, worldName + "&c does not exist");
                    }
                    return true;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("pvp")) {
                    String worldName = args[1];
                    boolean value = Boolean.valueOf(args[2]);
                    if (getWorlds().worldExist(worldName)) {
                        getWorlds().setPVP(getServer().getWorld(worldName), value);
                        if (getWorlds().isPVP(getServer().getWorld(worldName))) {
                            getMessage().send(player, worldName + "&6 is now pvp mode");
                        } else {
                            getMessage().send(player, worldName + "&6 is no longer pvp mode");
                        }
                    } else {
                        getMessage().send(player, worldName + "&c does not exist");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    String worldName = args[1];
                    World.Environment environment = World.Environment.valueOf(args[2]);
                    if (getWorlds().folderExist(worldName)) {
                        getMessage().send(player, worldName + "&c already exist");
                    } else {
                        getMessage().send(player, worldName + "&6 is about to be created");
                        getWorlds().create(worldName, environment);
                        getMessage().send(player, worldName + "&6 created with environment&f " + environment.name().toLowerCase());
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    String worldName = args[1];
                    World.Environment environment = World.Environment.valueOf(args[2]);
                    if (getWorlds().folderExist(worldName)) {
                        if (getWorlds().worldExist(worldName)) {
                            getMessage().send(player, worldName + "&c already exist");
                        } else {
                            getMessage().send(player, worldName + "&6 is about to be added");
                            getWorlds().create(worldName, environment);
                            getMessage().send(player, worldName + "&6 is added with environment&f " + environment.name().toLowerCase());
                        }
                    } else {
                        getMessage().send(player, worldName + "&c does not exist");
                    }
                    return true;
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args[2].equalsIgnoreCase("gamerule")) {
                        String worldName = args[1];
                        String gamerule = args[2];
                        String value = args[3];
                        if (getWorlds().worldExist(worldName)) {
                            getServer().getWorld(worldName).setGameRuleValue(gamerule, value);
                            getMessage().send(player, worldName + "&6 changed&f " + gamerule + "&6 to&f " + value);
                        } else {
                            getMessage().send(player, worldName + "&c does not exist");
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("create")) {
                    String worldName = args[1];
                    World.Environment environment = World.Environment.valueOf(args[2]);
                    long seed = Long.valueOf(args[3]);
                    if (getWorlds().folderExist(worldName)) {
                        getMessage().send(player, worldName + "&c already exist");
                    } else {
                        getMessage().send(player, worldName + "&6 is about to be created");
                        getWorlds().create(worldName, environment, seed);
                        getMessage().send(player, worldName + "&6 created with environment&f " + environment.name().toLowerCase());
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                commands.add("add");
                commands.add("create");
                commands.add("gamerule");
                commands.add("pvp");
                commands.add("remove");
                commands.add("setspawn");
                commands.add("teleport");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("gamerule") | args[0].equalsIgnoreCase("pvp") | args[0].equalsIgnoreCase("remove") | args[0].equalsIgnoreCase("teleport")) {
                    for (World worlds : player.getServer().getWorlds()) {
                        commands.add(worlds.getName());
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    commands.add("normal");
                    commands.add("nether");
                    commands.add("the_end");
                }
                if (args[0].equalsIgnoreCase("create")) {
                    commands.add("normal");
                    commands.add("nether");
                    commands.add("the_end");
                }
                if (args[0].equalsIgnoreCase("pvp")) {
                    commands.add("true");
                    commands.add("false");
                }
                if (args[0].equalsIgnoreCase("gamerule")) {
                    Collections.addAll(commands, player.getServer().getWorld(args[1]).getGameRules());
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("gamerule")) {
                    commands.add(player.getServer().getWorld(args[1]).getGameRuleValue(args[2]));
                }
            }
        }
        return commands;
    }
}