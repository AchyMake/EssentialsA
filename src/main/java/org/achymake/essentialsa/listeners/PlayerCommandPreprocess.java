package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
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
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Message getMessage() {
        return plugin.getMessage();
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
        if (getChunks().isEnable()) {
            Chunk chunk = player.getChunk();
            if (getChunks().isClaimed(chunk))return;
            if (!event.getMessage().toLowerCase().startsWith("/sethome"))return;
            if (getChunks().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().send(player, "&cYou are not allowed to sethome inside&f " + getChunks().getOwner(chunk).getName() + "&c's chunk");
        }
    }
}