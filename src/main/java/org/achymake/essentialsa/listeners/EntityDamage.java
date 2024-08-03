package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public record EntityDamage(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Chairs getChairs() {
        return plugin.getChairs();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
        } else if (entity.isInsideVehicle()) {
            if (plugin.getCarry().getMount(entity) instanceof Player player) {
                entity.leaveVehicle();
                plugin.getEntities().setScale(entity, 1);
                plugin.getScheduler().cancelTask(plugin.getUserdata().getTaskID(player, "carry"));
                plugin.getUserdata().removeTaskID(player, "carry");
            }
        } else if (entity instanceof Player player) {
            if (getChairs().hasChair(player)) {
                getChairs().dismount(player);
            } else {
                if (plugin.getConfig().getBoolean("teleport.cancel-on-damage")) {
                    if (getUserdata().hasTaskID(player, "teleport")) {
                        getMessage().sendActionBar(player, "&cYou moved before teleporting!");
                        plugin.getScheduler().cancelTask(getUserdata().getTaskID(player, "teleport"));
                        getUserdata().removeTaskID(player, "teleport");
                    }
                }
            }
        }
    }
}