package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnerCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public SpawnerCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("give")) {
                    Player target = getServer().getPlayerExact(args[1]);
                    String entityType = args[2];
                    if (target != null) {
                        getDatabase().giveItem(target, getDatabase().getSpawner(entityType, 1));
                        return true;
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("give")) {
                    Player target = getServer().getPlayerExact(args[1]);
                    String entityType = args[2];
                    int amount = Integer.parseInt(args[3]);
                    if (target != null) {
                        getDatabase().giveItem(target, getDatabase().getSpawner(entityType, amount));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.add("give");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        commands.add(players.getName());
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("give")) {
                    for (EntityType entityTypes : EntityType.values()) {
                        commands.add(entityTypes.toString().toLowerCase());
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("give")) {
                    commands.add("1");
                }
            }
        }
        return commands;
    }
}