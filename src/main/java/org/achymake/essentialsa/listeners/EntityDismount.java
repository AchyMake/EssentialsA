package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public record EntityDismount(EssentialsA plugin) implements Listener {
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDismounted() instanceof ArmorStand) {
                getChairs().dismount(player, player.getLocation().add(0, 1, 0).getBlock());
            }
        }
    }
}