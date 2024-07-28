package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class RemoveCommand extends WorldSubCommand {
    private final EssentialsA plugin;
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public RemoveCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "remove";
    }
    public String getDescription() {
        return "save and remove world";
    }
    public String getSyntax() {
        return "/worlds remove name";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                getMessage().send(player, "&cUsage:&f /worlds remove worldName");
            }
            if (args.length == 2) {
                if (getWorlds().worldExist(args[1])) {
                    remove(args[1]);
                    getMessage().send(player, args[1] + "&6 is saved and removed");
                } else {
                    getMessage().send(player, args[1] + "&c does not exist");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 1) {
                getMessage().send(consoleCommandSender, "Usage: /worlds remove worldName");
            }
            if (args.length == 2) {
                if (getWorlds().worldExist(args[1])) {
                    remove(args[1]);
                    getMessage().send(consoleCommandSender, args[1] + " is saved and removed");
                } else {
                    getMessage().send(consoleCommandSender, args[1] + " does not exist");
                }
            }
        }
    }
    private void remove(String worldName) {
        File file = new File(plugin.getDataFolder(), "database/" + worldName + ".yml");
        if (file.exists()) {
            file.delete();
        }
        getServer().unloadWorld(worldName, true);
    }
}