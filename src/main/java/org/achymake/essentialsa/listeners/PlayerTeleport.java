package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public record PlayerTeleport(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    private Warps getWarps() {
        return plugin.getWarps();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (getWorlds().isPortalEnable(player.getWorld())) {
            event.setCancelled(true);
            if (event.isCancelled()) {
                String warp = getWorlds().getWarp(player.getWorld(), event.getCause());
                if (warp != null) {
                    Location location = getWarps().getLocation(warp);
                    if (location != null) {
                        if (!location.getChunk().isLoaded()) {
                            location.getChunk().load();
                            player.teleport(location);
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (getChairs().hasChair(player)) {
            event.setCancelled(true);
            if (event.isCancelled()) {
                getMessage().send(player, "&cYou can't teleport while using a chair");
            }
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            getDatabase().setLocation(player, "recent");
        }
        if (getWorlds().isPortalEnable(player.getWorld())) {
            event.setCancelled(true);
            if (event.isCancelled()) {
                String warp = getWorlds().getWarp(player.getWorld(), event.getCause());
                if (warp != null) {
                    Location location = getWarps().getLocation(warp);
                    if (location != null) {
                        if (!location.getChunk().isLoaded()) {
                            location.getChunk().load();
                            player.teleport(location);
                        }
                    }
                }
            }
        }
    }
}