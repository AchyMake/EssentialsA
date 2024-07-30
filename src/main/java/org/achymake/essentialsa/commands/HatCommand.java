package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HatCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public HatCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (player.getInventory().getItemInMainHand().getType().isAir()) {
                    getMessage().send(player, "&cYou have to hold an item");
                } else {
                    if (player.getInventory().getHelmet() == null) {
                        getMessage().send(player, "&6You are now wearing&f " + player.getInventory().getItemInMainHand().getType());
                        player.getInventory().setHelmet(getDatabase().getItem(player.getInventory().getItemInMainHand().getType().toString(), 1));
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    } else {
                        getMessage().send(player, "&cYou are already wearing&f " + player.getInventory().getHelmet().getType());
                    }
                }
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.hat.other")) {
                    Player target = player.getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (player.getInventory().getItemInMainHand().getType().isAir()) {
                            getMessage().send(player, "&cYou have to hold an item");
                        } else {
                            if (target.hasPermission("essentials.command.hat.exempt")) {
                                getMessage().send(player, "&cYou are not allowed to change helmet for&f " + target.getName());
                            } else {
                                if (target.getInventory().getHelmet() == null) {
                                    getMessage().send(player, target.getName() + "&6 is now wearing&f " + player.getInventory().getItemInMainHand().getType());
                                    target.getInventory().setHelmet(getDatabase().getItem(player.getInventory().getItemInMainHand().getType().toString(), 1));
                                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                } else {
                                    getMessage().send(player, target.getName() + "&c is already wearing&f " + player.getInventory().getHelmet().getType());
                                }
                            }
                        }
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
                if (player.hasPermission("essentials.command.hat.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.hat.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}