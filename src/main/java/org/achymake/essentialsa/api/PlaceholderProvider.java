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
            }
        }
        return super.onPlaceholderRequest(player, params);
    }
}