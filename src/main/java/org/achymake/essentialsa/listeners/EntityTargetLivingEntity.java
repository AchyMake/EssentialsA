package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public record EntityTargetLivingEntity(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        Chunk chunk = entity.getChunk();
        if (event.getTarget() == null)return;
        if (event.getEntity() instanceof Player)return;
        if (getChunkdata().isClaimed(chunk)) {
            if (!(event.getTarget() instanceof Player player))return;
            if (getChunkdata().hasAccess(player, chunk))return;
            if (getEntities().isHostile(entity))return;
            event.setCancelled(true);
        } else if (getEntities().disableTarget(event.getEntity(), event.getTarget())) {
            event.setCancelled(true);
        }
    }
}