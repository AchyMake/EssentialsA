package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public record SignChange(EssentialsA plugin) implements Listener {
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        if (getChunkdata().isClaimed(chunk)) {
            if (getChunkdata().hasAccess(player, chunk)) {
                if (player.hasPermission("essentials.event.sign.color")) {
                    for (int i = 0; i < event.getLines().length; i++) {
                        if (!event.getLine(i).contains("&"))return;
                        event.setLine(i, getMessage().addColor(event.getLine(i)));
                    }
                }
            } else {
                event.setCancelled(true);
                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
            }
        } else if (player.hasPermission("essentials.event.sign.color")) {
            for (int i = 0; i < event.getLines().length; i++) {
                if (!event.getLine(i).contains("&"))return;
                event.setLine(i, getMessage().addColor(event.getLine(i)));
            }
        }
    }
}