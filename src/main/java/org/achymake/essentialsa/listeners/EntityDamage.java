package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chairs;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public record EntityDamage(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (getEntities().hasMount(entity)) {
            Player player = getEntities().getMount(entity);
            if (player == null)return;
            if (!getEntities().isAllowCarry(entity.getLocation().getBlock()))return;
            getEntities().removeMount(player, entity);
        } else if (entity instanceof Player player) {
            if (!getChairs().hasChair(player))return;
            getChairs().removeOccupied(player.getLocation().getBlock());
            getChairs().dismount(player);
        }
    }
}