package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public record EntityChangeBlock(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        Block block = event.getBlock();
        if (getEntities().isEnable(entity)) {
            if (!getEntities().disableBlockChange(entity))return;
            if (entity instanceof Player)return;
            event.setCancelled(true);
        }
    }
}