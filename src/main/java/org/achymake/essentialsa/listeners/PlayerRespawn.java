package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Spawn;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public record PlayerRespawn(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Spawn getSpawn() {
        return plugin.getSpawn();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!event.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.DEATH))return;
        Player player = event.getPlayer();
        if (player.hasPermission("essentials.event.death.location")) {
            Location location = getUserdata().getLocation(player,"death");
            String world = location.getWorld().getEnvironment().toString().toLowerCase();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            getMessage().send(player, "&6Death location:");
            getMessage().send(player, "&6World:&f " + world + "&6 X:&f " + x + "&6 Y:&f " + y + "&6 Z:&f " + z);
        }
        getUserdata().setBoolean(player, "settings.dead", false);
        if (event.isAnchorSpawn())return;
        if (event.isBedSpawn())return;
        if (getUserdata().locationExist(player, "home")) {
            event.setRespawnLocation(getUserdata().getLocation(player, "home"));
        } else if (getSpawn().locationExist()) {
            event.setRespawnLocation(getSpawn().getLocation());
        }
    }
}