package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorthCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Worth getWorth() {
        return plugin.getWorth();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public WorthCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (getWorth().isSellable(getDatabase().getMaterial(args[0]))) {
                    getMessage().send(player, getDatabase().getMaterial(args[0]) + "&6 is worth:&a " + getEconomy().currencyNamePlural() + getEconomy().format(getWorth().getWorth(getDatabase().getMaterial(args[0]))));
                } else {
                    getMessage().send(player, getDatabase().getMaterial(args[0]).toString() + "&c is not sellable");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (String worthList : getWorth().getList()) {
                    commands.add(worthList.toLowerCase());
                }
            }
        }
        return commands;
    }
}