package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;

public record BlockReceiveGame(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockReceiveGame(BlockReceiveGameEvent event) {
        if (!(event.getEntity() instanceof Player player))return;
        if (!getDatabase().isDisabled(player))return;
        event.setCancelled(true);
    }
}