package org.achymake.essentialsa.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public record PlayerJump(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJump(PlayerJumpEvent event) {
        if (!getUserdata().isFrozen(event.getPlayer()))return;
        event.setCancelled(true);
    }
}