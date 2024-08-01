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
    private Portals getPortals() {
        return plugin.getPortals();
    }
    private Warps getWarps() {
        return plugin.getWarps();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerPortalEvent event) {
        if (getPortals().isEnable()) {
            Player player = event.getPlayer();
            String warp = getPortals().getWarp(player.getWorld(), event.getCause());
            if (warp != null) {
                Location location = getWarps().getLocation(warp);
                if (location != null) {
                    event.setCancelled(true);
                    if (event.isCancelled()) {
                        location.getChunk().load();
                        player.teleport(location);
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
            getMessage().send(player, "&cYou can't teleport while using a chair");
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            getDatabase().setLocation(player, "recent");
        }
        if (getPortals().isEnable()) {
            String warp = getPortals().getWarp(player.getWorld(), event.getCause());
            if (warp != null) {
                Location location = getWarps().getLocation(warp);
                if (location != null) {
                    event.setCancelled(true);
                    if (event.isCancelled()) {
                        location.getChunk().load();
                        player.teleport(location);
                    }
                }
            }
        }
    }
}