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

public class BanCommand extends ChunkSubCommand {
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
    public BanCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "ban";
    }
    @Override
    public String getDescription() {
        return "bans certain player";
    }
    @Override
    public String getSyntax() {
        return "/chunk ban target";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.ban")) {
            if (args.length == 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (getChunkdata().getBanned(player).contains(target)) {
                    getMessage().send(player, target.getName() + "&c is already banned");
                } else {
                    if (getChunkdata().getMembers(player).contains(target)) {
                        List<String> members = getChunkdata().getMembersUUIDString(player);
                        List<String> banned = getChunkdata().getBannedUUIDString(player);
                        members.remove(target.getUniqueId().toString());
                        banned.add(target.getUniqueId().toString());
                        getDatabase().setStringList(player, "chunks.members", members);
                        getDatabase().setStringList(player, "chunks.banned", banned);
                        getMessage().send(player, "&6You banned&f " + target.getName());
                    } else {
                        List<String> banned = getChunkdata().getBannedUUIDString(player);
                        banned.add(target.getUniqueId().toString());
                        getDatabase().setStringList(player, "chunks.banned", banned);
                        getMessage().send(player, "&6You banned&f " + target.getName());
                    }
                }
            }
        }
    }
}