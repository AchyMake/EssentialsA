package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Warps;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public record EntityPortalEnter(EssentialsA plugin) implements Listener {
    private Warps getWarps() {
        return plugin.getWarps();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityPortalEnterEvent(EntityPortalEnterEvent event) {
        Entity entity = event.getEntity();
        World world = event.getLocation().getWorld();
        if (entity instanceof Player)return;
        if (world.getEnvironment().equals(World.Environment.NORMAL)) {
            if (event.getPortalType().equals(PortalType.NETHER)) {
                if (!getWorlds().worldExist(world.getName() + "_nether")) {
                    event.setCancelled(true);
                }
            }
            if (event.getPortalType().equals(PortalType.ENDER)) {
                if (!getWorlds().worldExist(world.getName() + "_the_end")) {
                    event.setCancelled(true);
                }
            }
        } else if (world.getEnvironment().equals(World.Environment.THE_END)) {
            if (event.getPortalType().equals(PortalType.ENDER)) {
                if (!getWorlds().worldExist(world.getName().replace("_tne_end", ""))) {
                    event.setCancelled(true);
                }
            }
        } else if (world.getEnvironment().equals(World.Environment.NETHER)) {
            if (event.getPortalType().equals(PortalType.NETHER)) {
                if (!getWorlds().worldExist(world.getName().replace("_nether", ""))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}