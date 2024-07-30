package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HealCommand implements CommandExecutor, TabCompleter {
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
    public HealCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().hasCooldown(player, "heal")) {
                    getMessage().sendActionBar(player, "&cYou have to wait&f " + getDatabase().getCooldown(player, "heal") + "&c seconds");
                } else {
                    player.setFoodLevel(20);
                    player.setHealth(player.getMaxHealth());
                    getMessage().sendActionBar(player, "&6Your health has been satisfied");
                    getDatabase().addCooldown(player, "heal");
                }
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.heal.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.heal.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to heal&f " + target.getName());
                        } else {
                            target.setFoodLevel(20);
                            target.setHealth(target.getMaxHealth());
                            getMessage().sendActionBar(target, "&6Your health has been satisfied by&f " + player.getName());
                            getMessage().send(player, "&6You satisfied&f " + target.getName() + "&6's health");
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
                    target.setHealth(target.getMaxHealth());
                    getMessage().sendActionBar(target, "&6Your health has been satisfied");
                    getMessage().send(consoleCommandSender, "You satisfied " + target.getName() + "'s health");
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
                if (player.hasPermission("essentials.command.heal.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.heal.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}