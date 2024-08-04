package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public record BlockRedstone(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        if (!getConfig().getBoolean("disable-redstone"))return;
        event.setNewCurrent(0);
    }
}