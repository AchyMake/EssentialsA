package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
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
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        Chunk chunk = event.getLocation().getChunk();
        if (getChunks().isEnable()) {
            if (!getChunks().isClaimed(chunk))return;
            switch (entity) {
                case TNTPrimed tntPrimed -> {
                    if (getChunks().isTNTAllowed(chunk))return;
                    event.blockList().clear();
                }
                case ExplosiveMinecart explosiveMinecart -> {
                    if (getChunks().isTNTAllowed(chunk))return;
                    event.blockList().clear();
                }
                default -> {
                    if (!getEntities().isEnable(entity))return;
                    if (!getEntities().disableBlockDamage(entity))return;
                    if (entity instanceof Player)return;
                    event.blockList().clear();
                }
            }
        }
        if (getEntities().isEnable(entity)) {
            if (!getEntities().disableBlockDamage(entity))return;
            if (entity instanceof Player)return;
            event.blockList().clear();
        }
    }
}