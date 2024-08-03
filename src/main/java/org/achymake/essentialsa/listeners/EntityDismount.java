package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Carry;
import org.achymake.essentialsa.data.Chairs;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public record EntityDismount(EssentialsA plugin) implements Listener {
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Carry getCarry() {
        return plugin.getCarry();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDismount(EntityDismountEvent event) {
        Entity dismounted = event.getDismounted();
        Entity entity = event.getEntity();
        if (dismounted instanceof Player) {
            if (getCarry().isEnable(entity)) {
                if (entity.isSneaking()) {
                    event.setCancelled(true);
                } else {
                    getEntities().setScale(entity, 1);
                    getEntities().setAI(entity, true);
                }
            }
        }
        if (event.getEntity() instanceof Player player) {
            if (event.getDismounted() instanceof ArmorStand) {
                getChairs().dismount(player);
            }
        }
    }
}