package org.achymake.essentialsa.listeners;

import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public record PlayerShearBlock(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerShearEntity(PlayerShearBlockEvent event) {
        Player player = event.getPlayer();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        }
    }
}