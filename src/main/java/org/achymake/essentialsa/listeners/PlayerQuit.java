package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chairs;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
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

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

public record PlayerQuit(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private List<Player> getChunkEditors() {
        return plugin.getChunkEditors();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeTeleportTask(player);
        removeTPAonQuit(player);
        getDatabase().setLocation(player, "quit");
        if (getChairs().hasChair(player)) {
            getChairs().dismount(player);
        }
        if (getDatabase().isVanished(player)) {
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
        if (player.getPassenger() != null) {
            Entity passenger = player.getPassenger();
            if (!plugin.getEntities().isEnableCarry(passenger))return;
            plugin.getEntities().removeMount(player, passenger);
        }
    }
    private String quitMessage(Player player) {
        return getMessage().addColor(getConfig().getString("connection.quit.message").replaceAll("%player%", player.getName()));
    }
    private void removeTeleportTask(Player player) {
        if (getDatabase().hasTaskID(player, "teleport")) {
            plugin.getScheduler().cancelTask(getDatabase().getTaskID(player, "teleport"));
            getDatabase().removeTaskID(player, "teleport");
        }
    }
    private void removeVanishTask(Player player) {
        if (getDatabase().hasTaskID(player, "vanish")) {
            plugin.getScheduler().cancelTask(getDatabase().getTaskID(player, "vanish"));
            getDatabase().removeTaskID(player, "vanish");
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
        if (getDatabase().getConfig(player).isString("tpa.from")) {
            String uuidString = getDatabase().getConfig(player).getString("tpa.from");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getDatabase().setString(target, "tpa.sent", null);
            int taskID = getDatabase().getConfig(target).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getDatabase().setString(player, "tpa.from", null);
            }
            getDatabase().setString(target, "task.tpa", null);
        } else if (getDatabase().getConfig(player).isString("tpahere.from")) {
            String uuidString = getDatabase().getConfig(player).getString("tpahere.from");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getDatabase().setString(target, "tpahere.sent", null);
            int taskID = getDatabase().getConfig(target).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getDatabase().setString(player, "tpahere.from", null);
            }
            getDatabase().setString(target, "task.tpa", null);
        } else if (getDatabase().getConfig(player).isString("tpa.sent")) {
            String uuidString = getDatabase().getConfig(player).getString("tpa.sent");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getDatabase().setString(target, "tpa.from", null);
            int taskID = getDatabase().getConfig(player).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getDatabase().setString(player, "task.tpa", null);
            }
            getDatabase().setString(player, "tpa.sent", null);
        } else if (getDatabase().getConfig(player).isString("tpahere.sent")) {
            String uuidString = getDatabase().getConfig(player).getString("tpahere.sent");
            UUID uuid = UUID.fromString(uuidString);
            OfflinePlayer target = getServer().getOfflinePlayer(uuid);
            getDatabase().setString(target, "tpahere.from", null);
            int taskID = getDatabase().getConfig(player).getInt("task.tpa");
            if (plugin.getScheduler().isQueued(taskID)) {
                plugin.getScheduler().cancelTask(taskID);
                getDatabase().setString(player, "task.tpa", null);
            }
            getDatabase().setString(player, "tpahere.sent", null);
        }
    }
}