package org.achymake.essentialsa.commands.chunks.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.commands.chunks.ChunksSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetOwnerCommand extends ChunksSubCommand {
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
    public SetOwnerCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "setowner";
    }
    @Override
    public String getDescription() {
        return "sets owner of the chunk";
    }
    @Override
    public String getSyntax() {
        return "/chunks setowner";
    }
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("essentials.command.chunks.setowner")) {
                if (args.length == 2) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    Chunk chunk = player.getLocation().getChunk();
                    if (getChunkdata().isAllowedClaim(chunk)) {
                        if (getDatabase().exist(target)) {
                            getChunkdata().setOwner(player, target, chunk);
                            getChunkdata().claimEffect(player, chunk);
                            getChunkdata().claimSound(player);
                            getMessage().send(player, "&6Chunk is now owned by&f " + getChunkdata().getOwner(chunk).getName());
                        } else {
                            getMessage().send(player, target.getName() + "&c has never joined");
                        }
                    } else {
                        getMessage().send(player, "&c&lHey!&7 Sorry but you are not allowed to claim here");
                    }
                }
            }
        }
    }
}