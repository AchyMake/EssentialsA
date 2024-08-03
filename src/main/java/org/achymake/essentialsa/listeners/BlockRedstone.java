package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public record BlockRedstone(EssentialsA plugin) implements Listener {
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        if (!getChunks().isEnable())return;
        if (!getChunks().getConfig().getBoolean("claim.redstone-only-inside"))return;
        if (getChunks().isClaimed(chunk))return;
        event.setNewCurrent(0);
    }
}