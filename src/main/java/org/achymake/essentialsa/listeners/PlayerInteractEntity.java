package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public record PlayerInteractEntity(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Chunk chunk = entity.getChunk();
        if (isDisabled(player)) {
            event.setCancelled(true);
        } else if (getEntities().isNPC(entity)) {
            event.setCancelled(true);
            if (getEntities().hasCommand(entity)) {
                if (getEntities().isCommandPlayer(entity)) {
                    player.getServer().dispatchCommand(player, getEntities().getCommand(entity));
                }
                if (getEntities().isCommandConsole(entity)) {
                    player.getServer().dispatchCommand(player.getServer().getConsoleSender(), getEntities().getCommand(entity).replaceAll("%player%", player.getName()));
                }
            }
        } else if (getChunkdata().isClaimed(chunk)) {
            if (getEntities().isHostile(entity))return;
            if (entity.isInvulnerable())return;
            if (getChunkdata().hasAccess(player, chunk)) {
                if (!player.isSneaking())return;
                if (player.getPassenger() != null)return;
                if (entity.getPassenger() != null)return;
                if (getEntities().isNPC(entity))return;
                if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
                if (!getEntities().isEnableCarry(entity))return;
                if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
                event.setCancelled(true);
                getEntities().addMount(player, entity);
                player.swingMainHand();
            } else {
                if (entity.getType().equals(EntityType.PLAYER))return;
                if (entity.getType().equals(EntityType.MINECART))return;
                if (entity.getType().equals(EntityType.BOAT))return;
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
            }
        } else {
            if (!player.isSneaking())return;
            if (player.getPassenger() != null)return;
            if (entity.getPassenger() != null)return;
            if (getEntities().isNPC(entity))return;
            if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
            if (!getEntities().isEnableCarry(entity))return;
            if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
            event.setCancelled(true);
            getEntities().addMount(player, entity);
            player.swingMainHand();
        }
    }
    private boolean isDisabled(Player player) {
        return getDatabase().isFrozen(player) || getDatabase().isJailed(player);
    }
}