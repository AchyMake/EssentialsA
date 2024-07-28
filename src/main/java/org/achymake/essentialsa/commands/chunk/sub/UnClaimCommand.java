package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class UnClaimCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
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
        return "unclaims current chunk";
    }
    @Override
    public String getSyntax() {
        return "/chunk unclaim";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.unclaim")) {
            if (args.length == 1) {
                Chunk chunk = player.getLocation().getChunk();
                if (getChunkdata().isClaimed(chunk)) {
                    if (getChunkdata().isOwner(player, chunk)) {
                        getMessage().send(player, "&6You unclaimed current chunk and got refunded&a " + getEconomy().currency() + getEconomy().format(getConfig().getDouble("chunks.economy.refund")));
                        getChunkdata().remove(player, chunk);
                        getChunkdata().unclaimEffect(player, chunk);
                        getChunkdata().unclaimSound(player);
                    } else {
                        getMessage().send(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                    }
                } else {
                    getMessage().send(player, "&cChunk is already unclaimed");
                }
            }
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("all")) {
                    getChunkdata().removeAll(player);
                    getMessage().send(player, "&6You unclaimed all chunks and got refunded each for&a " + getEconomy().currency() + getEconomy().format(getConfig().getDouble("chunks.economy.refund")));
                }
            }
        }
    }
}