package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public record BlockRedstone(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        if (!getConfig().getBoolean("chunks.claim.redstone-only-inside"))return;
        if (getChunkdata().isClaimed(chunk))return;
        event.setNewCurrent(0);
    }
}