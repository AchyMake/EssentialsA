package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Spawn;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Spawn getSpawn() {
        return plugin.getSpawn();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public SpawnCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().isFrozen(player) || getDatabase().isJailed(player)) {
                    return false;
                } else {
                    if (getSpawn().locationExist()) {
                        getDatabase().teleport(player, "spawn", getSpawn().getLocation());
                    } else {
                        getMessage().send(player, "Spawn&c does not exist");
                    }
                    return true;
                }
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.spawn.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (getDatabase().isDisabled(target)) {
                            return false;
                        } else {
                            if (getSpawn().locationExist()) {
                                if (target.hasPermission("essentials.command.spawn.exempt")) {
                                    getMessage().send(player, "&cYou are not allowed to spawn&f " + target.getName());
                                } else {
                                    getDatabase().teleport(player, "spawn", getSpawn().getLocation());
                                }
                            } else {
                                getMessage().send(player, "Spawn&c does not exist");
                            }
                            return true;
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    if (getDatabase().isDisabled(target)) {
                        return false;
                    } else {
                        if (getSpawn().locationExist()) {
                            getDatabase().teleport(target, "spawn", getSpawn().getLocation());
                        } else {
                            getMessage().send(consoleCommandSender, "Spawn&c does not exist");
                        }
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
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.spawn.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.spawn.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}