package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.achymake.essentialsa.data.Warps;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Warps getWarps() {
        return plugin.getWarps();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public WarpCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getUserdata().isDisabled(player)) {
                    return false;
                } else {
                    if (getWarps().getWarps().isEmpty()) {
                        getMessage().send(player, "&cWarps is currently empty");
                    } else {
                        getMessage().send(player, "&6Warps:");
                        for (String warps : getWarps().getWarps()) {
                            if (player.hasPermission("essentials.command.warp." + warps)) {
                                getMessage().send(player, "- " + warps);
                            }
                        }
                    }
                    return true;
                }
            }
            if (args.length == 1) {
                if (getUserdata().isDisabled(player)) {
                    return false;
                } else {
                    if (player.hasPermission("essentials.command.warp." + args[0])) {
                        if (getWarps().locationExist(args[0])) {
                            getDatabase().teleport(player, args[0], getWarps().getLocation(args[0]));
                        } else {
                            getMessage().send(player, args[0] + "&c does not exist");
                        }
                        return true;
                    }
                }
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.warp.other")) {
                    Player target = getServer().getPlayerExact(args[1]);
                    if (target != null) {
                        if (getUserdata().isDisabled(target)) {
                            return false;
                        } else {
                            if (getWarps().locationExist(args[0])) {
                                if (target.hasPermission("essentials.command.warp.exempt")) {
                                    getMessage().send(player, "&cYou are not allowed to warp&f " + target.getName());
                                } else {
                                    getDatabase().teleport(target, args[0], getWarps().getLocation(args[0]));
                                }
                            } else {
                                getMessage().send(player, args[0] + "&c does not exist");
                            }
                            return true;
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                Player target = getServer().getPlayerExact(args[1]);
                if (target != null) {
                    if (getUserdata().isDisabled(target)) {
                        return false;
                    } else {
                        if (getWarps().locationExist(args[0])) {
                            getDatabase().teleport(target, args[0], getWarps().getLocation(args[0]));
                        } else {
                            getMessage().send(consoleCommandSender, args[0] + " does not exist");
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
                for (String warps : getWarps().getWarps()) {
                    if (player.hasPermission("essentials.command.warp." + warps)) {
                        commands.add(warps);
                    }
                }
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.warp.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.warp.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}