package org.achymake.essentialsa.commands.chunk.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.chunk.ChunkSubCommand;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;

public class HelpCommand extends ChunkSubCommand {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    public HelpCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "help";
    }
    @Override
    public String getDescription() {
        return "checks chunk help";
    }
    @Override
    public String getSyntax() {
        return "/chunk help";
    }
    @Override
    public void perform(Player player, String[] args) {
        if (player.hasPermission("essentials.command.chunk.help")) {
            if (args.length == 1) {
                getMessage().send(player, "&6Chunk Help:");
                if (player.hasPermission("essentials.command.chunk.ban")) {
                    getMessage().send(player, "&f/chunk ban player&7 - bans target from chunk");
                }
                if (player.hasPermission("essentials.command.chunk.banned")) {
                    getMessage().send(player, "&f/chunk banned&7 - check list of banned");
                }
                if (player.hasPermission("essentials.command.chunk.claim")) {
                    getMessage().send(player, "&f/chunk claim&7 - claim current chunk");
                }
                getMessage().send(player, "&f/chunk help&7 - show this list");
                if (player.hasPermission("essentials.command.chunk.members")) {
                    getMessage().send(player, "&f/chunk members add player&7 - adds chunk member");
                    getMessage().send(player, "&f/chunk members remove player&7 - removes chunk member");
                }
                if (player.hasPermission("essentials.command.chunk.tnt")) {
                    getMessage().send(player, "&f/chunk tnt&7 - toggle tnt");
                }
                if (player.hasPermission("essentials.command.chunk.unban")) {
                    getMessage().send(player, "&f/chunk unban player&7 - unbans target");
                }
                if (player.hasPermission("essentials.command.chunk.unclaim")) {
                    getMessage().send(player, "&f/chunk unclaim&7 - unclaim current chunk");
                }
                if (player.hasPermission("essentials.command.chunk.view")) {
                    getMessage().send(player, "&f/chunk view&7 - view claimed chunks");
                }
                if (player.hasPermission("essentials.command.chunk.view.others")) {
                    getMessage().send(player, "&f/chunk view target&7 - view target claimed chunks");
                }
            }
        }
    }
}