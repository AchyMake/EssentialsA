package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
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
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
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
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    if (target.hasPermission("essentials.command.ban.exempt")) {
                        getMessage().send(player, "&cYou are not allowed to ban&f " + target.getName());
                    } else {
                        if (getUserdata().isBanned(target)) {
                            getMessage().send(player, target.getName() + "&c is already banned");
                        } else {
                            getUserdata().setBoolean(target, "settings.banned", true);
                            getUserdata().setString(target, "settings.ban-reason", "None&6:&7 by " + player.getName());
                            target.kickPlayer(getUserdata().getConfig(target).getString("settings.ban-reason"));
                            getMessage().send(player, "You banned " + target.getName() + " for no reason");
                        }
                    }
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (getUserdata().isBanned(offlinePlayer)) {
                        getMessage().send(player, offlinePlayer.getName() + "&c is already banned");
                    } else {
                        getUserdata().setBoolean(offlinePlayer, "settings.banned", true);
                        getUserdata().setString(offlinePlayer, "settings.ban-reason", "None&6:&7 by " + player.getName());
                        getMessage().send(player, "You banned " + offlinePlayer.getName() + " for no reason");
                    }
                }
                return true;
            }
            if (args.length > 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    if (target.hasPermission("essentials.command.ban.exempt")) {
                        getMessage().send(player, "&cYou are not allowed to ban&f " + target.getName());
                    } else {
                        if (getUserdata().isBanned(target)) {
                            getMessage().send(player, target.getName() + "&c is already banned");
                        } else {
                            getUserdata().setBoolean(target, "settings.banned", true);
                            getUserdata().setString(target, "settings.ban-reason", "None&6:&7 by " + player.getName());
                            target.kickPlayer(getUserdata().getConfig(target).getString("settings.ban-reason"));
                            getMessage().send(player, "You banned " + target.getName() + " for no reason");
                        }
                    }
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (getUserdata().isBanned(offlinePlayer)) {
                        getMessage().send(player, offlinePlayer.getName() + "&c is already banned");
                    } else {
                        getUserdata().setBoolean(offlinePlayer, "settings.banned", true);
                        getUserdata().setString(offlinePlayer, "settings.ban-reason", "None&6:&7 by " + player.getName());
                        getMessage().send(player, "You banned " + offlinePlayer.getName() + " for no reason");
                    }
                }
                return true;
            }
        }
        return false;
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
                for (Player players : getDatabase().getOnlinePlayers()) {
                    commands.add(players.getName());
                }
            }
        }
        return commands;
    }
}