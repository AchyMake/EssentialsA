package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FreezeCommand implements CommandExecutor, TabCompleter {
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
    public FreezeCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target == player) {
                    getDatabase().setBoolean(target, "settings.frozen", !getDatabase().isFrozen(target));
                    if (getDatabase().isFrozen(target)) {
                        getMessage().send(player, "&6You froze&f " + target.getName());
                    } else {
                        getMessage().send(player, "&6You unfroze&f " + target.getName());
                    }
                } else {
                    if (target != null) {
                        if (target.hasPermission("essentials.command.freeze.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to freeze&f " + target.getName());
                        } else {
                            getDatabase().setBoolean(target, "settings.frozen", !getDatabase().isFrozen(target));
                            if (getDatabase().isFrozen(target)) {
                                getMessage().send(player, "&6You froze&f " + target.getName());
                            } else {
                                getMessage().send(player, "&6You unfroze&f " + target.getName());
                            }
                        }
                    } else {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        if (getDatabase().exist(offlinePlayer)) {
                            getDatabase().setBoolean(offlinePlayer, "settings.frozen", !getDatabase().isFrozen(offlinePlayer));
                            if (getDatabase().isFrozen(offlinePlayer)) {
                                getMessage().send(player, "&6You froze&f " + offlinePlayer.getName());
                            } else {
                                getMessage().send(player, "&6You unfroze&f " + offlinePlayer.getName());
                            }
                        } else {
                            getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
                        }
                    }
                }
                return true;
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    getDatabase().setBoolean(target, "settings.frozen", !getDatabase().isFrozen(target));
                    if (getDatabase().isFrozen(target)) {
                        getMessage().send(consoleCommandSender, "You froze " + target.getName());
                    } else {
                        getMessage().send(consoleCommandSender, "You unfroze " + target.getName());
                    }
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (getDatabase().exist(offlinePlayer)) {
                        getDatabase().setBoolean(offlinePlayer, "settings.frozen", !getDatabase().isFrozen(offlinePlayer));
                        if (getDatabase().isFrozen(offlinePlayer)) {
                            getMessage().send(consoleCommandSender, "You froze " + offlinePlayer.getName());
                        } else {
                            getMessage().send(consoleCommandSender, "You unfroze " + offlinePlayer.getName());
                        }
                    } else {
                        getMessage().send(consoleCommandSender, offlinePlayer.getName() + " has never joined");
                    }
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
                for (Player players : getDatabase().getOnlinePlayers()) {
                    if (!players.hasPermission("essentials.command.freeze.exempt")) {
                        commands.add(players.getName());
                    }
                }
                commands.add(player.getName());
            }
        }
        return commands;
    }
}