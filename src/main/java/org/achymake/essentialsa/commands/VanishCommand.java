package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public VanishCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getUserdata().setVanish(player, !getUserdata().isVanished(player));
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.vanish.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target == player) {
                            getUserdata().setVanish(target, !getUserdata().isVanished(target));
                        } else {
                            if (target.hasPermission("essentials.command.vanish.exempt")) {
                                getMessage().send(player, "&cYou are not allowed to set vanish for&f " + target.getName());
                            } else {
                                getUserdata().setVanish(target, !getUserdata().isVanished(target));
                                if (getUserdata().isVanished(target)) {
                                    getMessage().send(target, player.getName() + "&6 made you vanish");
                                    getMessage().send(player, target.getName() + "&6 is now vanished");
                                } else {
                                    getMessage().send(target, player.getName() + "&6 made you no longer vanish");
                                    getMessage().send(player, target.getName() + "&6 is no longer vanished");
                                }
                            }
                        }
                    } else {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);
                        if (getUserdata().exist(offlinePlayer)) {
                            getUserdata().setVanish(offlinePlayer, !getUserdata().isVanished(offlinePlayer));
                            if (getUserdata().isVanished(offlinePlayer)) {
                                getMessage().send(player, offlinePlayer.getName() + "&6 is now vanished");
                            } else {
                                getMessage().send(player, offlinePlayer.getName() + "&6 is no longer vanished");
                            }
                        } else {
                            getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
                        }
                    }
                    return true;
                }
            }
            if (args.length == 2) {
                Player target = getServer().getPlayerExact(args[0]);
                boolean value = Boolean.valueOf(args[1]);
                if (value) {
                    if (target != null) {
                        if (!getUserdata().isVanished(target)) {
                            if (target == player) {
                                getUserdata().setVanish(target, true);
                            } else {
                                if (target.hasPermission("essentials.command.vanish.exempt")) {
                                    getMessage().send(player, "&cYou are not allowed to set vanish for&f " + target.getName());
                                } else {
                                    getUserdata().setVanish(target, true);
                                    getMessage().send(target, player.getName() + "&6 made you vanish");
                                    getMessage().send(player, target.getName() + "&6 is now vanished");
                                }
                            }
                            return true;
                        }
                    } else {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);
                        if (getUserdata().exist(offlinePlayer)) {
                            if (!getUserdata().isVanished(offlinePlayer)) {
                                getUserdata().setVanish(offlinePlayer, true);
                                if (getUserdata().isVanished(offlinePlayer)) {
                                    getMessage().send(player, offlinePlayer.getName() + "&6 is now vanished");
                                } else {
                                    getMessage().send(player, offlinePlayer.getName() + "&6 is no longer vanished");
                                }
                            }
                        } else {
                            getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
                        }
                        return true;
                    }
                } else {
                    if (target != null) {
                        if (getUserdata().isVanished(target)) {
                            if (target == player) {
                                getUserdata().setVanish(target, false);
                            } else {
                                if (target.hasPermission("essentials.command.vanish.exempt")) {
                                    getMessage().send(player, "&cYou are not allowed to set vanish for&f " + target.getName());
                                } else {
                                    getUserdata().setVanish(target, false);
                                    getMessage().send(target, player.getName() + "&6 made you no longer vanish");
                                    getMessage().send(player, target.getName() + "&6 is no longer vanished");
                                }
                            }
                            return true;
                        }
                    } else {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);
                        if (getUserdata().exist(offlinePlayer)) {
                            if (getUserdata().isVanished(offlinePlayer)) {
                                getUserdata().setVanish(offlinePlayer, false);
                                if (getUserdata().isVanished(offlinePlayer)) {
                                    getMessage().send(player, offlinePlayer.getName() + "&6 is now vanished");
                                } else {
                                    getMessage().send(player, offlinePlayer.getName() + "&6 is no longer vanished");
                                }
                            }
                        } else {
                            getMessage().send(player, offlinePlayer.getName() + "&c has never joined");
                        }
                        return true;
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    getUserdata().setVanish(target, !getUserdata().isVanished(target));
                    if (getUserdata().isVanished(target)) {
                        getMessage().send(consoleCommandSender, target.getName() + " is now vanished");
                    } else {
                        getMessage().send(consoleCommandSender, target.getName() + " is no longer vanished");
                    }
                } else {
                    OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);
                    if (getUserdata().exist(offlinePlayer)) {
                        getUserdata().setVanish(offlinePlayer, !getUserdata().isVanished(offlinePlayer));
                        if (getUserdata().isVanished(offlinePlayer)) {
                            getMessage().send(consoleCommandSender, offlinePlayer.getName() + " is now vanished");
                        } else {
                            getMessage().send(consoleCommandSender, offlinePlayer.getName() + " is no longer vanished");
                        }
                    } else {
                        getMessage().send(consoleCommandSender, offlinePlayer.getName() + " has never joined");
                    }
                }
                return true;
            }
            if (args.length == 2) {
                Player target = getServer().getPlayerExact(args[0]);
                boolean value = Boolean.valueOf(args[1]);
                if (value) {
                    if (target != null) {
                        if (!getUserdata().isVanished(target)) {
                            getUserdata().setVanish(target, true);
                            getMessage().send(consoleCommandSender, target.getName() + " is now vanished");
                            return true;
                        }
                    } else {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);
                        if (getUserdata().exist(offlinePlayer)) {
                            if (!getUserdata().isVanished(offlinePlayer)) {
                                getUserdata().setVanish(offlinePlayer, true);
                                if (getUserdata().isVanished(offlinePlayer)) {
                                    getMessage().send(consoleCommandSender, offlinePlayer.getName() + " is now vanished");
                                } else {
                                    getMessage().send(consoleCommandSender, offlinePlayer.getName() + " is no longer vanished");
                                }
                            }
                        } else {
                            getMessage().send(consoleCommandSender, offlinePlayer.getName() + " has never joined");
                        }
                        return true;
                    }
                } else {
                    if (target != null) {
                        if (getUserdata().isVanished(target)) {
                            getUserdata().setVanish(target, false);
                            getMessage().send(consoleCommandSender, target.getName() + " is no longer vanished");
                            return true;
                        }
                    } else {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);
                        if (getUserdata().exist(offlinePlayer)) {
                            if (getUserdata().isVanished(offlinePlayer)) {
                                getUserdata().setVanish(offlinePlayer, false);
                                if (getUserdata().isVanished(offlinePlayer)) {
                                    getMessage().send(consoleCommandSender, offlinePlayer.getName() + " is now vanished");
                                } else {
                                    getMessage().send(consoleCommandSender, offlinePlayer.getName() + " is no longer vanished");
                                }
                            }
                        } else {
                            getMessage().send(consoleCommandSender, offlinePlayer.getName() + " has never joined");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.vanish.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.vanish.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.vanish.other")) {
                    commands.add("true");
                    commands.add("false");
                }
            }
        }
        return commands;
    }
}