package org.achymake.essentialsa.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ColorCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Minecraft colors:");
            sender.sendMessage(ChatColor.BLACK + "&0" + ChatColor.DARK_BLUE + " &1" + ChatColor.DARK_GREEN + " &2" + ChatColor.DARK_AQUA + " &3");
            sender.sendMessage(ChatColor.DARK_RED + "&4" + ChatColor.DARK_PURPLE + " &5" + ChatColor.GOLD + " &6" + ChatColor.GRAY + " &7");
            sender.sendMessage(ChatColor.DARK_GRAY + "&8" + ChatColor.BLUE + " &9" + ChatColor.GREEN + " &a" + ChatColor.AQUA + " &b");
            sender.sendMessage(ChatColor.RED + "&c" + ChatColor.LIGHT_PURPLE + " &d" + ChatColor.YELLOW + " &e");
            sender.sendMessage("");
            sender.sendMessage("&k" + ChatColor.MAGIC + " magic" + ChatColor.RESET + " &l" + ChatColor.BOLD + " Bold");
            sender.sendMessage("&m" + ChatColor.STRIKETHROUGH + " Strike" + ChatColor.RESET + " &n" + ChatColor.UNDERLINE + " Underline");
            sender.sendMessage("&o" + ChatColor.ITALIC + " Italic" + ChatColor.RESET + " &r Reset");
        }
        if (args.length == 1) {
            if (sender.hasPermission("essentials.command.color.other")) {
                Player target = sender.getServer().getPlayerExact(args[0]);
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
                if (player.hasPermission("essentials.command.color.others")) {
                    for (Player players : player.getServer().getOnlinePlayers()) {
                        commands.add(players.getName());
                    }
                }
            }
        }
        return commands;
    }
}