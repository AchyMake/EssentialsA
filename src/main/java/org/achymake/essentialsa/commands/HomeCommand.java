package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public HomeCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (getDatabase().isFrozen(player) || getDatabase().isJailed(player)) {
                return false;
            } else {
                if (args.length == 0) {
                    if (getDatabase().homeExist(player, "home")) {
                        getDatabase().teleport(player, "home", getDatabase().getHome(player, "home"));
                    } else {
                        getMessage().send(player, "home&c does not exist");
                    }
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("bed")) {
                        if (player.hasPermission("essentials.command.home.bed")) {
                            if (player.getBedSpawnLocation() != null) {
                                getDatabase().teleport(player, "bed", player.getBedSpawnLocation());
                            } else {
                                getMessage().send(player, args[0] + "&c does not exist");
                            }
                        }
                    } else {
                        if (getDatabase().homeExist(player, args[0])) {
                            getDatabase().teleport(player, args[0], getDatabase().getHome(player, args[0]));
                        } else {
                            getMessage().send(player, args[0] + "&c does not exist");
                        }
                    }
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.commands.home.bed")) {
                    commands.add("bed");
                }
                commands.addAll(getDatabase().getHomes(player));
            }
        }
        return commands;
    }
}