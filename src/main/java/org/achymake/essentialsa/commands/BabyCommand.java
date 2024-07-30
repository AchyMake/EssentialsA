package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BabyCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public BabyCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getDatabase().toggleBaby(player);
                if (getDatabase().isBaby(player)) {
                    getMessage().send(player, "&6You changed to a baby");
                } else {
                    getMessage().send(player, "&6You changed to an adult");
                }
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.baby.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.baby.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to change scale for&f " + target.getName());
                        } else {
                            getDatabase().toggleBaby(target);
                            if (getDatabase().isBaby(target)) {
                                getMessage().send(player, "&6You changed&f " + target.getName() + "&6 to a baby");
                            } else {
                                getMessage().send(player, "&6You changed&f " + target.getName() + "&6 to an adult");
                            }
                        }
                    } else {
                        getMessage().send(player, args[0] + "&c is not online");
                    }
                    return true;
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
                if (player.hasPermission("essentials.command.baby.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.baby.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}