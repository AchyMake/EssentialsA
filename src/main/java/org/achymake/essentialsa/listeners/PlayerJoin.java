package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
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
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();;
        getUserdata().setScale(player, getUserdata().getConfig(player).getDouble("settings.scale"));
        if (getUserdata().isVanished(player)) {
            getUserdata().setVanish(player, true);
            getMessage().send(player, "&6You joined back vanished");
            event.setJoinMessage(null);
        } else {
            getUserdata().hideVanished(player);
            if (getUserdata().hasJoined(player)) {
                getMessage().sendStringList(player, getConfig().getStringList("message-of-the-day.welcome-back"));
            } else {
                getMessage().sendStringList(player, getConfig().getStringList("message-of-the-day.welcome"));
            }
            if (getConfig().getBoolean("connection.join.enable")) {
                getMessage().sendJoinSound();
                event.setJoinMessage(getMessage().addColor(getConfig().getString("connection.join.message").replaceAll("%player%", player.getName())));
            } else if (player.hasPermission("essentials.event.join.message")) {
                getMessage().sendJoinSound();
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
            getMessage().getUpdate(player);
        }
    }
}