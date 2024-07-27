package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

public record EntityMount(EssentialsA plugin) implements Listener {
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityMount(EntityMountEvent event) {
        Chunk chunk = event.getMount().getChunk();
        if (!getChunkdata().isClaimed(chunk))return;
        if (!(event.getEntity() instanceof Player player))return;
        if (getChunkdata().hasAccess(player, chunk))return;
        if (event.getMount() instanceof ArmorStand)return;
        if (event.getMount() instanceof Boat)return;
        if (event.getMount() instanceof Minecart)return;
        event.setCancelled(true);
        getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
    }
}