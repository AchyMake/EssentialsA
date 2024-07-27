package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.ArrayList;
import java.util.List;

public record EntitySpawn(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player)return;
        if (!getEntities().isEnable(entity))return;
        if (getEntities().disableSpawn(entity)) {
            event.setCancelled(true);
        } else if (getEntities().getChunkLimit(entity) > 0) {
            Chunk chunk = event.getLocation().getChunk();
            List<Entity> listed = new ArrayList<>();
            for (Entity entities : chunk.getEntities()) {
                if (entities.getType().equals(entity.getType())) {
                    listed.add(entity);
                }
            }
            if (listed.size() >= getEntities().getChunkLimit(entity)) {
                event.setCancelled(true);
            }
            listed.clear();
        }
    }
}