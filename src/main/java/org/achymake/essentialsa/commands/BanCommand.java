package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BanCommand implements CommandExecutor, TabCompleter {
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
    public BanCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (getDatabase().isBanned(offlinePlayer)) {
                    getMessage().send(player, offlinePlayer.getName() + "&c is already banned");
                } else {
                    getDatabase().setBoolean(offlinePlayer, "settings.banned", true);
                    getDatabase().setString(offlinePlayer, "settings.ban-reason", "None&6:&7 by " + player.getName());
                    getMessage().send(player, "You banned " + offlinePlayer.getName() + " for no reason");
                }
            }
            if (args.length > 1) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (offlinePlayer.isBanned()) {
                    getMessage().send(player, offlinePlayer.getName() + "&c is already banned");
                } else {
                    getDatabase().setBoolean(offlinePlayer, "settings.banned", true);
                    getDatabase().setString(offlinePlayer, "settings.ban-reason", args(args) + "&6:&7 by " + player.getName());
                    getMessage().send(player, "You have banned " + offlinePlayer.getName() + " for " + args(args));
                }
            }
        }
        return true;
    }
    private String args(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String words : args) {
            stringBuilder.append(words);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().strip();
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (OfflinePlayer offlinePlayer : getServer().getOfflinePlayers()) {
                    commands.add(offlinePlayer.getName());
                }
            }
        }
        return commands;
    }
}