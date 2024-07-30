package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Warps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetWarpCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Warps getWarps() {
        return plugin.getWarps();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public SetWarpCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (getWarps().locationExist(args[0])) {
                    getWarps().setLocation(args[0], player.getLocation());
                    getMessage().send(player, args[0] + "&6 has been relocated");
                } else {
                    getWarps().setLocation(args[0], player.getLocation());
                    getMessage().send(player, args[0] + "&6 has been set");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.addAll(getWarps().getWarps());
            }
        }
        return commands;
    }
}