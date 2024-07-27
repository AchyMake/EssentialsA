package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;

public record PlayerMove(EssentialsA plugin) implements Listener {
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (getDatabase().isFrozen(player)) {
            if (getDatabase().hasMoved(event.getFrom(), event.getTo())) {
                event.setCancelled(true);
            }
        }
        if (getDatabase().hasTaskID(player, "teleport")) {
            if (getDatabase().hasMoved(event.getFrom(), event.getTo())) {
                getMessage().sendActionBar(player, "&cYou moved before teleporting!");
                plugin.getScheduler().cancelTask(getDatabase().getTaskID(player, "teleport"));
                getDatabase().removeTaskID(player, "teleport");
            }
        }
        if (event.getTo().getChunk() != event.getFrom().getChunk()) {
            Chunk chunk = event.getTo().getChunk();
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
        if (player.getPassenger() != null) {
            Entity passenger = player.getPassenger();
            if (plugin.getEntities().isEnableCarry(passenger)) {
                if (getDatabase().hasMoved(event.getFrom(), event.getTo())) {
                    plugin.getEntities().addEffects(player);
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