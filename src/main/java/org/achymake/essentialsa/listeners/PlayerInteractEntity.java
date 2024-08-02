package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
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
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Entities getEntities() {
        return plugin.getEntities();
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
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        } else if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
            if (getVillagers().hasCommand(entity)) {
                if (getVillagers().isCommandPlayer(entity)) {
                    Villager villager = (Villager) entity;
                    villager.playEffect(EntityEffect.VILLAGER_HAPPY);
                    villager.shakeHead();
                    player.getServer().dispatchCommand(player, getVillagers().getCommand(entity));
                }
                if (getVillagers().isCommandConsole(entity)) {
                    Villager villager = (Villager) entity;
                    villager.playEffect(EntityEffect.VILLAGER_HAPPY);
                    villager.shakeHead();
                    player.getServer().dispatchCommand(player.getServer().getConsoleSender(), getVillagers().getCommand(entity).replaceAll("%player%", player.getName()));
                }
            }
        } else if (getChunkdata().isClaimed(chunk)) {
            if (entity.isInvulnerable())return;
            if (entity instanceof Player)return;
            if (getChunkdata().hasAccess(player, chunk)) {
                if (!getCarry().isAllowCarry(entity.getLocation().getBlock()))return;
                if (!getCarry().isEnable(entity))return;
                if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
                if (!player.getInventory().getItemInMainHand().isEmpty())return;
                if (!player.getInventory().getItemInOffHand().isEmpty())return;
                if (player.isSneaking()) {
                    event.setCancelled(true);
                    getCarry().carry(player, entity, true);
                } else {
                    event.setCancelled(true);
                    getCarry().stack(player, entity);
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
            if (entity instanceof Player)return;
            if (!getCarry().isAllowCarry(entity.getLocation().getBlock()))return;
            if (!getCarry().isEnable(entity))return;
            if (!player.hasPermission("essentials.carry." + entity.getType().toString().toLowerCase()))return;
            if (!player.getInventory().getItemInMainHand().isEmpty())return;
            if (!player.getInventory().getItemInOffHand().isEmpty())return;
            if (player.isSneaking()) {
                event.setCancelled(true);
                getCarry().carry(player, entity, true);
            } else {
                event.setCancelled(true);
                getCarry().stack(player, entity);
            }
        }
    }
}