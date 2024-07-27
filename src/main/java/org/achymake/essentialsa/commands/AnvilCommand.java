package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnvilCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public AnvilCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.openAnvil(player.getLocation(), true);
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.anvil.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target == player) {
                        target.openAnvil(target.getLocation(), true);
                    } else {
                        if (target != null) {
                            target.openAnvil(target.getLocation(), true);
                            getMessage().send(target, player.getName() + "&6 opened anvil for you");
                            getMessage().send(player, "&6You opened anvil for " + target.getName());
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender commandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.openAnvil(target.getLocation(), true);
                    getMessage().send(commandSender, "You opened anvil for " + target.getName());
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
                if (player.hasPermission("essentials.command.anvil.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }
}