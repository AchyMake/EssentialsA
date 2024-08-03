package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public record PlayerLeashEntity(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        } else {
            Entity entity = event.getEntity();
            Chunk chunk = entity.getChunk();
            if (entity.isInsideVehicle()) {
                if (entity.getVehicle() instanceof Player) {
                    event.setCancelled(true);
                }
            }
            if (getVillagers().isNPC(entity)) {
                event.setCancelled(true);
            } else if (getChunkdata().isClaimed(chunk)) {
                if (getChunkdata().hasAccess(player, chunk))return;
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
            }
        }
    }
}