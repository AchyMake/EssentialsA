package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhisperCommand implements CommandExecutor, TabCompleter {
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
    public WhisperCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (getDatabase().isMuted(player) || getDatabase().isJailed(player)) {
                return false;
            }
            if (args.length > 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    String builder = getMessage().getStringBuilder(args).toString().strip();
                    getMessage().send(player, "&7You > " + target.getName() + ": " + builder);
                    getMessage().send(target, "&7" + player.getName() + " > You: " + builder);
                    getDatabase().setString(target, "last-whisper", target.getUniqueId().toString());
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (players.hasPermission("essentials.notify.whispers")) {
                            getMessage().send(players, "&7" + player.getName() + " > " + target.getName() + ": " + builder);
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
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (Player players : getServer().getOnlinePlayers()) {
                    if (!plugin.getVanished().contains(players)) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }
}