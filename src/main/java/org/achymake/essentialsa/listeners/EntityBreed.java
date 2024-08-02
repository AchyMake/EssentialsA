package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Villagers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public record EntityBreed(EssentialsA plugin) implements Listener {
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBreed(EntityBreedEvent event) {
        if (!getVillagers().isNPC(event.getEntity()))return;
        event.setCancelled(true);
    }
}