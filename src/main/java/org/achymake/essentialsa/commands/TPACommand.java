package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class TPACommand implements CommandExecutor, TabCompleter {
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
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    public TPACommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (getDatabase().isFrozen(player) || getDatabase().isJailed(player)) {
                    return false;
                } else {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target == null) {
                        getMessage().send(player, args[0] + "&c is currently offline");
                        return true;
                    } else if (target == player) {
                        getMessage().send(player, "&cYou can't send request to your self");
                        return true;
                    } else if (getDatabase().getConfig(player).isString("tpa.sent")) {
                        getMessage().send(player, "&cYou already sent tp request");
                        getMessage().send(player, "&cYou can type&f /tpcancel");
                        return true;
                    } else {
                        int taskID = getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                getDatabase().setString(target, "tpa.from", null);
                                getDatabase().setString(player, "tpa.sent", null);
                                getDatabase().setString(player, "task.tpa", null);
                                getMessage().send(player, "&cTeleport request has expired");
                                getMessage().send(target, "&cTeleport request has expired");
                            }
                        }, 300).getTaskId();
                        getDatabase().setString(target, "tpa.from", player.getUniqueId().toString());
                        getDatabase().setString(player, "tpa.sent", target.getUniqueId().toString());
                        getDatabase().setInt(player, "task.tpa", taskID);
                        getMessage().send(target, player.getName() + "&6 has sent you a tpa request");
                        getMessage().send(target, "&6You can type&a /tpaccept&6 or&c /tpdeny");
                        getMessage().send(player, "&6You have sent a tpa request to&f " + target.getName());
                        getMessage().send(player, "&6You can type&c /tpcancel");
                        return true;
                    }
                }
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