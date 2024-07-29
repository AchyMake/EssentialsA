package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Levels;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public record PlayerLevelChange(EssentialsA plugin) implements Listener {
    private Levels getLevels() {
        return plugin.getLevels();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        if (!getLevels().isEnable())return;
        Player player = event.getPlayer();
        int level = player.getLevel();
        int oldLevel = event.getOldLevel();
        if (level > oldLevel) {
            getLevels().effectUp(player);
            getLevels().levelChanged(player);
        } else if (oldLevel > level) {
            getLevels().effectDown(player);
            getLevels().levelChanged(player);
        }
    }
}