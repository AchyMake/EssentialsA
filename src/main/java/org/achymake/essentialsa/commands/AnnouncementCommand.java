package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class AnnouncementCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public AnnouncementCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                for (Player players : getServer().getOnlinePlayers()) {
                    getMessage().send(players, "&6Server:&f " + announcement(args));
                }
                return true;
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            if (args.length > 0) {
                for (Player players : getServer().getOnlinePlayers()) {
                    getMessage().send(players, "&6Server:&f " + announcement(args));
                }
                return true;
            }
        }
        return true;
    }
    private String announcement(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String words : args) {
            stringBuilder.append(words);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().strip();
    }
    @Override
    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.EMPTY_LIST;
    }
}