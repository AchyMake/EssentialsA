package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MOTDCommand implements CommandExecutor, TabCompleter {
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
    public MOTDCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                sendMotd(player, "welcome");
            }
            if (args.length == 1) {
                sendMotd(player, args[0]);
            }
        }
        if (args.length == 2) {
            if (sender.hasPermission("essentials.command.motd.other")) {
                Player target = getServer().getPlayerExact(args[1]);
                if (target != null) {
                    sendMotd(target, args[0]);
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
                commands.addAll(getConfig().getConfigurationSection("message-of-the-day").getKeys(false));
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.motd.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }
    private void sendMotd(Player player, String motd) {
        if (getConfig().isList("message-of-the-day." + motd)) {
            for (String messages : getConfig().getStringList("message-of-the-day." + motd)) {
                getMessage().send(player, messages.replaceAll("%player%", player.getName()));
            }
        }
        if (getConfig().isString("message-of-the-day." + motd)) {
            getMessage().send(player, getConfig().getString("message-of-the-day." + motd).replaceAll("%player%", player.getName()));
        }
    }
}