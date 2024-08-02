package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Userdata;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NicknameCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public NicknameCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            String name = getUserdata().getConfig(player).getString("name");
            String displayName = getUserdata().getDisplayName(player);
            if (args.length == 0) {
                if (!displayName.equals(name)) {
                    getUserdata().setString(player, "display-name", name);
                    getMessage().send(player, "&6You reset your nickname");
                    return true;
                }
            }
            if (args.length == 1) {
                String rename = args[0];
                if (displayName.equals(rename)) {
                    getMessage().send(player, "&cYou already have&f " + rename + "&c as nickname");
                } else {
                    getUserdata().setString(player, "display-name", rename);
                    getMessage().send(player, "&6You changed your nickname to&f " + rename);
                }
                return true;
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.nickname.other")) {
                    String rename = args[0];
                    Player target = getServer().getPlayerExact(args[1]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.nickname.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to change nickname for&f " + target.getName());
                        } else {
                            if (!getUserdata().getConfig(target).getString("display-name").equals(rename)) {
                                getUserdata().setString(target, "display-name", rename);
                                getMessage().send(player, "&6You changed " + target.getName() + " nickname to&f " + args[0]);
                            } else {
                                getMessage().send(player, target.getName() + "&c already have&f " + args[0] + "&c as nickname");
                            }
                        }
                    } else {
                        getMessage().send(player, args[1] + "&c is currently offline");
                    }
                    return true;
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
                for (Player players : getServer().getOnlinePlayers()) {
                    commands.add(players.getName());
                }
            }
            if (args.length == 2) {
                if (player.hasPermission("essentials.command.nickname.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.nickname.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}