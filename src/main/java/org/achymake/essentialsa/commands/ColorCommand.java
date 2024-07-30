package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Message;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ColorCommand implements CommandExecutor, TabCompleter {
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
    public ColorCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.GOLD + "Minecraft colors:");
                player.sendMessage(ChatColor.BLACK + "&0" + ChatColor.DARK_BLUE + " &1" + ChatColor.DARK_GREEN + " &2" + ChatColor.DARK_AQUA + " &3");
                player.sendMessage(ChatColor.DARK_RED + "&4" + ChatColor.DARK_PURPLE + " &5" + ChatColor.GOLD + " &6" + ChatColor.GRAY + " &7");
                player.sendMessage(ChatColor.DARK_GRAY + "&8" + ChatColor.BLUE + " &9" + ChatColor.GREEN + " &a" + ChatColor.AQUA + " &b");
                player.sendMessage(ChatColor.RED + "&c" + ChatColor.LIGHT_PURPLE + " &d" + ChatColor.YELLOW + " &e");
                player.sendMessage("");
                player.sendMessage("&k" + ChatColor.MAGIC + " magic" + ChatColor.RESET + " &l" + ChatColor.BOLD + " Bold");
                player.sendMessage("&m" + ChatColor.STRIKETHROUGH + " Strike" + ChatColor.RESET + " &n" + ChatColor.UNDERLINE + " Underline");
                player.sendMessage("&o" + ChatColor.ITALIC + " Italic" + ChatColor.RESET + " &r Reset");
                return true;
            }
            if (args.length == 1) {
                if (player.hasPermission("essentials.command.color.other")) {
                    Player target = player.getServer().getPlayerExact(args[0]);
                    if (target != null) {
                        if (target.hasPermission("essentials.command.color.exempt")) {
                            getMessage().send(player, "&cYou are not allowed to send color message for&f " + target.getName());
                        } else {
                            target.sendMessage(ChatColor.GOLD + "Minecraft colors:");
                            target.sendMessage(ChatColor.BLACK + "&0" + ChatColor.DARK_BLUE + " &1" + ChatColor.DARK_GREEN + " &2" + ChatColor.DARK_AQUA + " &3");
                            target.sendMessage(ChatColor.DARK_RED + "&4" + ChatColor.DARK_PURPLE + " &5" + ChatColor.GOLD + " &6" + ChatColor.GRAY + " &7");
                            target.sendMessage(ChatColor.DARK_GRAY + "&8" + ChatColor.BLUE + " &9" + ChatColor.GREEN + " &a" + ChatColor.AQUA + " &b");
                            target.sendMessage(ChatColor.RED + "&c" + ChatColor.LIGHT_PURPLE + " &d" + ChatColor.YELLOW + " &e");
                            target.sendMessage("");
                            target.sendMessage("&k" + ChatColor.MAGIC + " magic" + ChatColor.RESET + " &l" + ChatColor.BOLD + " Bold");
                            target.sendMessage("&m" + ChatColor.STRIKETHROUGH + " Strike" + ChatColor.RESET + " &n" + ChatColor.UNDERLINE + " Underline");
                            target.sendMessage("&o" + ChatColor.ITALIC + " Italic" + ChatColor.RESET + " &r Reset");
                        }
                        return true;
                    }
                }
            }
        }
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {
                Player target = getServer().getPlayerExact(args[0]);
                if (target != null) {
                    target.sendMessage(ChatColor.GOLD + "Minecraft colors:");
                    target.sendMessage(ChatColor.BLACK + "&0" + ChatColor.DARK_BLUE + " &1" + ChatColor.DARK_GREEN + " &2" + ChatColor.DARK_AQUA + " &3");
                    target.sendMessage(ChatColor.DARK_RED + "&4" + ChatColor.DARK_PURPLE + " &5" + ChatColor.GOLD + " &6" + ChatColor.GRAY + " &7");
                    target.sendMessage(ChatColor.DARK_GRAY + "&8" + ChatColor.BLUE + " &9" + ChatColor.GREEN + " &a" + ChatColor.AQUA + " &b");
                    target.sendMessage(ChatColor.RED + "&c" + ChatColor.LIGHT_PURPLE + " &d" + ChatColor.YELLOW + " &e");
                    target.sendMessage("");
                    target.sendMessage("&k" + ChatColor.MAGIC + " magic" + ChatColor.RESET + " &l" + ChatColor.BOLD + " Bold");
                    target.sendMessage("&m" + ChatColor.STRIKETHROUGH + " Strike" + ChatColor.RESET + " &n" + ChatColor.UNDERLINE + " Underline");
                    target.sendMessage("&o" + ChatColor.ITALIC + " Italic" + ChatColor.RESET + " &r Reset");
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
                if (player.hasPermission("essentials.command.color.others")) {
                    for (Player players : getDatabase().getOnlinePlayers()) {
                        if (!players.hasPermission("essentials.command.color.exempt")) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
        }
        return commands;
    }
}