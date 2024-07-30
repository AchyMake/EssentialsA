package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WalkSpeedCommand implements CommandExecutor, TabCompleter {
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
    public WalkSpeedCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                float value = Float.parseFloat(args[0]);
                player.setWalkSpeed(value);
                getMessage().send(player, "&6You're walk speed has changed to&f " + value);
                return true;
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.walkspeed.other")) {
                    Player target = getServer().getPlayerExact(args[1]);
                    if (target != null) {
                        float value = Float.parseFloat(args[0]);
                        if (target == player) {
                            target.setFlySpeed(value);
                            getMessage().send(player, "&6You changed&f " + target.getName() + " &6walk speed to&f " + value);
                        } else if (target.hasPermission("essentials.command.walkspeed.exempt")) {
                            getMessage().send(player, "&6You are not allowed to change&f " + target.getName() + " &6walk speed");
                        } else {
                            target.setFlySpeed(value);
                            getMessage().send(player, "&6You changed&f " + target.getName() + " &6walk speed to&f " + value);
                        }
                    } else {
                        getMessage().send(player, args[1] + "&c is currently offline");
                    }
                    return true;
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                Player target = getServer().getPlayerExact(args[1]);
                if (target != null) {
                    float value = Float.parseFloat(args[0]);
                    target.setFlySpeed(value);
                    getMessage().send(consoleCommandSender, "You changed " + target.getName() + " walk speed to " + value);
                } else {
                    getMessage().send(consoleCommandSender, args[1] + " is currently offline");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                commands.add("0.2");
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.walkspeed.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.walkspeed.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}