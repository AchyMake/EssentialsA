package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GMSPCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public GMSPCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                    getMessage().send(player, "&cYou are already in&f Spectator&c mode");
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    getMessage().send(player, "&6You changed gamemode to&f Spectator");
                }
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.gamemode.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target == player) {
                        if (!target.getGameMode().equals(GameMode.SPECTATOR)) {
                            target.setGameMode(GameMode.SPECTATOR);
                            getMessage().send(target, player.getName() + "&6 has changed your gamemode to&f Spectator");
                            getMessage().send(player, "&6You changed&f " + target.getName() + "&6 gamemode to&f Spectator");
                        }
                    } else {
                        if (target != null) {
                            if (target.hasPermission("essentials.command.gamemode.exempt")) {
                                getMessage().send(player, "&cYou are not allowed to change gamemode of&f " + target.getName());
                            } else {
                                if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                                    getMessage().send(player, target.getName() + "&c is already in&f Spectator&c mode");
                                } else {
                                    target.setGameMode(GameMode.SPECTATOR);
                                    getMessage().send(target, player.getName() + "&6 has changed your gamemode to&f Spectator");
                                    getMessage().send(player, "&6You changed&f " + target.getName() + "&6 gamemode to&f Spectator");
                                }
                            }
                        }
                    }
                } else {
                    getMessage().send(player, "&cError:&7 You do not have the permissions to execute the command");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    if (!target.getGameMode().equals(GameMode.SPECTATOR)) {
                        target.setGameMode(GameMode.SPECTATOR);
                        getMessage().send(target, "&6Your gamemode has changed to&f Spectator");
                        getMessage().send(consoleCommandSender, "You changed " + target.getName() + " gamemode to Spectator");
                    }
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.gamemode.other")) {
                    for (Player players : getServer().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.gamemode.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                    commands.add(player.getName());
                }
            }
        }
        return commands;
    }
}