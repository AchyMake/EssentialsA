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
    private Carry getCarry() {
        return plugin.getCarry();
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
        } else if (getCarry().hasMount(entity)) {
            Player player = getCarry().getMount(entity);
            if (player == null)return;
            if (!getCarry().isAllowCarry(entity.getLocation().getBlock()))return;
            getCarry().removeMount(player, entity);
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