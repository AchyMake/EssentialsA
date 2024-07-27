package org.achymake.essentialsa.commands.chunks;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.chunks.sub.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChunksCommand implements CommandExecutor, TabCompleter {
    private final ArrayList<ChunksSubCommand> chunksSubCommands = new ArrayList<>();
    public ChunksCommand(EssentialsA plugin) {
        chunksSubCommands.add(new EditCommand(plugin));
        chunksSubCommands.add(new EffectCommand(plugin));
        chunksSubCommands.add(new HelpCommand(plugin));
        chunksSubCommands.add(new InfoCommand(plugin));
        chunksSubCommands.add(new SetOwnerCommand(plugin));
        chunksSubCommands.add(new UnClaimCommand(plugin));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            for (ChunksSubCommand commands : chunksSubCommands) {
                if (args[0].equals(commands.getName())) {
                    commands.perform(sender, args);
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("essentials.command.chunks.unclaim")) {
                commands.add("unclaim");
            }
            if (sender.hasPermission("essentials.command.chunks.edit")) {
                commands.add("edit");
            }
            if (sender.hasPermission("essentials.command.chunks.effect")) {
                commands.add("effect");
            }
            if (sender.hasPermission("essentials.command.chunks.help")) {
                commands.add("help");
            }
            if (sender.hasPermission("essentials.command.chunks.info")) {
                commands.add("info");
            }
            if (sender.hasPermission("essentials.command.chunks.setowner")) {
                commands.add("setowner");
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("effect")) {
                if (sender.hasPermission("essentials.command.chunks.effect")) {
                    commands.add("claim");
                    commands.add("unclaim");
                }
            }
            if (args[0].equalsIgnoreCase("info")) {
                if (sender.hasPermission("essentials.command.chunks.info")) {
                    for (OfflinePlayer offlinePlayers : sender.getServer().getOfflinePlayers()) {
                        commands.add(offlinePlayers.getName());
                    }
                }
            }
            if (args[0].equalsIgnoreCase("setowner")) {
                if (sender.hasPermission("essentials.command.chunks.setowner")) {
                    for (OfflinePlayer offlinePlayers : sender.getServer().getOfflinePlayers()) {
                        commands.add(offlinePlayers.getName());
                    }
                }
            }
            if (args[0].equalsIgnoreCase("unclaim")) {
                if (sender.hasPermission("essentials.command.chunks.unclaim")) {
                    for (OfflinePlayer offlinePlayers : sender.getServer().getOfflinePlayers()) {
                        commands.add(offlinePlayers.getName());
                    }
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("unclaim")) {
                if (sender.hasPermission("essentials.command.chunks.unclaim")) {
                    commands.add("all");
                }
            }
        }
        return commands;
    }
}