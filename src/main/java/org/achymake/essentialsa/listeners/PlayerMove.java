package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;

public record PlayerMove(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (getUserdata().isFrozen(player)) {
            if (getDatabase().hasMoved(from, to)) {
                event.setCancelled(true);
            }
        } else {
            if (getUserdata().hasTaskID(player, "teleport")) {
                if (getConfig().getBoolean("teleport.cancel-on-move")) {
                    if (getDatabase().hasMoved(from, to)) {
                        getMessage().sendActionBar(player, "&cYou moved before teleporting!");
                        getScheduler().cancelTask(getUserdata().getTaskID(player, "teleport"));
                        getUserdata().removeTaskID(player, "teleport");
                    }
                }
            }
        }
    }
}