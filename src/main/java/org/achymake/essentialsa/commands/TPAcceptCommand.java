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

public class TPAcceptCommand implements CommandExecutor, TabCompleter {
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
    public TPAcceptCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().getConfig(player).isString("tpa.from")) {
                    Player target = getServer().getPlayer(UUID.fromString(getDatabase().getConfig(player).getString("tpa.from")));
                    if (target != null) {
                        int taskID = getDatabase().getConfig(target).getInt("task.tpa");
                        if (getScheduler().isQueued(taskID)) {
                            getScheduler().cancelTask(taskID);
                            target.teleport(player);
                            getMessage().sendActionBar(target, "&6Teleporting to&f " + player.getName());
                            getMessage().send(player, "&6You accepted&f " + target.getName() + "&6 tpa request");
                            getDatabase().setString(target, "tpa.sent", null);
                            getDatabase().setString(target, "task.tpa", null);
                            getDatabase().setString(player, "tpa.from", null);
                        }
                    }
                } else if (getDatabase().getConfig(player).isString("tpahere.from")) {
                    Player target = getServer().getPlayer(UUID.fromString(getDatabase().getConfig(player).getString("tpahere.from")));
                    if (target != null) {
                        int taskID = getDatabase().getConfig(target).getInt("task.tpa");
                        if (getScheduler().isQueued(taskID)) {
                            getScheduler().cancelTask(taskID);
                            player.teleport(target);
                            getMessage().sendActionBar(target, "&6Teleporting to&f " + player.getName());
                            getMessage().send(player, "&6You accepted&f " + target.getName() + "&6's tpahere request");
                            getDatabase().setString(target, "tpahere.sent", null);
                            getDatabase().setString(target, "task.tpa", null);
                            getDatabase().setString(player, "tpahere.from", null);
                        }
                    }
                } else  {
                    getMessage().send(player, "&cYou don't have any tpa request");
                }
            }
        }
        return true;
    }
    @Override
    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.EMPTY_LIST;
    }
}