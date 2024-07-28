package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AddCommand extends WorldSubCommand {
    private final EssentialsA plugin;
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public AddCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "add";
    }
    public String getDescription() {
        return "add existing world";
    }
    public String getSyntax() {
        return "/worlds add name normal";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2) {
                getMessage().send(player, "&cUsage:&f /worlds add worldName normal");
            }
            if (args.length == 3) {
                if (World.Environment.valueOf(args[2].toUpperCase()).equals(World.Environment.valueOf(args[2].toUpperCase()))) {
                    if (getWorlds().folderExist(args[1])) {
                        if (getWorlds().worldExist(args[1])) {
                            getMessage().send(player, args[1] + "&c already exist");
                        } else {
                            getMessage().send(player, args[1] + "&6 is about to be added");
                            getWorlds().create(args[1], World.Environment.valueOf(args[2].toUpperCase()));
                            getMessage().send(player, args[1] + "&6 is added with environment&f " + World.Environment.valueOf(args[2].toUpperCase()).name().toLowerCase());
                        }
                    } else {
                        getMessage().send(player, args[1] + "&c does not exist");
                    }
                } else {
                    getMessage().send(player, "&cYou have to add environment to add your world");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                getMessage().send(consoleCommandSender, "Usage: /worlds add worldName normal");
            }
            if (args.length == 3) {
                if (World.Environment.valueOf(args[2].toUpperCase()).equals(World.Environment.valueOf(args[2].toUpperCase()))) {
                    if (getWorlds().folderExist(args[1])) {
                        if (getWorlds().worldExist(args[1])) {
                            getMessage().send(consoleCommandSender, args[1] + " already exist");
                        } else {
                            getMessage().send(consoleCommandSender, args[1] + " is about to be added");
                            getWorlds().create(args[1], World.Environment.valueOf(args[2].toUpperCase()));
                            getMessage().send(consoleCommandSender, args[1] + " is added with environment " + World.Environment.valueOf(args[2].toUpperCase()).name().toLowerCase());
                        }
                    } else {
                        getMessage().send(consoleCommandSender, args[1] + " does not exist");
                    }
                } else {
                    getMessage().send(consoleCommandSender, "You have to add environment to add your world");
                }
            }
        }
    }
}