package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VoteCommand implements CommandExecutor, TabCompleter {
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
    public VoteCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendVote(sender);
        }
        if (args.length == 1) {
            if (sender.hasPermission("essentials.command.vote.others")) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    sendVote(target);
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
                if (player.hasPermission("essentials.command.vote.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }
    private void sendVote(CommandSender sender) {
        if (sender instanceof Player player) {
            if (getConfig().isList("vote")) {
                for (String messages : getConfig().getStringList("vote")) {
                    getMessage().send(player, messages.replaceAll("%player%", player.getName()));
                }
            } else if (getConfig().isString("vote")) {
                getMessage().send(player, getConfig().getString("vote").replaceAll("%player%", player.getName()));
            }
        }
        if (sender instanceof ConsoleCommandSender commandSender) {
            if (getConfig().isList("vote")) {
                for (String messages : getConfig().getStringList("vote")) {
                    getMessage().send(commandSender, messages.replaceAll("%player%", commandSender.getName()));
                }
            } else if (getConfig().isString("vote")) {
                getMessage().send(commandSender, getConfig().getString("vote").replaceAll("%player%", commandSender.getName()));
            }
        }
    }
}