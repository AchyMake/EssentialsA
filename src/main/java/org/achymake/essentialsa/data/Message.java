package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.net.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Level;

public record Message(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private BukkitScheduler getScheduler() {
        return plugin().getScheduler();
    }
    private UpdateChecker getUpdateChecker() {
        return plugin.getUpdateChecker();
    }
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
    public void send(String path, Player player) {
        if (getConfig().isList(path)) {
            for (String messages : getConfig().getStringList(path)) {
                send(player, messages.replaceAll("%player%", player.getName()));
            }
        } else if (getConfig().isString(path)) {
            send(player, getConfig().getString(path).replaceAll("%player%", player.getName()));
        }
    }
    public void send(String path, ConsoleCommandSender consoleCommandSender) {
        if (getConfig().isList(path)) {
            for (String messages : getConfig().getStringList(path)) {
                send(consoleCommandSender, messages.replaceAll("%player%", consoleCommandSender.getName()));
            }
        } else if (getConfig().isString(path)) {
            send(consoleCommandSender, getConfig().getString(path).replaceAll("%player%", consoleCommandSender.getName()));
        }
    }
    public void sendMotd(Player player, String motd) {
        getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                send("message-of-the-day." + motd, player);
            }
        }, 3);
    }
    public void sendMotd(ConsoleCommandSender consoleCommandSender, String motd) {
        send("message-of-the-day." + motd, consoleCommandSender);
    }
    public StringBuilder getStringBuilder(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
            stringBuilder.append(" ");
        }
        return stringBuilder;
    }
    public void getUpdate(Player player) {
        getUpdateChecker().getUpdate(player);
    }
    public void sendJoinSound() {
        if (getConfig().getBoolean("connection.join.sound.enable")) {
            String soundType = getConfig().getString("connection.join.sound.type");
            float soundVolume = (float) getConfig().getDouble("connection.join.sound.volume");
            float soundPitch = (float) getConfig().getDouble("connection.join.sound.pitch");
            for (Player players : getServer().getOnlinePlayers()) {
                players.playSound(players, Sound.valueOf(soundType), soundVolume, soundPitch);
            }
        }
    }
    public void sendLog(Level level, String message) {
        plugin.getLogger().log(level, message);
    }
}