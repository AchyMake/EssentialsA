package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Villagers;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;

public record EntityEnterLoveMode(EssentialsA plugin) implements Listener {
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityEnterLoveMode(EntityEnterLoveModeEvent event) {
        Entity entity = event.getEntity();
        if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
        }
    }
}