package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public record PlayerCommandPreprocess(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("essentials.event.command.exempt"))return;
        for (String disabled : getConfig().getStringList("commands.disable")) {
            if (event.getMessage().toLowerCase().startsWith("/" + disabled.toLowerCase())) {
                event.setCancelled(true);
            }
        }
    }
}