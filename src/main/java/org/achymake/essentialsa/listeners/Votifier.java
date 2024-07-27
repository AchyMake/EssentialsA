package org.achymake.essentialsa.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Reward;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public record Votifier(EssentialsA plugin) implements Listener {
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Reward getReward() {
        return plugin.getReward();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVote(VotifierEvent event) {
        if (!getReward().isEnable())return;
        Vote vote = event.getVote();
        String username = vote.getUsername();
        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(username);
        String sn = vote.getServiceName();
        int voted = getDatabase().getConfig(offlinePlayer).getInt("voted") + 1;
        getDatabase().setInt(offlinePlayer, "voted", voted);
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            getReward().giveReward(player);
        }
        for (Player players : getServer().getOnlinePlayers()) {
            getMessage().send(players, offlinePlayer.getName() + "&6 has voted on&f " + sn);
        }
    }
}