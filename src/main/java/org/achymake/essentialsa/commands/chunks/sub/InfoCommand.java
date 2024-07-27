package org.achymake.essentialsa.commands.chunks.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.commands.chunks.ChunksSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends ChunksSubCommand {
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
    public InfoCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "info";
    }
    @Override
    public String getDescription() {
        return "checks chunk info";
    }
    @Override
    public String getSyntax() {
        return "/chunks info";
    }
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("essentials.command.chunks.info")) {
                if (args.length == 1) {
                    Chunk chunk = player.getLocation().getChunk();
                    if (getChunkdata().isClaimed(chunk)) {
                        getMessage().send(player, "&6Chunks Info:&f Chunk");
                        getMessage().send(player, "&6Owner:&f " + getChunkdata().getOwner(chunk).getName());
                        getMessage().send(player, "&6Date claimed:&f " + getChunkdata().getDateClaimed(chunk));
                        getMessage().send(player, "&6Chunks claimed:&f " + getChunkdata().getClaimCount(chunk));
                        if (getChunkdata().getMembers(getChunkdata().getOwner(chunk)).isEmpty()) {
                            getMessage().send(player, getChunkdata().getOwner(chunk).getName() + "&6 has no members");
                        } else {
                            getMessage().send(player, getChunkdata().getOwner(chunk).getName()+"&6 members:");
                            for (OfflinePlayer offlinePlayer : getChunkdata().getMembers(getChunkdata().getOwner(chunk))) {
                                getMessage().send(player, "- " + offlinePlayer.getName());
                            }
                        }
                    }
                }
                if (args.length == 2) {
                    OfflinePlayer target = player.getServer().getOfflinePlayer(args[1]);
                    if (getDatabase().exist(target)) {
                        getMessage().send(player, "&6Chunks Info:&f "+target.getName());
                        getMessage().send(player, "&6Chunks claimed:&f " + getChunkdata().getClaimCount(target));
                        if (getChunkdata().getMembers(target).isEmpty()) {
                            getMessage().send(player, target.getName() + "&6 has no members");
                        } else {
                            getMessage().send(player, "&6Members:");
                            for (OfflinePlayer offlinePlayer : getChunkdata().getMembers(target)) {
                                getMessage().send(player, "- " + offlinePlayer.getName());
                            }
                        }
                    }
                }
            }
        }
    }
}