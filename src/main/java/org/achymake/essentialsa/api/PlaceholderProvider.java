package org.achymake.essentialsa.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.achymake.essentialsa.EssentialsA;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlaceholderProvider extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "essentials";
    }
    @Override
    public String getAuthor() {
        return "AchyMake";
    }
    @Override
    public String getVersion() {
        return EssentialsA.getInstance().getDescription().getVersion();
    }
    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public boolean register() {
        return super.register();
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        } else {
            switch (params) {
                case "name" -> {
                    return EssentialsA.getInstance().getDatabase().getConfig(player).getString("name");
                }
                case "display_name" -> {
                    return EssentialsA.getInstance().getDatabase().getConfig(player).getString("display-name");
                }
                case "vanished" -> {
                    return String.valueOf(EssentialsA.getInstance().getVanished().contains(player));
                }
                case "online_players" -> {
                    return String.valueOf(EssentialsA.getInstance().getDatabase().getOnlinePlayers().size());
                }
                case "account" -> {
                    return EssentialsA.getInstance().getEconomy().currency() + EssentialsA.getInstance().getEconomy().format(EssentialsA.getInstance().getEconomy().get(player));
                }
                case "pvp" -> {
                    return String.valueOf(EssentialsA.getInstance().getDatabase().isPVP(player));
                }
                case "chunk_owner" -> {
                    if (EssentialsA.getInstance().getChunkdata().isClaimed(player.getChunk())) {
                        return EssentialsA.getInstance().getChunkdata().getOwner(player.getChunk()).getName();
                    }
                    return "None";
                }
                case "chunk_access" -> {
                    if (EssentialsA.getInstance().getChunkdata().isClaimed(player.getChunk())) {
                        if (EssentialsA.getInstance().getChunkdata().hasAccess(player, player.getChunk())) {
                            return "True";
                        } else {
                            return "False";
                        }
                    } else {
                        return "True";
                    }
                }
                case "chunk_claimed" -> {
                    return String.valueOf(EssentialsA.getInstance().getChunkdata().getClaimCount(player));
                }
                case "chunk_max_claims" -> {
                    return String.valueOf(EssentialsA.getInstance().getConfig().getInt("chunks.claim.max-claims"));
                }
                case "chunk_claims_left" -> {
                    return String.valueOf(EssentialsA.getInstance().getConfig().getInt("chunks.claim.max-claims") - EssentialsA.getInstance().getChunkdata().getClaimCount(player));
                }
                case "world_name" -> {
                    return EssentialsA.getInstance().getWorlds().getConfig(player.getWorld()).getString("name");
                }
                case "world_display_name" -> {
                    return EssentialsA.getInstance().getWorlds().getConfig(player.getWorld()).getString("display-name");
                }
                case "world_pvp" -> {
                    return String.valueOf(EssentialsA.getInstance().getWorlds().getConfig(player.getWorld()).getBoolean("pvp"));
                }
            }
        }
        return super.onPlaceholderRequest(player, params);
    }
}