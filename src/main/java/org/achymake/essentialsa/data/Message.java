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

import java.util.List;
import java.util.logging.Level;

public record Message(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private BukkitScheduler getScheduler() {
        return plugin().getScheduler();
    }
    private UpdateChecker getUpdateChecker() {
        return plugin.getUpdateChecker();
    }
    private Server getServer() {
        return plugin.getServer();
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
    public void sendStringList(Player player, List<String> strings) {
        getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (String messages : strings) {
                    send(player, messages.replaceAll("%player%", player.getName()));
                }
            }
        },3);
    }
    public void sendStringList(ConsoleCommandSender consoleCommandSender, List<String> strings) {
        for (String messages : strings) {
            send(consoleCommandSender, messages.replaceAll("%player%", consoleCommandSender.getName()));
        }
    }
    public String getStringBuilder(String[] args, int value) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = value; i < args.length; i++) {
            stringBuilder.append(args[i]);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().strip();
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