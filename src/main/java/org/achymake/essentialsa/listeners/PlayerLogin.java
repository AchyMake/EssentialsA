package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public record PlayerLogin(EssentialsA plugin) implements Listener {
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (getServer().getOnlinePlayers().size() >= getServer().getMaxPlayers()) {
            if (player.hasPermission("essentials.event.login.full-server")) {
                if (getDatabase().exist(player)) {
                    if (getDatabase().isBanned(player)) {
                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage().addColor("Reason: " + getDatabase().getBanReason(player)));
                    } else {
                        event.allow();
                        getDatabase().setup(player);
                    }
                } else {
                    event.allow();
                    getDatabase().setup(player);
                }
            }
        } else {
            if (getDatabase().exist(player)) {
                if (getDatabase().isBanned(player)) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage().addColor("Reason: " + getDatabase().getBanReason(player)));
                } else {
                    event.allow();
                    getDatabase().setup(player);
                }
            } else {
                event.allow();
                getDatabase().setup(player);
            }
        }
    }
}