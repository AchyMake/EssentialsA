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
import org.bukkit.event.player.PlayerBucketEntityEvent;

public record PlayerBucketEntity(EssentialsA plugin) implements Listener {
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
    public void onPlayerBucketEntity(PlayerBucketEntityEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getEntity().getChunk();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        }
        if (getChunks().isEnable()) {
            if (!getChunks().isClaimed(chunk))return;
            if (!getChunks().isDisableBuckets(event.getEntityBucket().getType()))return;
            if (getChunks().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
        }
    }
}