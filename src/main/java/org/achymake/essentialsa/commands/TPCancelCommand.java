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

public class TPCancelCommand implements CommandExecutor, TabCompleter {
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
    public TPCancelCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().getConfig(player).isString("tpa.sent")) {
                    String uuidString = getDatabase().getConfig(player).getString("tpa.sent");
                    UUID uuid = UUID.fromString(uuidString);
                    Player target = getServer().getPlayer(uuid);
                    if (target != null) {
                        int taskID = getDatabase().getConfig(player).getInt("task.tpa");
                        if (getScheduler().isQueued(taskID)) {
                            getScheduler().cancelTask(taskID);
                            getMessage().send(target, player.getName() + "&6 cancelled tpa request");
                            getMessage().send(player, "&6You cancelled tpa request");
                            getDatabase().setString(target, "tpa.from", null);
                            getDatabase().setString(player, "task.tpa", null);
                            getDatabase().setString(player, "tpa.sent", null);
                            return true;
                        }
                    }
                } else {
                    getMessage().send(player, "&cYou haven't sent any tpa request");
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