package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Jail;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JailCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Jail getJail() {
        return plugin.getJail();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public JailCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    if (getJail().locationExist()) {
                        if (target == player) {
                            if (getUserdata().isJailed(target)) {
                                getUserdata().getLocation(target, "jail").getChunk().load();
                                target.teleport(getUserdata().getLocation(target, "jail"));
                                getUserdata().setBoolean(target, "jailed", false);
                                getMessage().send(target, "&cYou got free by&f " + player.getName());
                                getMessage().send(player, "&6You freed&f " + target.getName());
                                getUserdata().setString(target, "locations.jail", null);
                            } else {
                                getJail().getLocation().getChunk().load();
                                getUserdata().setLocation(target, "jail");
                                target.teleport(getJail().getLocation());
                                getUserdata().setBoolean(target, "jailed", true);
                                getMessage().send(target, "&cYou got jailed by&f " + player.getName());
                                getMessage().send(player, "&6You jailed&f " + target.getName());
                            }
                        } else if (!target.hasPermission("essentials.command.jail.exempt")) {
                            if (getUserdata().isJailed(target)) {
                                getUserdata().getLocation(target, "jail").getChunk().load();
                                target.teleport(getUserdata().getLocation(target, "jail"));
                                getUserdata().setBoolean(target, "jailed", false);
                                getMessage().send(target, "&cYou got free by&f " + player.getName());
                                getMessage().send(player, "&6You freed&f " + target.getName());
                                getUserdata().setString(target, "locations.jail", null);
                            } else {
                                getJail().getLocation().getChunk().load();
                                getUserdata().setLocation(target, "jail");
                                target.teleport(getJail().getLocation());
                                getUserdata().setBoolean(target, "jailed", true);
                                getMessage().send(target, "&cYou got jailed by&f " + player.getName());
                                getMessage().send(player, "&6You jailed&f " + target.getName());
                            }
                        }
                    }
                } else {
                    getMessage().send(player, args[0] + "&c is currently offline");
                }
                return true;
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    if (getJail().locationExist()) {
                        if (getUserdata().isJailed(target)) {
                            getUserdata().getLocation(target, "jail").getChunk().load();
                            target.teleport(getUserdata().getLocation(target, "jail"));
                            getUserdata().setBoolean(target, "jailed", false);
                            getMessage().send(target, "&cYou got free by&f " + consoleCommandSender.getName());
                            getMessage().send(consoleCommandSender, "&6You freed&f " + target.getName());
                            getUserdata().setString(target, "locations.jail", null);
                        } else {
                            getJail().getLocation().getChunk().load();
                            getUserdata().setLocation(target, "jail");
                            target.teleport(getJail().getLocation());
                            getUserdata().setBoolean(target, "jailed", true);
                            getMessage().send(target, "&cYou got jailed by&f " + consoleCommandSender.getName());
                            getMessage().send(consoleCommandSender, "&6You jailed&f " + target.getName());
                        }
                    }
                } else {
                    getMessage().send(consoleCommandSender, args[0] + " is currently offline");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                for (Player players : getDatabase().getOnlinePlayers()) {
                    if (!players.hasPermission("essentials.command.jail.exempt")) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }
}