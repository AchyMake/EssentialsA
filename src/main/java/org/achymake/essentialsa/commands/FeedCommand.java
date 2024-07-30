package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FeedCommand implements CommandExecutor, TabCompleter {
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
    public FeedCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().hasCooldown(player, "feed")) {
                    getMessage().sendActionBar(player, "&cYou have to wait&f " + getDatabase().getCooldown(player, "feed") + "&c seconds");
                } else {
                    player.setFoodLevel(20);
                    getMessage().sendActionBar(player, "&6Your starvation has been satisfied");
                    getDatabase().addCooldown(player, "feed");
                    getMessage().send(player, "&6You satisfied&f " + player.getName() + "&6's starvation");
                }
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.feed.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.feed.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to feed&f " + target.getName());
                        } else {
                            target.setFoodLevel(20);
                            getMessage().sendActionBar(target, "&6Your starvation has been satisfied");
                            getMessage().send(player, "&6You satisfied&f " + target.getName() + "&6's starvation");
                        }
                        return true;
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.setFoodLevel(20);
                    getMessage().sendActionBar(target, "&6Your starvation has been satisfied");
                    getMessage().send(consoleCommandSender, "You satisfied " + target.getName() + "'s starvation");
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
                if (player.hasPermission("essentials.command.feed.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.feed.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}