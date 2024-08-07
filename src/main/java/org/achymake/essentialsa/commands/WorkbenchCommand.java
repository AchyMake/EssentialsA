package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public WorkbenchCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.openWorkbench(player.getLocation(), true);
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.workbench.other")) {
                    Player target = getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target == player) {
                            target.openWorkbench(target.getLocation(), true);
                            return true;
                        } else if (target.hasPermission("essentials.command.workbench.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to open workbench for&f " + target.getName());
                            return true;
                        } else {
                            target.openWorkbench(target.getLocation(), true);
                            getMessage().send(target, player.getName() + "&6 opened workbench for you");
                            getMessage().send(player, "&6You opened workbench for " + target.getName());
                            return true;
                        }
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender commandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.openWorkbench(target.getLocation(), true);
                    getMessage().send(commandSender, "You opened workbench for " + target.getName());
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
                if (player.hasPermission("essentials.command.workbench.other")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.workbench.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}