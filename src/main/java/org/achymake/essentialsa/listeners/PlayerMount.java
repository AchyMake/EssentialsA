package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

public record PlayerMount(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getMount() instanceof ArmorStand)return;
            if (!isDisabled(player))return;
            event.setCancelled(true);
        }
    }
    private boolean isDisabled(Player player) {
        return getDatabase().isFrozen(player) || getDatabase().isJailed(player);
    }
}