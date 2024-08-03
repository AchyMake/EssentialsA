package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public record PlayerToggleSneak(EssentialsA plugin) implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getPassenger() != null) {
            Entity passenger = player.getPassenger();
            if (plugin.getCarry().isEnable(passenger)) {
                plugin.getEntities().setSneaking(passenger, !player.isSneaking());
            }
        }
    }
}