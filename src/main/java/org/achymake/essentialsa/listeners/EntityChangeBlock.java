package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
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
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        if (getChunks().isEnable()) {
            if (!getChunks().isClaimed(chunk))return;
            if (entity instanceof Player player) {
                if (!getChunks().isDisabledChangeBlocks(block))return;
                if (getChunks().hasAccess(player, chunk))return;
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
            } else if (getEntities().isHostile(entity)) {
                event.setCancelled(true);
            }
        }
        if (getEntities().isEnable(entity)) {
            if (!getEntities().disableBlockChange(entity))return;
            if (entity instanceof Player)return;
            event.setCancelled(true);
        }
    }
}