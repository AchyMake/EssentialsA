package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.achymake.essentialsa.data.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BannedCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public BannedCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "banned";
    }
    @Override
    public String getDescription() {
        return "check list of banned players";
    }
    @Override
    public String getSyntax() {
        return "/chunk banned";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.banned")) {
            if (args.length == 1) {
                if (getDatabase().getConfig(player).getStringList("chunks.banned").isEmpty()) {
                    getMessage().send(player, "&cYou do not have any banned members");
                } else {
                    getMessage().send(player, "&6Banned:");
                    for (String uuidListed : getDatabase().getConfig(player).getStringList("chunks.banned")) {
                        OfflinePlayer offlinePlayer = player.getServer().getOfflinePlayer(UUID.fromString(uuidListed));
                        getMessage().send(player, "- " + offlinePlayer.getName());
                    }
                }
            }
        }
    }
}