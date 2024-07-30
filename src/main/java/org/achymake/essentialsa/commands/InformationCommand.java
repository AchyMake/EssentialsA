package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InformationCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public InformationCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (getDatabase().exist(offlinePlayer)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                    getMessage().send(player, "&m&7----------------------");
                    getMessage().send(player, "&6Name:&f " + offlinePlayer.getName());
                    getMessage().send(player, "&6UUID:&f " + offlinePlayer.getUniqueId());
                    getMessage().send(player, "&6Last online:&f " + simpleDateFormat.format(offlinePlayer.getLastPlayed()));
                    getMessage().send(player, "&6Homes:&f " + getDatabase().getHomes(offlinePlayer).size());
                    getMessage().send(player, "&6Muted:&f " + getDatabase().isMuted(offlinePlayer));
                    getMessage().send(player, "&6Frozen:&f " + getDatabase().isFrozen(offlinePlayer));
                    getMessage().send(player, "&6Jailed:&f " + getDatabase().isJailed(offlinePlayer));
                    getMessage().send(player, "&6PvP:&f " + getDatabase().isPVP(offlinePlayer));
                    getMessage().send(player, "&6Banned:&f " + getDatabase().isBanned(offlinePlayer));
                    getMessage().send(player, "&6Ban-reason:&f " + getDatabase().getBanReason(offlinePlayer));
                    getMessage().send(player, "&6Vanished:&f " + getDatabase().isVanished(offlinePlayer));
                    getMessage().send(player, "&m&7----------------------");
                } else {
                    getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (OfflinePlayer offlinePlayer : getDatabase().getOnlinePlayers()) {
                    commands.add(offlinePlayer.getName());
                }
            }
        }
        return commands;
    }
}