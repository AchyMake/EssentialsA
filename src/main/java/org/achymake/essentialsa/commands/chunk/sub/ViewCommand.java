package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ViewCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    public ViewCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "view";
    }
    @Override
    public String getDescription() {
        return "view claimed chunks";
    }
    @Override
    public String getSyntax() {
        return "/chunk view";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.view")) {
            if (args.length == 1) {
                getChunkdata().chunkView(player, player);
                getChunkdata().claimSound(player);
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.chunk.view.others")) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    if (getDatabase().exist(offlinePlayer)) {
                        getChunkdata().chunkView(player, offlinePlayer);
                        getChunkdata().claimSound(player);
                    }
                }
            }
        }
    }
}