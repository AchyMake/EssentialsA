package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand implements CommandExecutor, TabCompleter {
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
    public GiveCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    String type = args[1];
                    getDatabase().giveItem(target, getDatabase().getItem(type, 1));
                    getMessage().send(player, "&6You gave&f " + target.getName() + " &61&f " + type);
                }
            }
            if (args.length == 3) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    String type = args[1];
                    int amount = Integer.parseInt(args[2]);
                    getDatabase().giveItem(target, getDatabase().getItem(type, amount));
                    getMessage().send(player, "&6You gave&f " + target.getName() + " &6" + amount + "&f " + type);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (Player players : getServer().getOnlinePlayers()) {
                    commands.add(players.getName());
                }
            }
            if (args.length == 2) {
                for (Material test : Material.values()) {
                    commands.add(test.toString().toLowerCase());
                }
            }
            if (args.length == 3) {
                commands.add("8");
                commands.add("16");
                commands.add("32");
                commands.add("64");
            }
        }
        return commands;
    }
}