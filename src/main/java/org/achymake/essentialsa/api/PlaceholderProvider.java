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
            EssentialsA ess = EssentialsA.getInstance();
            switch (params) {
                case "name" -> {
                    return ess.getUserdata().getConfig(player).getString("name");
                }
                case "display_name" -> {
                    return ess.getUserdata().getConfig(player).getString("display-name");
                }
                case "vanished" -> {
                    return String.valueOf(ess.getVanished().contains(player));
                }
                case "online_players" -> {
                    return String.valueOf(ess.getDatabase().getOnlinePlayers().size());
                }
                case "account" -> {
                    return ess.getEconomy().currencyNamePlural() + ess.getEconomy().format(ess.getEconomy().getBalance(player));
                }
                case "pvp" -> {
                    return String.valueOf(ess.getUserdata().isPVP(player));
                }
                case "chunk_owner" -> {
                    if (ess.getChunkdata().isClaimed(player.getChunk())) {
                        return ess.getChunkdata().getOwner(player.getChunk()).getName();
                    }
                    return "None";
                }
                case "chunk_access" -> {
                    if (ess.getChunkdata().isClaimed(player.getChunk())) {
                        if (ess.getChunkdata().hasAccess(player, player.getChunk())) {
                            return "True";
                        } else {
                            return "False";
                        }
                    } else {
                        return "True";
                    }
                }
                case "chunk_claimed" -> {
                    return String.valueOf(ess.getChunkdata().getClaimCount(player));
                }
                case "chunk_max_claims" -> {
                    return String.valueOf(ess.getConfig().getInt("chunks.claim.max-claims"));
                }
                case "chunk_claims_left" -> {
                    return String.valueOf(ess.getConfig().getInt("chunks.claim.max-claims") - ess.getChunkdata().getClaimCount(player));
                }
                case "world_name" -> {
                    return ess.getWorlds().getConfig(player.getWorld()).getString("name");
                }
                case "world_display_name" -> {
                    return ess.getWorlds().getDisplayName(player.getWorld());
                }
                case "world_pvp" -> {
                    return String.valueOf(ess.getWorlds().getConfig(player.getWorld()).getBoolean("pvp"));
                }
            }
        }
        return super.onPlaceholderRequest(player, params);
    }
}