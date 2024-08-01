package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
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
            if (event.isCancelled()) {
                if (getEntities().hasCommand(entity)) {
                    if (getEntities().isCommandPlayer(entity)) {
                        Villager villager = (Villager) entity;
                        villager.playEffect(EntityEffect.VILLAGER_HAPPY);
                        villager.shakeHead();
                        player.getServer().dispatchCommand(player, getEntities().getCommand(entity));
                    }
                    if (getEntities().isCommandConsole(entity)) {
                        Villager villager = (Villager) entity;
                        villager.playEffect(EntityEffect.VILLAGER_HAPPY);
                        villager.shakeHead();
                        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), getEntities().getCommand(entity).replaceAll("%player%", player.getName()));
                    }
                }
            }
        } else if (getChunkdata().isClaimed(chunk)) {
            if (entity.isInvulnerable())return;
            if (entity instanceof Player)return;
            if (getChunkdata().hasAccess(player, chunk)) {
                if (player.isSneaking()) {
                    if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
                    if (!getEntities().isEnableCarry(entity))return;
                    if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
                    event.setCancelled(true);
                    if (event.isCancelled()) {
                        getEntities().carry(player, entity, true);
                    }
                } else {
                    if (!getEntities().hasPassenger(player))return;
                    if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
                    if (!getEntities().isEnableCarry(entity))return;
                    if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
                    event.setCancelled(true);
                    if (event.isCancelled()) {
                        getEntities().stack(player, entity);
                    }
                }
            } else {
                if (getEntities().isHostile(entity))return;
                if (entity.getType().equals(EntityType.PLAYER))return;
                if (entity.getType().equals(EntityType.MINECART))return;
                if (entity.getType().equals(EntityType.BOAT))return;
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
            }
        } else {
            if (entity.isInvulnerable())return;
            if (player.isSneaking()) {
                if (entity instanceof Player)return;
                if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
                if (!getEntities().isEnableCarry(entity))return;
                if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
                event.setCancelled(true);
                if (event.isCancelled()) {
                    getEntities().carry(player, entity, true);
                }
            } else {
                if (!getEntities().hasPassenger(player))return;
                if (entity instanceof Player)return;
                if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
                if (!getEntities().isEnableCarry(entity))return;
                if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
                event.setCancelled(true);
                if (event.isCancelled()) {
                    getEntities().stack(player, entity);
                }
            }
        }
    }
    private boolean isDisabled(Player player) {
        return getDatabase().isFrozen(player) || getDatabase().isJailed(player);
    }
}