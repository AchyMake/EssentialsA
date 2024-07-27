package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EnderChestCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public EnderChestCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.openInventory(player.getEnderChest());
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.enderchest.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target == player) {
                        player.openInventory(target.getEnderChest());
                    } else {
                        if (target != null) {
                            if (!target.hasPermission("essentials.command.enderchest.exempt")) {
                                player.openInventory(target.getEnderChest());
                                getMessage().send(player, "&6Opened enderchest of&f " + target.getName());
                            }
                        }
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
                if (player.hasPermission("essentials.command.enderchest.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.enderchest.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}