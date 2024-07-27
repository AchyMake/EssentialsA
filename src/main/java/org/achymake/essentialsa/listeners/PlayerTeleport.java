package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public record PlayerTeleport(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();
        if (!isSelfEvent(teleportCause))return;
        getDatabase().setLocation(event.getPlayer(), "recent");
    }
    private boolean isSelfEvent(PlayerTeleportEvent.TeleportCause teleportCause) {
        return teleportCause.equals(PlayerTeleportEvent.TeleportCause.COMMAND) || teleportCause.equals(PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}