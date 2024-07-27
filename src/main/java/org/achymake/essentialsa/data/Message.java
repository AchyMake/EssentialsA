package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public record Message(EssentialsA plugin) {
    public void send(ConsoleCommandSender sender, String message) {
        sender.sendMessage(message);
    }
    public void send(Player player, String message) {
        player.sendMessage(addColor(message));
    }
    public void sendActionBar(Player player, String message) {
        player.sendActionBar(addColor(message));
    }
    public String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public StringBuilder getStringBuilder(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
            stringBuilder.append(" ");
        }
        return stringBuilder;
    }
    public void sendLog(Level level, String message) {
        plugin.getLogger().log(level, message);
    }
}