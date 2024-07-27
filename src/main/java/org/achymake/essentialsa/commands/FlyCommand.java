package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlyCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public FlyCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.setAllowFlight(!player.getAllowFlight());
                if (player.getAllowFlight()) {
                    getMessage().sendActionBar(player, "&6&lFly:&a Enabled");
                } else {
                    getMessage().sendActionBar(player, "&6&lFly:&c Disabled");
                }
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.fly.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target == player) {
                        target.setAllowFlight(!target.getAllowFlight());
                        if (target.getAllowFlight()) {
                            getMessage().sendActionBar(target, "&6&lFly:&a Enabled");
                        } else {
                            getMessage().sendActionBar(target, "&6&lFly:&c Disabled");
                        }
                    } else {
                        if (target != null) {
                            if (!target.hasPermission("essentials.command.fly.exempt")) {
                                target.setAllowFlight(!target.getAllowFlight());
                                if (target.getAllowFlight()) {
                                    getMessage().sendActionBar(target, "&6&lFly:&a Enabled");
                                    getMessage().send(player, "&6You enabled fly for&f " + target.getName());
                                } else {
                                    getMessage().sendActionBar(target, "&6&lFly:&c Disabled");
                                    getMessage().send(player, "&6You disabled fly for&f " + target.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.setAllowFlight(!target.getAllowFlight());
                    if (target.getAllowFlight()) {
                        getMessage().sendActionBar(target, "&6&lFly:&a Enabled");
                        getMessage().send(consoleCommandSender, "You enabled fly for " + target.getName());
                    } else {
                        getMessage().sendActionBar(target, "&6&lFly:&c Disabled");
                        getMessage().send(consoleCommandSender, "You disabled fly for " + target.getName());
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
                if (player.hasPermission("essentials.command.fly.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.fly.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}