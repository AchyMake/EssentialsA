package org.achymake.essentialsa.data;

import org.achymake.essentialsa.EssentialsA;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;

public record Economy(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    public double get(OfflinePlayer offlinePlayer) {
        return getDatabase().getConfig(offlinePlayer).getDouble("account");
    }
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return get(offlinePlayer) >= amount;
    }
    public void add(OfflinePlayer offlinePlayer, double amount) {
        getDatabase().setDouble(offlinePlayer, "account", amount + get(offlinePlayer));
    }
    public void remove(OfflinePlayer offlinePlayer, double amount) {
        getDatabase().setDouble(offlinePlayer, "account", get(offlinePlayer) - amount);
    }
    public void set(OfflinePlayer offlinePlayer, double value) {
        getDatabase().setDouble(offlinePlayer, "account", value);
    }
    public void reset(OfflinePlayer offlinePlayer) {
        getDatabase().setDouble(offlinePlayer, "account", getConfig().getDouble("economy.starting-balance"));
    }
    public String format(double amount) {
        return new DecimalFormat(getConfig().getString("economy.format")).format(amount);
    }
    public String currency() {
        return getConfig().getString("economy.currency");
    }
}