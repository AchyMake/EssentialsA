package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public record PlayerQuit(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
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
        removeTPAonQuit(player);
        getUserdata().setDouble(player, "settings.scale", getUserdata().getScale(player));
        getUserdata().setScale(player, 1.0);
        getUserdata().setLocation(player, "quit");
        if (getChairs().hasChair(player)) {
            getChairs().dismount(player);
        }
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
        if (getCarry().hasPassenger(player)) {
            getCarry().removeMount(player);
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
    private void removeTPAonQuit(Player player) {
        if (getUserdata().getConfig(player).isString("tpa.from")) {
            String uuidString = getUserdata().getConfig(player).getString("tpa.from");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getUserdata().setString(target, "tpa.sent", null);
            int taskID = getUserdata().getConfig(target).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getUserdata().setString(player, "tpa.from", null);
            }
            getUserdata().setString(target, "task.tpa", null);
        } else if (getUserdata().getConfig(player).isString("tpahere.from")) {
            String uuidString = getUserdata().getConfig(player).getString("tpahere.from");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getUserdata().setString(target, "tpahere.sent", null);
            int taskID = getUserdata().getConfig(target).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getUserdata().setString(player, "tpahere.from", null);
            }
            getUserdata().setString(target, "task.tpa", null);
        } else if (getUserdata().getConfig(player).isString("tpa.sent")) {
            String uuidString = getUserdata().getConfig(player).getString("tpa.sent");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getUserdata().setString(target, "tpa.from", null);
            int taskID = getUserdata().getConfig(player).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getUserdata().setString(player, "task.tpa", null);
            }
            getUserdata().setString(player, "tpa.sent", null);
        } else if (getUserdata().getConfig(player).isString("tpahere.sent")) {
            String uuidString = getUserdata().getConfig(player).getString("tpahere.sent");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getUserdata().setString(target, "tpahere.from", null);
            int taskID = getUserdata().getConfig(player).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getUserdata().setString(player, "task.tpa", null);
            }
            getUserdata().setString(player, "tpahere.sent", null);
        }
    }
}