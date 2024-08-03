package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;

public record BlockFertilize(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if (player == null)return;
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        } else if (getChunks().isEnable()) {
            if (!getChunks().isClaimed(chunk))return;
            if (!getChunks().isDisableBlockFertilize())return;
            if (getChunks().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
        }
    }
}