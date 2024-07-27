package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class UnBanCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public UnBanCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "unban";
    }
    @Override
    public String getDescription() {
        return "unbans certain player";
    }
    @Override
    public String getSyntax() {
        return "/chunk unban target";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.unban")) {
            if (args.length == 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (getChunkdata().getBanned(player).contains(target.getUniqueId().toString())) {
                    List<String> banned = getChunkdata().getBannedUUIDString(player);
                    banned.remove(target.getUniqueId().toString());
                    getDatabase().setStringList(player, "chunks.banned", banned);
                    getMessage().send(player, "&6You banned&f " + target.getName() + "&6 from you're chunks");
                } else {
                    getMessage().send(player, target.getName() + "&c is already banned");
                }
            }
        }
    }
}