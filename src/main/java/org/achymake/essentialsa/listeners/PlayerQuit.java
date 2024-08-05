package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public record PlayerQuit(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private List<Player> getChunkEditors() {
        return plugin.getChunkEditors();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeTeleportTask(player);
        getUserdata().saveQuit(player);
        getUserdata().resetScale(player);
        if (getUserdata().isVanished(player)) {
            removeVanishTask(player);
            plugin.getVanished().remove(player);
            event.setQuitMessage(null);
        } else {
            if (getConfig().getBoolean("connection.quit.enable")) {
                event.setQuitMessage(quitMessage(player));
                playSound();
            } else {
                if (player.hasPermission("essentials.event.quit.message")) {
                    event.setQuitMessage(quitMessage(player));
                    playSound();
                } else {
                    event.setQuitMessage(null);
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (players.hasPermission("essentials.event.quit.notify")) {
                            getMessage().send(players, player.getName() + "&7 left the Server");
                        }
                    }
                }
            }
        }
        if (getChunkEditors().contains(player)) {
            getChunkEditors().remove(player);
        }
    }
    private String quitMessage(Player player) {
        return getMessage().addColor(getConfig().getString("connection.quit.message").replaceAll("%player%", player.getName()));
    }
    private void removeTeleportTask(Player player) {
        if (getUserdata().hasTaskID(player, "teleport")) {
            plugin.getScheduler().cancelTask(getUserdata().getTaskID(player, "teleport"));
            getUserdata().removeTaskID(player, "teleport");
        }
    }
    private void removeVanishTask(Player player) {
        if (getUserdata().hasTaskID(player, "vanish")) {
            plugin.getScheduler().cancelTask(getUserdata().getTaskID(player, "vanish"));
            getUserdata().removeTaskID(player, "vanish");
        }
    }
    private void playSound() {
        if (getConfig().getBoolean("connection.quit.sound.enable")) {
            String soundType = getConfig().getString("connection.quit.sound.type");
            float soundVolume = (float) getConfig().getDouble("connection.quit.sound.volume");
            float soundPitch = (float) getConfig().getDouble("connection.quit.sound.pitch");
            for (Player players : getServer().getOnlinePlayers()) {
                players.playSound(players, Sound.valueOf(soundType), soundVolume, soundPitch);
            }
        }
    }
}