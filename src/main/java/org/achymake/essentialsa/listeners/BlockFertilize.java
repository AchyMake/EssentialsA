package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;

public record BlockFertilize(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        Player player = event.getPlayer();
        if (player == null)return;
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        }
    }
}