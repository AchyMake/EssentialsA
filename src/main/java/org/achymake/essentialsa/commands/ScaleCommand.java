package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScaleCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public ScaleCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getDatabase().setScale(player, 1.0);
                getMessage().send(player, "&6You changed you're scale to&f 1.0");
            }
            if (args.length == 1) {
                double value = Double.parseDouble(args[0]);
                double maxValue = getConfig().getDouble("scale.max");
                double minimumValue = getConfig().getDouble("scale.minimum");
                if (value > maxValue) {
                    getMessage().send(player, "&cThe max value is&f " + maxValue);
                } else if (minimumValue > value) {
                    getMessage().send(player, "&cThe minimum value is&f " + minimumValue);
                } else {
                    getDatabase().setScale(player, value);
                    getMessage().send(player, "&6You changed you're scale to&f " + args[0]);
                }
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.scale.other")) {
                    Player target = getServer().getPlayerExact(args[1]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.scale.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to changed&f " + target.getName() + "&c's scale");
                        } else {
                            double value = Double.parseDouble(args[0]);
                            double maxValue = getConfig().getDouble("scale.max");
                            double minimumValue = getConfig().getDouble("scale.minimum");
                            if (value > maxValue) {
                                getMessage().send(player, "&cThe max value is&f " + maxValue);
                            } else if (minimumValue > value) {
                                getMessage().send(player, "&cThe minimum value is&f " + minimumValue);
                            } else {
                                getDatabase().setScale(target, value);
                                getMessage().send(player, "&6You changed &f" + target.getName() + "&6's scale to&f " + args[0]);
                            }
                        }
                    } else {
                        getMessage().send(player, args[1] + "&c is not online");
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
                commands.add("1.0");
                commands.add("0.75");
                commands.add("0.5");
                commands.add("0.25");
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.scale.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.scale.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}