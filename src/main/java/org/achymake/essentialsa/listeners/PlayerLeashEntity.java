package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public record PlayerLeashEntity(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        }
        if (entity.isInsideVehicle()) {
            if (entity.getVehicle() instanceof Player) {
                event.setCancelled(true);
            }
        }
        if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
        }
    }
}