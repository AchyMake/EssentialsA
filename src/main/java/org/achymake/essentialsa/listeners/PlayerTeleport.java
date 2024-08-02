package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public record PlayerTeleport(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            if (!getWorlds().isPortalEnable())return;
            event.setCancelled(true);
            getWorlds().teleport(player, "NETHER");
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            if (!getWorlds().isPortalEnable())return;
            event.setCancelled(true);
            getWorlds().teleport(player, "END");
        } else {
            if (getChairs().hasChair(player)) {
                event.setCancelled(true);
                getMessage().send(player, "&cYou can't teleport while using a chair");
            } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.COMMAND) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                getUserdata().setLocation(player, "recent");
            }
        }
    }
}