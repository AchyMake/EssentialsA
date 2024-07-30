package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
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
    private Message getMessage() {
        return plugin.getMessage();
    }
    public BackCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (getDatabase().isDisabled(player)) {
                return false;
            } else {
                if (args.length == 0) {
                    teleportBack(player);
                    return true;
                }
                if (args.length == 1) {
                    if (player.hasPermission("essentials.command.back.other")) {
                        Player target = getServer().getPlayerExact(args[0]);
                        if (target != null) {
                            if (target == player) {
                                teleportBack(player);
                            } else {
                                if (target.hasPermission("essentials.command.back.exempt")) {
                                    getMessage().send(player, "&cYou are not allowed to tp back for&f " + target.getName());
                                } else {
                                    teleportBack(target);
                                    getMessage().send(player, target.getName() + "&6 has been teleported back");
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (getDatabase().isDisabled(target)) {
                    return false;
                } else {
                    if (target != null) {
                        teleportBack(target);
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
                if (player.hasPermission("essentials.command.back.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
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