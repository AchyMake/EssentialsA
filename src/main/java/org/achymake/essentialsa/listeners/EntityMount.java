package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Carry;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

public record EntityMount(EssentialsA plugin) implements Listener {
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityMount(EntityMountEvent event) {
        Entity entity = event.getEntity();
        Entity mount = event.getMount();
        Chunk chunk = event.getMount().getChunk();
        if (mount instanceof Player) {
            if (getCarry().isEnable(entity)) {
                getEntities().setScale(entity, 0.5);
            }
        }
        if (getChunkdata().isClaimed(chunk)) {
            if (entity instanceof Player player) {
                if (mount.getType().equals(EntityType.BOAT))return;
                if (mount.getType().equals(EntityType.MINECART))return;
                if (mount.getType().equals(EntityType.ARMOR_STAND))return;
                if (getChunkdata().hasAccess(player, chunk))return;
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
            }
        }
    }
}