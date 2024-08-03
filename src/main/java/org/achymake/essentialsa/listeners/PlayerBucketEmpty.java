package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunks;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.text.MessageFormat;

public record PlayerBucketEmpty(EssentialsA plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockClicked();
        if (getUserdata().isDisabled(player)) {
            event.setCancelled(true);
        }
        if (getChunks().isEnable()) {
            Chunk chunk = block.getChunk();
            if (!getChunks().isClaimed(chunk))return;
            if (!getChunks().isDisableBuckets(event.getBucket()))return;
            if (getChunks().hasAccess(player, chunk))return;
            event.setCancelled(true);
            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
        }
        if (getConfig().getBoolean("notification.enable")) {
            String material = event.getBucket().toString();
            if (!getConfig().getStringList("notification.bucket-empty").contains(material))return;
            String worldName = event.getBlock().getWorld().getName();
            int x = event.getBlock().getX();
            int y = event.getBlock().getY();
            int z = event.getBlock().getZ();
            for (Player players : getServer().getOnlinePlayers()) {
                if (!players.hasPermission("essentials.event.bucket-empty.notify"))return;
                for (String messages : getConfig().getStringList("notification.message")) {
                    players.sendMessage(getMessage().addColor(MessageFormat.format(messages, player.getName(), material, worldName, x, y, z)));
                }
            }
        }
    }
}