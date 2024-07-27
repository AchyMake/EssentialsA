package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
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
                if (!player.getInventory().getItemInMainHand().getType().isAir()) {
                    if (player.getInventory().getHelmet() == null) {
                        getMessage().send(player, "&6You are now wearing&f " + player.getInventory().getItemInMainHand().getType());
                        player.getInventory().setHelmet(getDatabase().getItem(player.getInventory().getItemInMainHand().getType().toString(), 1));
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    } else {
                        getMessage().send(player, "&cYou are already wearing&f " + player.getInventory().getHelmet().getType());
                    }
                } else {
                    getMessage().send(player, "&cYou have to hold an item");
                }
            }
        }
        return true;
    }
    @Override
    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.EMPTY_LIST;
    }
}