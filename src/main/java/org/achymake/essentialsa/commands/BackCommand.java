package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BackCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public BackCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (getDatabase().isFrozen(player) || getDatabase().isJailed(player)) {
                return false;
            } else {
                if (args.length == 0) {
                    teleportBack(player);
                }
                if (args.length == 1) {
                    if (player.hasPermission("essentials.command.back.other")) {
                        Player target = getServer().getPlayerExact(args[0]);
                        if (target != null) {
                            if (target == player) {
                                teleportBack(player);
                            } else {
                                if (!target.hasPermission("essentials.command.back.exempt")) {
                                    teleportBack(target);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (getDatabase().isFrozen(target) || getDatabase().isJailed(target)) {
                    return false;
                } else {
                    if (target != null) {
                        teleportBack(target);
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
                if (player.hasPermission("essentials.command.back.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.back.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
    private void teleportBack(Player player) {
        if (getDatabase().locationExist(player, "death")) {
            if (player.hasPermission("essentials.command.back.death")) {
                getDatabase().teleport(player, "death", getDatabase().getLocation(player, "death"));
                getDatabase().setString(player, "locations.death", null);
            } else {
                getDatabase().teleport(player, "recent", getDatabase().getLocation(player, "recent"));
            }
        } else {
            String worldName = getDatabase().getLocation(player, "recent").getWorld().getName();
            if (player.hasPermission("essentials.command.back.world." + worldName)) {
                getDatabase().teleport(player, "recent", getDatabase().getLocation(player, "recent"));
            }
        }
    }
}