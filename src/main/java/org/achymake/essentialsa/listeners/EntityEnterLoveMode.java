package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Villagers;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;

public record EntityEnterLoveMode(EssentialsA plugin) implements Listener {
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityEnterLoveMode(EntityEnterLoveModeEvent event) {
        Entity entity = event.getEntity();
        if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
        } else {
            if (event.getHumanEntity() instanceof Player player) {
                if (!getChunks().isEnable())return;
                Chunk chunk = entity.getChunk();
                if (!getChunks().isClaimed(chunk))return;
                if (getChunks().hasAccess(player, chunk))return;
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
            }
        }
    }
}