package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

public record PlayerMove(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (getUserdata().isFrozen(player)) {
            if (getDatabase().hasMoved(from, to)) {
                event.setCancelled(true);
            }
        } else {
            if (getUserdata().hasTaskID(player, "teleport")) {
                if (getConfig().getBoolean("teleport.cancel-on-move")) {
                    if (getDatabase().hasMoved(from, to)) {
                        getMessage().sendActionBar(player, "&cYou moved before teleporting!");
                        getScheduler().cancelTask(getUserdata().getTaskID(player, "teleport"));
                        getUserdata().removeTaskID(player, "teleport");
                    }
                }
            }
            if (player.getPassenger() != null) {
                Entity passenger = player.getPassenger();
                if (getCarry().isEnable(passenger)) {
                    passenger.setRotation(player.getYaw(), player.getPitch());
                    if (getDatabase().hasMoved(from, to)) {
                        getCarry().addEffects(player);
                    }
                }
            }
            if (to.getChunk() != from.getChunk()) {
                Chunk chunk = to.getChunk();
                if (getChunkdata().isClaimed(chunk)) {
                    if (getChunkdata().isBanned(chunk, player)) {
                        if (plugin.getChunkEditors().contains(player)) {
                            visit(player, getChunkdata().getOwner(chunk));
                        } else {
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cYou are banned from&f " + getChunkdata().getOwner(chunk).getName() + "&c's chunk");
                        }
                    } else {
                        visit(player, getChunkdata().getOwner(chunk));
                    }
                } else {
                    exit(player);
                }
            }
        }
    }
    private void visit(Player player, OfflinePlayer owner) {
        if (player.getPersistentDataContainer().has(NamespacedKey.minecraft("chunk-visitor"), PersistentDataType.STRING)) {
            if (!player.getPersistentDataContainer().get(NamespacedKey.minecraft("chunk-visitor"), PersistentDataType.STRING).equals(owner.getName())) {
                player.getPersistentDataContainer().remove(NamespacedKey.minecraft("chunk-visitor"));
            }
        } else {
            getMessage().sendActionBar(player, "&6Visiting&f " + owner.getName() + "&6's chunk");
            player.getPersistentDataContainer().set(NamespacedKey.minecraft("chunk-visitor"), PersistentDataType.STRING, owner.getName());
        }
    }
    private void exit(Player player) {
        if (player.getPersistentDataContainer().has(NamespacedKey.minecraft("chunk-visitor"), PersistentDataType.STRING)) {
            String lastChunkOwner = player.getPersistentDataContainer().get(NamespacedKey.minecraft("chunk-visitor"), PersistentDataType.STRING);
            getMessage().sendActionBar(player, "&6Exited&f " + lastChunkOwner + "&6's chunk");
            player.getPersistentDataContainer().remove(NamespacedKey.minecraft("chunk-visitor"));
        }
    }
}