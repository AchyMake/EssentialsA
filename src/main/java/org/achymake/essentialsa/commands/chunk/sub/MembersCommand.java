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

public class MembersCommand extends ChunkSubCommand {
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
    public MembersCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "members";
    }
    @Override
    public String getDescription() {
        return "add or removes members to the chunk";
    }
    @Override
    public String getSyntax() {
        return "/chunk members add/remove target";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.members")) {
            if (args.length == 1) {
                if (getDatabase().getConfig(player).getStringList("chunks.members").isEmpty()) {
                    getMessage().send(player, "&cYou do not have any members");
                } else {
                    getMessage().send(player, "&6Chunk Members:");
                    for (OfflinePlayer offlinePlayer : getChunkdata().getMembers(player)) {
                        getMessage().send(player, "- " + offlinePlayer.getName());
                    }
                }
            }
            if (args.length == 3) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                if (args[1].equalsIgnoreCase("add")) {
                    if (getChunkdata().getMembers(player).contains(target)) {
                        getMessage().send(player, target.getName() + "&c is already a member");
                    } else {
                        List<String> members = getDatabase().getConfig(player).getStringList("chunks.members");
                        members.add(target.getUniqueId().toString());
                        getDatabase().setStringList(player, "chunks.members", members);
                        getMessage().send(player, target.getName() + "&6 is now a member");
                    }
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (getDatabase().getConfig(player).getStringList("chunks.members").contains(target.getUniqueId().toString())) {
                        List<String> members = getDatabase().getConfig(player).getStringList("chunks.members");
                        members.remove(target.getUniqueId().toString());
                        getDatabase().setStringList(player, "chunks.members", members);
                        getMessage().send(player, target.getName() + "&6 is now removed from members");
                    } else {
                        getMessage().send(player, target.getName() + "&c is not a member");
                    }
                }
            }
        }
    }
}