package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public record PlayerPortal(EssentialsA plugin) implements Listener {
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            if (!getWorlds().isPortalEnable())return;
            event.setCancelled(true);
            getWorlds().teleport(player, "NETHER");
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            if (!getWorlds().isPortalEnable())return;
            event.setCancelled(true);
            getWorlds().teleport(player, "END");
        }
    }
}