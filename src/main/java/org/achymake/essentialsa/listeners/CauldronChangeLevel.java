package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;

public record CauldronChangeLevel(EssentialsA plugin) implements Listener {
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        Entity entity = event.getEntity();
        Chunk chunk = event.getBlock().getChunk();
        if (!getChunks().isEnable())return;
        if (!getChunks().isClaimed(chunk))return;
        if (entity instanceof Player player) {
            if (!getChunks().isDisableCauldronLevelChange())return;
            if (getChunks().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
        }
    }
}