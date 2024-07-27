package org.achymake.essentialsa.commands.chunks.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.chunks.ChunksSubCommand;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends ChunksSubCommand {
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
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (player.hasPermission("essentials.command.chunks.help")) {
                if (args.length == 1) {
                    getMessage().send(player, "&6Chunks Help:");
                    if (player.hasPermission("essentials.command.chunks.delete")) {
                        getMessage().send(player, "&f/chunks delete &7- safely unclaims chunk");
                    }
                    if (player.hasPermission("essentials.command.chunks.edit")) {
                        getMessage().send(player, "&f/chunks edit &7- toggle chunk edit");
                    }
                    if (player.hasPermission("essentials.command.chunks.effect")) {
                        getMessage().send(player, "&f/chunks effect claim &7- effects of claiming");
                        getMessage().send(player, "&f/chunks effect unclaim &7- effects of unclaiming");
                    }
                    getMessage().send(player, "&f/chunks help &7- show this list");
                    if (player.hasPermission("essentials.command.chunks.info")) {
                        getMessage().send(player, "&f/chunks info &7- checks info of chunk");
                    }
                    if (player.hasPermission("essentials.command.chunks.reload")) {
                        getMessage().send(player, "&f/chunks reload &7- reload chunks plugin");
                    }
                    if (player.hasPermission("essentials.command.chunks.setowner")) {
                        getMessage().send(player, "&f/chunks setowner target &7- sets chunk owner");
                    }
                }
            }
        } else if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                getMessage().send(consoleCommandSender, "Chunks Help:");
                getMessage().send(consoleCommandSender, "/chunks help - show this list");
                getMessage().send(consoleCommandSender, "/chunks reload - reload smpchunks plugin");
            }
        }
    }
}