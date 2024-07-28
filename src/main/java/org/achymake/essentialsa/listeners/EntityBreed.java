package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public record EntityBreed(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBreed(EntityBreedEvent event) {
        if (!getEntities().isNPC(event.getEntity()))return;
        event.setCancelled(true);
    }
}