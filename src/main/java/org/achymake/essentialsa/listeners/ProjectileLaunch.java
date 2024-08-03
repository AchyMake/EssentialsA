package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Carry;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public record ProjectileLaunch(EssentialsA plugin) implements Listener {
    private Carry getCarry() {
        return plugin.getCarry();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        Entity shooter = (Entity) event.getEntity().getShooter();
        if (shooter instanceof Player player) {
            Entity passenger = player.getPassenger();
            if (player.getPassenger() != null) {
                if (!getCarry().isEnable(passenger))return;
                if (!player.hasPermission("essentials.carry.shoot"))return;
                getEntities().launchProjectile(passenger, projectile.getClass(), projectile.getVelocity());
            }
        }
    }
}