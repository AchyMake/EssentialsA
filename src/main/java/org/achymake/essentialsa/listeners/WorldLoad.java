package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;

public record WorldLoad(EssentialsA plugin) implements Listener {
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldLoad(WorldLoadEvent event) {
        if (getWorlds().getFile(event.getWorld()).exists())return;
        getWorlds().createFile(event.getWorld());
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldSave(WorldSaveEvent event) {
        if (getWorlds().getFile(event.getWorld()).exists())return;
        getWorlds().createFile(event.getWorld());
    }
}