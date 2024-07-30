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

public class HomesCommand implements CommandExecutor, TabCompleter {
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
    public HomesCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().getHomes(player).isEmpty()) {
                    getMessage().send(player, "&cYou haven't set any homes yet");
                } else {
                    getMessage().send(player, "&6Homes:");
                    for (String listedHomes : getDatabase().getHomes(player)) {
                        getMessage().send(player, "- " + listedHomes);
                    }
                }
                return true;
            }
            if (args.length == 3) {
                String arg0 = args[0];
                String target = args[1];
                String targetHome = args[2];
                if (arg0.equalsIgnoreCase("delete")) {
                    if (player.hasPermission("essentials.command.homes.delete")) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
                        if (getDatabase().exist(offlinePlayer)) {
                            if (getDatabase().getHomes(offlinePlayer).contains(targetHome)) {
                                getDatabase().setString(offlinePlayer, "homes." + targetHome, null);
                                getMessage().send(player, "&6Deleted&f " + targetHome + "&6 of&f " + target);
                            } else {
                                getMessage().send(player, target + "&c doesn't have&f " + targetHome);
                            }
                        } else {
                            getMessage().send(player, target + "&c has never joined");
                        }
                        return true;
                    }
                }
                if (arg0.equalsIgnoreCase("teleport")) {
                    if (player.hasPermission("essentials.command.homes.teleport")) {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(target);
                        if (getDatabase().exist(offlinePlayer)) {
                            if (targetHome.equalsIgnoreCase("bed")) {
                                if (offlinePlayer.getBedSpawnLocation() != null) {
                                    player.teleport(offlinePlayer.getBedSpawnLocation());
                                    getMessage().send(player, "&6Teleporting&f " + targetHome + "&6 of&f " + target);
                                } else {
                                    getMessage().send(player, target + "&c do not have a bed");
                                }
                            } else {
                                if (getDatabase().getHomes(offlinePlayer).contains(targetHome)) {
                                    getDatabase().getHome(offlinePlayer, targetHome).getChunk().load();
                                    getMessage().send(player, "&6Teleporting&f " + targetHome + "&6 of&f " + target);
                                    player.teleport(getDatabase().getHome(offlinePlayer, targetHome));
                                } else {
                                    getMessage().send(player, target + "&c doesn't have&f " + targetHome);
                                }
                            }
                        } else {
                            getMessage().send(player, target + "&c has never joined");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.homes.delete")) {
                    commands.add("delete");
                }
                if (player.hasPermission("essentials.command.homes.teleport")) {
                    commands.add("teleport");
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("teleport")) {
                    if (player.hasPermission("essentials.command.homes.teleport")) {
                        for (OfflinePlayer offlinePlayers : getServer().getOfflinePlayers()) {
                            commands.add(offlinePlayers.getName());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("delete")) {
                    if (player.hasPermission("essentials.command.homes.delete")) {
                        for (OfflinePlayer offlinePlayers : getServer().getOfflinePlayers()) {
                            commands.add(offlinePlayers.getName());
                        }
                    }
                }
            }
            if (args.length == 3) {
                if (player.hasPermission("essentials.command.homes.teleport")) {
                    OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[1]);
                    if (getDatabase().exist(offlinePlayer)) {
                        commands.addAll(getDatabase().getHomes(offlinePlayer));
                    }
                }
            }
        }
        return commands;
    }
}