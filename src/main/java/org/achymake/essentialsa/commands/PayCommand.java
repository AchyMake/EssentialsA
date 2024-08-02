package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public PayCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (getDatabase().exist(offlinePlayer)) {
                    double amount = Double.parseDouble(args[1]);
                    if (amount >= getConfig().getDouble("economy.minimum-payment")) {
                        if (getEconomy().has(player, amount)) {
                            getEconomy().withdrawPlayer(player, amount);
                            getEconomy().depositPlayer(offlinePlayer, amount);
                            getMessage().send(player, "&6You paid&f " + offlinePlayer.getName() + "&a " + getEconomy().currencyNamePlural() + getEconomy().format(amount));
                        } else {
                            getMessage().send(player, "&cYou don't have&a " + getEconomy().currencyNamePlural() + getEconomy().format(amount) + "&c to pay&f " + offlinePlayer.getName());
                        }
                    } else {
                        getMessage().send(player, "&cYou have to pay at least&a " + getEconomy().currencyNamePlural() + getEconomy().format(getConfig().getDouble("economy.minimum-payment")));
                    }
                } else {
                    getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
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
                for (Player players : getDatabase().getOnlinePlayers()) {
                    commands.add(players.getName());
                }
            }
        }
        return commands;
    }
}