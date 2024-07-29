package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
        if (getDatabase().isVanished(player)) {
            getDatabase().setVanish(player, true);
            getMessage().send(player, "&6You joined back vanished");
            event.setJoinMessage(null);
        } else {
            getDatabase().hideVanished(player);
            if (getDatabase().hasJoined(player)) {
                getDatabase().sendMotd(player, "welcome-back");
            } else {
                getDatabase().sendMotd(player, "welcome");
            }
            if (getConfig().getBoolean("connection.join.enable")) {
                getDatabase().sendJoinSound();
                event.setJoinMessage(getMessage().addColor(getConfig().getString("connection.join.message").replaceAll("%player%", player.getName())));
            } else if (player.hasPermission("essentials.event.join.message")) {
                getDatabase().sendJoinSound();
                event.setJoinMessage(getMessage().addColor(getConfig().getString("connection.join.message").replaceAll("%player%", player.getName())));
            } else {
                event.setJoinMessage(null);
                for (Player players : getServer().getOnlinePlayers()) {
                    if (players.hasPermission("essentials.event.join.notify")) {
                        getMessage().send(players, player.getName() + "&7 joined the Server");
                    }
                }
            }
        }
        if (player.hasPermission("essentials.event.join.update")) {
            getDatabase().getUpdate(player);
        }
    }
}