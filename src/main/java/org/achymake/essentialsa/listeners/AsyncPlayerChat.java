package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public record AsyncPlayerChat(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (getDatabase().isMuted(player)) {
            event.setCancelled(true);
        } else {
            String prefix = getDatabase().prefix(player);
            String displayName = getDatabase().getDisplayName(player);
            String suffix = getDatabase().suffix(player);
            String output = event.getMessage();
            if (player.isOp()) {
                event.setFormat(prefix + getMessage().addColor("&4" + displayName + "&f") + suffix + ChatColor.WHITE + ": " + getMessage().addColor(output));
            } else {
                if (player.hasPermission("essentials.event.chat.color")) {
                    event.setFormat(prefix + displayName + suffix + ChatColor.WHITE + ": " + getMessage().addColor(output));
                } else {
                    event.setFormat(prefix + displayName + suffix + ChatColor.WHITE + ": " + output);
                }
            }
        }
    }
}