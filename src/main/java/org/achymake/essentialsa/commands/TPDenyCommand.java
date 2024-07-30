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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TPDenyCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private BukkitScheduler getScheduler() {
        return Bukkit.getScheduler();
    }
    public TPDenyCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().getConfig(player).isString("tpa.from")) {
                    String uuidString = getDatabase().getConfig(player).getString("tpa.from");
                    UUID uuid = UUID.fromString(uuidString);
                    Player target = getServer().getPlayer(uuid);
                    if (target != null) {
                        int taskID = getDatabase().getConfig(target).getInt("task.tpa");
                        if (getScheduler().isQueued(taskID)) {
                            getScheduler().cancelTask(taskID);
                            getMessage().send(target, player.getName() + "&6 denied tpa request");
                            getMessage().send(player, "&6You denied tpa request");
                            getDatabase().setString(target, "tpa.sent", null);
                            getDatabase().setString(target, "task.tpa", null);
                            getDatabase().setString(player, "tpa.from", null);
                            return true;
                        }
                    }
                } else {
                    getMessage().send(player, "&cYou haven't have any tpa request");
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.EMPTY_LIST;
    }
}