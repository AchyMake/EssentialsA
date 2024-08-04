package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
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
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEntity(PlayerBucketEntityEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getEntity().getChunk();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        }
    }
}