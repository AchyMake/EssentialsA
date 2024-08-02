package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Userdata getUserdata() {
        return plugin.getUserdata();
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
    private Server getServer() {
        return plugin.getServer();
    }
    public BalanceCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getMessage().send(player, "&6Balance:&a " + getEconomy().currencyNamePlural() + getEconomy().format(getEconomy().getBalance(player)));
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.balance.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.balance.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to check&f " + target.getName() + "&c's balance");
                        } else {
                            getMessage().send(player, target.getName() + "&6's balance:&a " + getEconomy().currencyNamePlural() + getEconomy().format(getEconomy().getBalance(target)));
                        }
                    } else {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        if (getUserdata().exist(offlinePlayer)) {
                            getMessage().send(player, offlinePlayer.getName() + "&6's balance:&a " + getEconomy().currencyNamePlural() + getEconomy().format(getEconomy().getBalance(offlinePlayer)));
                        } else {
                            getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
                        }
                    }
                    return true;
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (getUserdata().exist(offlinePlayer)) {
                    getMessage().send(consoleCommandSender, offlinePlayer.getName() + "'s " + getEconomy().currencyNamePlural() + getEconomy().format(getEconomy().getBalance(offlinePlayer)));
                } else {
                    getMessage().send(consoleCommandSender, offlinePlayer.getName() + " has never joined");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.balance.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.balance.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}