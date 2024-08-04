package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public record EntityExplode(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (getEntities().isEnable(entity)) {
            if (!getEntities().disableBlockDamage(entity))return;
            if (entity instanceof Player)return;
            event.blockList().clear();
        }
    }
}