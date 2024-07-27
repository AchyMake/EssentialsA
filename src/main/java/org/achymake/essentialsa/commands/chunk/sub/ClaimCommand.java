package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClaimCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public ClaimCommand(EssentialsA chunks) {
        plugin = chunks;
    }
    @Override
    public String getName() {
        return "claim";
    }
    @Override
    public String getDescription() {
        return "claims the chunk";
    }
    @Override
    public String getSyntax() {
        return "/chunk claim";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.claim")) {
            if (args.length == 1) {
                Chunk chunk = player.getChunk();
                if (getChunkdata().isAllowedClaim(chunk)) {
                    if (getChunkdata().isClaimed(chunk)) {
                        if (getChunkdata().isOwner(player ,chunk)) {
                            getMessage().send(player, "&cYou already own it");
                        } else {
                            getMessage().send(player, "&cChunk is already owned by " + getChunkdata().getOwner(chunk).getName());
                        }
                    } else {
                        if (getChunkdata().getClaimCount(player) >= getConfig().getInt("chunks.claim.max-claims")) {
                            getMessage().send(player, "&cYou have reach you're limit of&f " + getChunkdata().getClaimCount(player) + "&c claimed chunks");
                        } else {
                            double cost = getConfig().getDouble("chunks.economy.cost");
                            int claimed = getChunkdata().getClaimCount(player);
                            String currency = getEconomy().currency();
                            if (claimed > 0) {
                                int multiply = getConfig().getInt("chunks.economy.multiply");
                                double calculator = multiply * cost / 100 * claimed;
                                double result = cost + calculator;
                                if (getEconomy().has(player, result)) {
                                    getChunkdata().setup(player, chunk);
                                    getChunkdata().claimEffect(player, chunk);
                                    getChunkdata().claimSound(player);
                                    getEconomy().remove(player, result);
                                    getMessage().send(player, "&6You claimed a chunk for&a " + currency + getEconomy().format(result));
                                } else {
                                    getMessage().send(player, "&cYou do not have&a " +  currency + result + "&c to claim it");
                                }
                            } else {
                                if (getEconomy().has(player, cost)) {
                                    getChunkdata().setup(player, chunk);
                                    getChunkdata().claimEffect(player, chunk);
                                    getChunkdata().claimSound(player);
                                    getEconomy().remove(player, cost);
                                    getMessage().send(player, "&6You claimed a chunk for&a " + currency + getEconomy().format(cost));
                                } else {
                                    String value = currency + getEconomy().format(cost);
                                    getMessage().send(player, "&cYou do not have&a " +  value + "&c to claim it");
                                }
                            }
                        }
                    }
                } else {
                    getMessage().send(player, "&c&lHey!&7 Sorry but you are not allowed to claim here");
                }
            }
        }
    }
}