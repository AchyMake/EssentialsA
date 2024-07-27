package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlySpeedCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public FlySpeedCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                float value = Float.parseFloat(args[0]);
                player.setFlySpeed(value);
                getMessage().send(player, "&6You're fly speed has changed to&f " + value);
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.flyspeed.other")) {
                    float value = Float.parseFloat(args[0]);
                    Player target = getServer().getPlayerExact(args[1]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.flyspeed.exempt")) {
                            getMessage().send(player, "&6You are not allowed to change&f " + target.getName() + " &6fly speed");
                        } else {
                            target.setFlySpeed(value);
                            getMessage().send(player, "&6You changed&f " + target.getName() + " &6fly speed to&f " + value);
                        }
                    } else {
                        getMessage().send(player, args[1] + "&c is currently offline");
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
                commands.add("0.1");
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.flyspeed.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.flyspeed.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}