package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public record PlayerLogin(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
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
        if (getServer().isWhitelistEnforced()) {
            if (getServer().getWhitelistedPlayers().contains(player)) {
                if (getServer().getOnlinePlayers().size() >= getServer().getMaxPlayers()) {
                    if (player.hasPermission("essentials.event.login.full-server")) {
                        if (getUserdata().exist(player)) {
                            if (getUserdata().isBanned(player)) {
                                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage().addColor("Reason: " + getUserdata().getBanReason(player)));
                            } else {
                                event.allow();
                                getUserdata().setup(player);
                            }
                        } else {
                            event.allow();
                            getUserdata().setup(player);
                        }
                    }
                } else {
                    if (getUserdata().exist(player)) {
                        if (getUserdata().isBanned(player)) {
                            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage().addColor("Reason: " + getUserdata().getBanReason(player)));
                        } else {
                            event.allow();
                            getUserdata().setup(player);
                        }
                    } else {
                        event.allow();
                        getUserdata().setup(player);
                    }
                }
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Server is currently Whitelisted");
            }
        } else {
            if (getServer().getOnlinePlayers().size() >= getServer().getMaxPlayers()) {
                if (player.hasPermission("essentials.event.login.full-server")) {
                    if (getUserdata().exist(player)) {
                        if (getUserdata().isBanned(player)) {
                            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage().addColor("Reason: " + getUserdata().getBanReason(player)));
                        } else {
                            event.allow();
                            getUserdata().setup(player);
                        }
                    } else {
                        event.allow();
                        getUserdata().setup(player);
                    }
                }
            } else {
                if (getUserdata().exist(player)) {
                    if (getUserdata().isBanned(player)) {
                        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMessage().addColor("Reason: " + getUserdata().getBanReason(player)));
                    } else {
                        event.allow();
                        getUserdata().setup(player);
                    }
                } else {
                    event.allow();
                    getUserdata().setup(player);
                }
            }
        }
    }
}