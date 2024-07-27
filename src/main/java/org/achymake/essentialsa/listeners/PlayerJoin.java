package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.MessageFormat;

public record PlayerJoin(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getDatabase().getUpdate(player);
        if (getDatabase().isVanished(player)) {
            getDatabase().setVanish(player, true);
            getMessage().send(player, "&6You joined back vanished");
            event.setJoinMessage(null);
        } else {
            getDatabase().hideVanished(player);
            if (getConfig().getBoolean("connection.join.enable")) {
                sendSound();
                event.setJoinMessage(joinMessage(player));
            } else {
                if (player.hasPermission("essentials.event.join.message")) {
                    sendSound();
                    event.setJoinMessage(joinMessage(player));
                } else {
                    event.setJoinMessage(null);
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (players.hasPermission("essentials.event.join.notify")) {
                            getMessage().send(players, player.getName() + "&7 joined the Server");
                        }
                    }
                }
            }
            if (getDatabase().hasJoined(player)) {
                sendMotd(player, "welcome-back");
            } else {
                sendMotd(player, "welcome");
            }
        }
    }
    private String joinMessage(Player player) {
        return getMessage().addColor(MessageFormat.format(getConfig().getString("connection.join.message"), player.getName()));
    }
    private void sendSound() {
        if (getConfig().getBoolean("connection.join.sound.enable")) {
            String soundType = getConfig().getString("connection.join.sound.type");
            float soundVolume = (float) getConfig().getDouble("connection.join.sound.volume");
            float soundPitch = (float) getConfig().getDouble("connection.join.sound.pitch");
            for (Player players : getServer().getOnlinePlayers()) {
                players.playSound(players, Sound.valueOf(soundType), soundVolume, soundPitch);
            }
        }
    }
    private void sendMotd(Player player, String motd) {
        if (getConfig().isList("message-of-the-day." + motd)) {
            for (String messages : getConfig().getStringList("message-of-the-day." + motd)) {
                getMessage().send(player, messages.replaceAll("%player%", player.getName()));
            }
        } else if (getConfig().isString("message-of-the-day." + motd)) {
            getMessage().send(player, getConfig().getString("message-of-the-day." + motd).replaceAll("%player%", player.getName()));
        }
    }
}