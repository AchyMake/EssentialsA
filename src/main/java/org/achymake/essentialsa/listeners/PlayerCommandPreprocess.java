package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
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
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getChunk();
        if (getChunkdata().isClaimed(chunk)) {
            if (!event.getMessage().startsWith("/sethome"))return;
            if (getChunkdata().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().send(player, "&cYou are not allowed to sethome inside&f " + getChunkdata().getOwner(chunk).getName() + "&c's chunk");
        } else {
            if (player.hasPermission("essentials.event.command.exempt"))return;
            for (String disabled : getConfig().getStringList("commands.disable")) {
                if (event.getMessage().toLowerCase().startsWith("/" + disabled.toLowerCase())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}