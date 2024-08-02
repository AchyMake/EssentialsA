package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Villagers;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public record EntityTarget(EssentialsA plugin) implements Listener {
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player)return;
        if (event.getTarget() == null)return;
        Entity target = event.getTarget();
        if (getVillagers().isNPC(target)) {
            event.setCancelled(true);
        } else {
            Chunk chunk = entity.getChunk();
            if (getChunkdata().isClaimed(chunk)) {
                if (!(target instanceof Player player))return;
                if (getChunkdata().hasAccess(player, chunk))return;
                if (getEntities().isHostile(entity))return;
                event.setCancelled(true);
            } else if (getEntities().disableTarget(entity, target)) {
                event.setCancelled(true);
            }
        }
    }
}