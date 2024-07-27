package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worth;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetWorthCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Worth getWorth() {
        return plugin.getWorth();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public SetWorthCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (isEmpty(player)) {
                    getMessage().send(player, "&cYou have to hold an item");
                } else {
                    double value = Double.parseDouble(args[0]);
                    Material material = player.getInventory().getItemInMainHand().getType();
                    if (value > 0) {
                        getWorth().setWorth(material, value);
                        getMessage().send(player, material + "&6 is now worth&a " + getEconomy().currency() + getEconomy().format(value));
                    } else {
                        getWorth().setWorth(material, value);
                        getMessage().send(player, material + "&6 is now worthless");
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
            if (!isEmpty(player)) {
                if (args.length == 1) {
                    commands.add("0.0625");
                    commands.add("0.125");
                    commands.add("0.25");
                    commands.add("0.50");
                    commands.add("0.75");
                }
            }
        }
        return commands;
    }
    private boolean isEmpty(Player player) {
        return player.getInventory().getItemInMainHand().getType().equals(Material.AIR);
    }
}