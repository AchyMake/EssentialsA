package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Carry;
import org.achymake.essentialsa.data.Chairs;
import org.achymake.essentialsa.data.Villagers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public record EntityDamage(EssentialsA plugin) implements Listener {
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
        } else if (getCarry().hasMount(entity)) {
            Player player = getCarry().getMount(entity);
            if (player == null)return;
            if (!getCarry().isAllowCarry(entity.getLocation().getBlock()))return;
            getCarry().removeMount(player, entity);
        } else if (entity instanceof Player player) {
            if (!getChairs().hasChair(player))return;
            getChairs().dismount(player);
        }
    }
}