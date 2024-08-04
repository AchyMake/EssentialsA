package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Villagers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public record EntityTargetLivingEntity(EssentialsA plugin) implements Listener {
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player)return;
        if (event.getTarget() == null)return;
        Entity target = event.getTarget();
        if (getVillagers().isNPC(target)) {
            event.setCancelled(true);
        }
        if (getEntities().disableTarget(entity, target)) {
            event.setCancelled(true);
        }
    }
}