package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameModeCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public GameModeCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("adventure")) {
                    if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                        getMessage().send(player, "&cYou are already in&f Adventure&c mode");
                    } else {
                        player.setGameMode(GameMode.ADVENTURE);
                        getMessage().send(player, "&6You changed gamemode to&f adventure");
                    }
                }
                if (args[0].equalsIgnoreCase("creative")) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        getMessage().send(player, "&cYou are already in&f Creative&c mode");
                    } else {
                        player.setGameMode(GameMode.CREATIVE);
                        getMessage().send(player, "&6You changed gamemode to&f creative");
                    }
                }
                if (args[0].equalsIgnoreCase("survival")) {
                    if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                        getMessage().send(player, "&cYou are already in&f Survival&c mode");
                    } else {
                        player.setGameMode(GameMode.SURVIVAL);
                        getMessage().send(player, "&6You changed gamemode to&f survival");
                    }
                }
                if (args[0].equalsIgnoreCase("spectator")) {
                    if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                        getMessage().send(player, "&cYou are already in&f Spectator&c mode");
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        getMessage().send(player, "&6You changed gamemode to&f spectator");
                    }
                }
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.gamemode.other")) {
                    Player target = getServer().getPlayerExact(args[1]);
                    if (target == sender) {
                        if (args[0].equalsIgnoreCase("adventure")) {
                            if (target.getGameMode().equals(GameMode.ADVENTURE)) {
                                getMessage().send(player, "&cYou are already in&f Adventure&c mode");
                            } else {
                                target.setGameMode(GameMode.ADVENTURE);
                                getMessage().send(player, "&6You changed gamemode to&f Adventure");
                            }
                        }
                        if (args[0].equalsIgnoreCase("creative")) {
                            if (target.getGameMode().equals(GameMode.CREATIVE)) {
                                getMessage().send(player, "&cYou are already in&f Creative&c mode");
                            } else {
                                target.setGameMode(GameMode.CREATIVE);
                                getMessage().send(player, "&6You changed gamemode to&f Creative");
                            }
                        }
                        if (args[0].equalsIgnoreCase("survival")) {
                            if (target.getGameMode().equals(GameMode.SURVIVAL)) {
                                getMessage().send(player, "&cYou are already in&f Survival&c mode");
                            } else {
                                target.setGameMode(GameMode.SURVIVAL);
                                getMessage().send(player, "&6You changed gamemode to&f Survival");
                            }
                        }
                        if (args[0].equalsIgnoreCase("spectator")) {
                            if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                                getMessage().send(player, "&cYou are already in&f Spectator&c mode");
                            } else {
                                target.setGameMode(GameMode.SPECTATOR);
                                getMessage().send(player, "&6You changed gamemode to&f Spectator");
                            }
                        }
                    } else {
                        if (target != null) {
                            if (!target.hasPermission("essentials.command.gamemode.exempt")) {
                                if (args[0].equalsIgnoreCase("adventure")) {
                                    if (target.getGameMode().equals(GameMode.ADVENTURE)) {
                                        getMessage().send(player, target.getName() + "&c is already in&f Adventure&c mode");
                                    } else {
                                        target.setGameMode(GameMode.ADVENTURE);
                                        getMessage().send(target, player.getName() + "&6 has changed your gamemode to&f Adventure");
                                        getMessage().send(player, "&6You changed&f " + target.getName() + "&6's mode to&f Adventure");
                                    }
                                }
                                if (args[0].equalsIgnoreCase("creative")) {
                                    if (target.getGameMode().equals(GameMode.CREATIVE)) {
                                        getMessage().send(player, target.getName() + "&c is already in&f Creative&c mode");
                                    } else {
                                        target.setGameMode(GameMode.CREATIVE);
                                        getMessage().send(target, player.getName() + "&6 has changed your gamemode to&f Creative");
                                        getMessage().send(player, "&6You changed&f " + target.getName() + "&6's mode to&f Creative");
                                    }
                                }
                                if (args[0].equalsIgnoreCase("survival")) {
                                    if (!target.getGameMode().equals(GameMode.SURVIVAL)) {
                                        getMessage().send(player, target.getName() + "&c is already in&f Survival&c mode");
                                    } else {
                                        target.setGameMode(GameMode.SURVIVAL);
                                        getMessage().send(target, player.getName() + "&6 has changed your gamemode to&f Survival");
                                        getMessage().send(player, "&6You changed&f " + target.getName() + "&6's mode to&f Survival");
                                    }
                                }
                                if (args[0].equalsIgnoreCase("spectator")) {
                                    if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                                        getMessage().send(player, target.getName() + "&c is already in&f Spectator&c mode");
                                    } else {
                                        target.setGameMode(GameMode.SPECTATOR);
                                        getMessage().send(target, player.getName() + "&6 has changed your gamemode to&f Spectator");
                                        getMessage().send(player, "&6You changed&f " + target.getName() + "&6's mode to&f Spectator");
                                    }
                                }
                            }
                        } else {
                            getMessage().send(player, args[1] + "&c is currently offline");
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                Player target = getServer().getPlayerExact(args[1]);
                if (target != null) {
                    if (args[0].equalsIgnoreCase("adventure")) {
                        if (target.getGameMode().equals(GameMode.ADVENTURE)) {
                            getMessage().send(consoleCommandSender, target.getName() + " is already in Adventure mode");
                        } else {
                            target.setGameMode(GameMode.ADVENTURE);
                            getMessage().send(target, "&6Your gamemode has changed to&f Adventure");
                            getMessage().send(consoleCommandSender, "You changed " + target.getName() + "'s mode to Adventure");
                        }
                    }
                    if (args[0].equalsIgnoreCase("creative")) {
                        if (target.getGameMode().equals(GameMode.CREATIVE)) {
                            getMessage().send(consoleCommandSender, target.getName() + " is already in Creative mode");
                        } else {
                            target.setGameMode(GameMode.CREATIVE);
                            getMessage().send(target, "&6Your gamemode has changed to&f Creative");
                            getMessage().send(consoleCommandSender, "You changed " + target.getName() + "'s mode to Creative");
                        }
                    }
                    if (args[0].equalsIgnoreCase("survival")) {
                        if (target.getGameMode().equals(GameMode.SURVIVAL)) {
                            getMessage().send(consoleCommandSender, target.getName() + " is already in Survival mode");
                        } else {
                            target.setGameMode(GameMode.SURVIVAL);
                            getMessage().send(target, "&6Your gamemode has changed to&f Survival");
                            getMessage().send(consoleCommandSender, "You changed " + target.getName() + "'s mode to Survival");
                        }
                    }
                    if (args[0].equalsIgnoreCase("spectator")) {
                        if (target.getGameMode().equals(GameMode.SPECTATOR)) {
                            getMessage().send(consoleCommandSender, target.getName() + " is already in Spectator mode");
                        } else {
                            target.setGameMode(GameMode.SPECTATOR);
                            getMessage().send(target, "&6Your gamemode has changed to&f Spectator");
                            getMessage().send(consoleCommandSender, "You changed " + target.getName() + "'s mode to Spectator");
                        }
                    }
                } else {
                    getMessage().send(consoleCommandSender, args[1] + " is currently offline");
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
                if (player.hasPermission("essentials.command.gamemode.adventure")) {
                    commands.add("adventure");
                }
                if (player.hasPermission("essentials.command.gamemode.creative")) {
                    commands.add("creative");
                }
                if (player.hasPermission("essentials.command.gamemode.survival")) {
                    commands.add("survival");
                }
                if (player.hasPermission("essentials.command.gamemode.spectator")) {
                    commands.add("spectator");
                }
            }
            if (args.length == 2) {
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