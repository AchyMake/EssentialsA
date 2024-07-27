package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RTPCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public RTPCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (getDatabase().hasCooldown(player, "rtp")) {
                    getMessage().sendActionBar(player, "&cYou have to wait&f " + getDatabase().getCooldown(player, "rtp") + "&c seconds");
                } else {
                    getDatabase().addCooldown(player, "rtp");
                    getMessage().sendActionBar(player, "&6Finding safe locations...");
                    getDatabase().randomTeleport(player);
                }
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("force")) {
                    if (player.hasPermission("essentials.command.rtp.force")) {
                        getMessage().sendActionBar(player, "&6Finding safe locations...");
                        getDatabase().randomTeleport(player);
                    }
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.rtp.force")) {
                    commands.add("force");
                }
            }
        }
        return commands;
    }
}