package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StoreCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    private Message getMessage() {
        return plugin.getMessage();
    }

    private Server getServer() {
        return plugin.getServer();
    }

    public StoreCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        }
        if (args.length == 1) {
            if (sender.hasPermission("essentials.command.store.other")) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    sendHelp(target);
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
                if (player.hasPermission("essentials.command.store.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }

    private void sendHelp(CommandSender sender) {
        if (sender instanceof Player player) {
            if (getConfig().isList("store")) {
                for (String messages : getConfig().getStringList("store")) {
                    getMessage().send(player, messages.replaceAll("%player%", player.getName()));
                }
            } else if (getConfig().isString("help")) {
                getMessage().send(player, getConfig().getString("store").replaceAll("%player%", player.getName()));
            }
        }
        if (sender instanceof ConsoleCommandSender commandSender) {
            if (getConfig().isList("store")) {
                for (String messages : getConfig().getStringList("store")) {
                    getMessage().send(commandSender, messages.replaceAll("%player%", commandSender.getName()));
                }
            } else if (getConfig().isString("store")) {
                getMessage().send(commandSender, getConfig().getString("store").replaceAll("%player%", commandSender.getName()));
            }
        }
    }
}