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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RespondCommand implements CommandExecutor, TabCompleter {
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
    public RespondCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!getDatabase().isMuted(player)) {
                if (args.length > 0) {
                    if (getDatabase().getConfig(player).isString("last-whisper")) {
                        String uuidString = getDatabase().getConfig(player).getString("last-whisper");
                        UUID uuid = UUID.fromString(uuidString);
                        Player target = player.getServer().getPlayer(uuid);
                        if (target != null) {
                            getMessage().send(player, "&7You > " + target.getName() + ": " + getMessage().getStringBuilder(args, 1));
                            getMessage().send(target, "&7" + player.getName() + " > You: " + getMessage().getStringBuilder(args, 1));
                            getDatabase().setString(target, "last-whisper", player.getUniqueId().toString());
                            for (Player players : getServer().getOnlinePlayers()) {
                                if (players.hasPermission("essentials.notify.whispers")) {
                                    getMessage().send(players, "&7" + player.getName() + " > " + target.getName() + ": " + getMessage().getStringBuilder(args, 1));
                                }
                            }
                            return true;
                        }
                    }
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