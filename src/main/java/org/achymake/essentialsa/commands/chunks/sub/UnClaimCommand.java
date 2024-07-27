package org.achymake.essentialsa.commands.chunks.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.chunks.ChunksSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnClaimCommand extends ChunksSubCommand {
    private final EssentialsA plugin;
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public UnClaimCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "unclaim";
    }
    @Override
    public String getDescription() {
        return "unclaim current chunk";
    }
    @Override
    public String getSyntax() {
        return "/chunks unclaim";
    }
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (sender.hasPermission("essentials.command.chunks.unclaim")) {
                if (args.length == 1) {
                    Chunk chunk = player.getLocation().getChunk();
                    if (getChunkdata().isClaimed(chunk)) {
                        getMessage().send(player, "&6You safely unclaimed&f " + getChunkdata().getOwner(chunk).getName() + "&6's chunk");
                        getChunkdata().remove(getChunkdata().getOwner(chunk), chunk);
                        getChunkdata().unclaimEffect(player, chunk);
                        getChunkdata().unclaimSound(player);
                    } else {
                        getMessage().send(player, "&cCurrent chunk is already unclaimed");
                    }
                }
                if (args.length == 3) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    if (args[2].equalsIgnoreCase("all")) {
                        getChunkdata().removeAll(offlinePlayer);
                        getMessage().send(player, "&6You safely unclaimed all&f " + offlinePlayer.getName() + "&6's chunks");
                    }
                }
            }
        }
    }
}