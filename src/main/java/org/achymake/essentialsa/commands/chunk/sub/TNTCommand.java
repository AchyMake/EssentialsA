package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class TNTCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public TNTCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "tnt";
    }
    @Override
    public String getDescription() {
        return "toggle tnt";
    }
    @Override
    public String getSyntax() {
        return "/chunk tnt";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.tnt")) {
            if (args.length == 1) {
                Chunk chunk = player.getLocation().getChunk();
                if (getChunkdata().isClaimed(chunk)) {
                    if (getChunkdata().isOwner(player, chunk)) {
                        if (getChunkdata().isTNTAllowed(chunk)) {
                            getChunkdata().toggleTNT(chunk, false);
                            getMessage().send(player, "&6Disabled&f TNT&6 for current chunk");
                        } else {
                            getChunkdata().toggleTNT(chunk, true);
                            getMessage().send(player, "&6Enabled&f TNT&6 for current chunk");
                        }
                    } else {
                        if (player.hasPermission("essentialschunks.command.chunks.edit")) {
                            if (getChunkdata().isTNTAllowed(chunk)) {
                                getChunkdata().toggleTNT(chunk, false);
                                getMessage().send(player, "&6Disabled&f TNT&6 for current chunk");
                            } else {
                                getChunkdata().toggleTNT(chunk, true);
                                getMessage().send(player, "&6Enabled&f TNT&6 for current chunk");
                            }
                        } else {
                            getMessage().send(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    }
                } else {
                    getMessage().send(player, "&cChunk is unclaimed");
                }
            }
        }
    }
}