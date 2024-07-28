package org.achymake.essentialsa.commands.world;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.sub.*;
import org.achymake.essentialsa.data.Message;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private final ArrayList<WorldSubCommand> worldSubCommands = new ArrayList<>();
    public WorldCommand(EssentialsA plugin) {
        worldSubCommands.add(new AddCommand(plugin));
        worldSubCommands.add(new CreateCommand(plugin));
        worldSubCommands.add(new GameruleCommand(plugin));
        worldSubCommands.add(new PVPCommand(plugin));
        worldSubCommands.add(new RemoveCommand(plugin));
        worldSubCommands.add(new SetSpawnCommand(plugin));
        worldSubCommands.add(new TeleportCommand(plugin));
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                getMessage().send(player, "&6" + plugin.getName() + ":&f " + plugin.getDescription().getVersion());
            }
            if (sender instanceof ConsoleCommandSender consoleCommandSender) {
                getMessage().send(consoleCommandSender, plugin.getName() + ": " + plugin.getDescription().getVersion());
            }
        } else {
            for (WorldSubCommand commands : worldSubCommands) {
                if (args[0].equalsIgnoreCase(commands.getName())) {
                    commands.perform(sender, args);
                }
            }
        }
        return true;
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
