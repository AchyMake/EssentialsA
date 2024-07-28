package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends WorldSubCommand {
    private final EssentialsA plugin;
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public CreateCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "create";
    }
    public String getDescription() {
        return "create new world";
    }
    public String getSyntax() {
        return "/worlds create name";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2) {
                getMessage().send(player,"&cUsage:&f /worlds create worldName worldType");
            }
            if (args.length == 3) {
                if (World.Environment.valueOf(args[2].toUpperCase()).equals(World.Environment.valueOf(args[2].toUpperCase()))) {
                    if (getWorlds().folderExist(args[1])) {
                        getMessage().send(player, args[1] + "&c already exist");
                    } else {
                        getMessage().send(player, args[1] + "&6 is about to be created");
                        getWorlds().create(args[1], World.Environment.valueOf(args[2].toUpperCase()));
                        getMessage().send(player, args[1] + "&6 created with environment&f " + World.Environment.valueOf(args[2].toUpperCase()).toString().toLowerCase());
                    }
                } else {
                    getMessage().send(player, "&cYou have to add environment to create your world");
                }
            }
            if (args.length == 4) {
                getMessage().send(player, "arg1: " + args[1]);
                getMessage().send(player,  " arg2: " + args[2]);
                getMessage().send(player,  " arg3: " + args[3]);
                if (World.Environment.valueOf(args[2].toUpperCase()).equals(World.Environment.valueOf(args[2].toUpperCase()))) {
                    if (getWorlds().folderExist(args[1])) {
                        getMessage().send(player, args[1] + "&c already exist");
                    } else {
                        getMessage().send(player, args[1] + "&6 is about to be created");
                        getWorlds().create(args[1], World.Environment.valueOf(args[2].toUpperCase()), Long.valueOf(args[3]));
                        getMessage().send(player, args[1] + "&6 created with environment&f " + World.Environment.valueOf(args[2].toUpperCase()).toString().toLowerCase());
                    }
                } else {
                    getMessage().send(player, "&cYou have to add environment to create your world");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                getMessage().send(consoleCommandSender,"Usage: /worlds create worldName normal");
            }
            if (args.length == 3) {
                if (World.Environment.valueOf(args[2].toUpperCase()).equals(World.Environment.valueOf(args[2].toUpperCase()))) {
                    if (getWorlds().folderExist(args[1])) {
                        getMessage().send(consoleCommandSender, args[1] + " already exist");
                    } else {
                        getMessage().send(consoleCommandSender, args[1] + " is about to be created");
                        getWorlds().create(args[1], World.Environment.valueOf(args[2].toUpperCase()));
                        getMessage().send(consoleCommandSender, args[1] + " created with environment " + World.Environment.valueOf(args[2].toUpperCase()).toString().toLowerCase());
                    }
                } else {
                    getMessage().send(consoleCommandSender, "You have to add environment to create your world");
                }
            }
        }
    }
}