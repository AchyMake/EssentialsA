package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public record EntityExplode(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Chunk chunk = event.getLocation().getChunk();
        if (getChunkdata().isClaimed(chunk)) {
            switch (entity) {
                case TNTPrimed tntPrimed -> {
                    if (getChunkdata().isTNTAllowed(chunk))return;
                    event.blockList().clear();
                }
                case ExplosiveMinecart explosiveMinecart -> {
                    if (getChunkdata().isTNTAllowed(chunk))return;
                    event.blockList().clear();
                }
                default -> {
                    if (entity instanceof Player)return;
                    if (!getEntities().isEnable(entity))return;
                    if (!getEntities().disableBlockDamage(entity))return;
                    event.blockList().clear();
                }
            }
        } else {
            if (entity instanceof Player)return;
            if (!getEntities().isEnable(entity))return;
            if (!getEntities().disableBlockDamage(entity))return;
            event.blockList().clear();
        }
    }
}