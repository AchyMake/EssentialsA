package org.achymake.essentialsa.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public record PlayerJump(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJump(PlayerJumpEvent event) {
        if (!getDatabase().isFrozen(event.getPlayer()))return;
        event.setCancelled(true);
    }
}