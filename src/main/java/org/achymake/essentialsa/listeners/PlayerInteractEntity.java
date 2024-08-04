package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public record PlayerInteractEntity(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        } else if (getVillagers().isNPC(entity)) {
            event.setCancelled(true);
            if (getVillagers().hasCommand(entity)) {
                if (getVillagers().isCommandPlayer(entity)) {
                    Villager villager = (Villager) entity;
                    villager.playEffect(EntityEffect.VILLAGER_HAPPY);
                    villager.shakeHead();
                    player.getServer().dispatchCommand(player, getVillagers().getCommand(entity));
                }
                if (getVillagers().isCommandConsole(entity)) {
                    Villager villager = (Villager) entity;
                    villager.playEffect(EntityEffect.VILLAGER_HAPPY);
                    villager.shakeHead();
                    player.getServer().dispatchCommand(player.getServer().getConsoleSender(), getVillagers().getCommand(entity).replaceAll("%player%", player.getName()));
                }
            }
        }
    }
}