package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public record PlayerDeath(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getPassenger() != null) {
            Entity passenger = player.getPassenger();
            passenger.leaveVehicle();
            plugin.getEntities().setScale(passenger, 1);
            plugin.getScheduler().cancelTask(plugin.getUserdata().getTaskID(player, "carry"));
            plugin.getUserdata().removeTaskID(player, "carry");
        }
        getUserdata().setLocation(player, "death");
        if (getConfig().getInt("deaths.drop-player-head.chance") > new Random().nextInt(100)) {
            if (getConfig().getBoolean("deaths.drop-player-head.enable")) {
                event.getDrops().add(getDatabase().getOfflinePlayerHead(player, 1));
            } else if (player.hasPermission("essentials.event.death.player-head")) {
                event.getDrops().add(getDatabase().getOfflinePlayerHead(player, 1));
            }
        }
    }
}