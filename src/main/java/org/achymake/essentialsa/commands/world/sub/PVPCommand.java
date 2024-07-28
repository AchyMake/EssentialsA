package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PVPCommand extends WorldSubCommand {
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
    public PVPCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "pvp";
    }
    public String getDescription() {
        return "PvP settings";
    }
    public String getSyntax() {
        return "/worlds pvp world true";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (getWorlds().worldExist(player.getWorld().getName())) {
                    getWorlds().setPVP(player.getWorld(), !getWorlds().isPVP(player.getWorld()));
                    if (getWorlds().isPVP(player.getWorld())) {
                        getMessage().send(player, player.getWorld().getName() + "&6 is now pvp mode");
                    } else {
                        getMessage().send(player, player.getWorld().getName() + "&6 is no longer pvp mode");
                    }
                }
            }
            if (args.length == 2) {
                if (getWorlds().worldExist(args[1])) {
                    getWorlds().setPVP(player.getWorld(), !getWorlds().isPVP(player.getWorld()));
                    if (getWorlds().isPVP(player.getWorld())) {
                        getMessage().send(player, args[1] + "&6 is now pvp mode");
                    } else {
                        getMessage().send(player, args[1] + "&6 is no longer pvp mode");
                    }
                } else {
                    getMessage().send(player, args[1] + "&c does not exist");
                }
            }
            if (args.length == 3) {
                if (getWorlds().worldExist(args[1])) {
                    getWorlds().setPVP(getServer().getWorld(args[1]), Boolean.valueOf(args[2]));
                    if (getWorlds().isPVP(getServer().getWorld(args[1]))) {
                        getMessage().send(player, args[1] + "&6 is now pvp mode");
                    } else {
                        getMessage().send(player, args[1] + "&6 is no longer pvp mode");
                    }
                } else {
                    getMessage().send(player, args[1] + "&c does not exist");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 2) {
                if (getWorlds().worldExist(args[1])) {
                    getWorlds().setPVP(getServer().getWorld(args[1]), !getWorlds().isPVP(getServer().getWorld(args[1])));
                    if (getWorlds().isPVP(getServer().getWorld(args[1]))) {
                        getMessage().send(consoleCommandSender, args[1] + " is now pvp mode");
                    } else {
                        getMessage().send(consoleCommandSender, args[1] + " is no longer pvp mode");
                    }
                } else {
                    getMessage().send(consoleCommandSender, args[1] + " does not exist");
                }
            }
            if (args.length == 3) {
                if (getWorlds().worldExist(args[1])) {
                    getWorlds().setPVP(getServer().getWorld(args[1]), Boolean.valueOf(args[2]));
                    if (getWorlds().isPVP(getServer().getWorld(args[1]))) {
                        getMessage().send(consoleCommandSender, args[1] + " is now pvp mode");
                    } else {
                        getMessage().send(consoleCommandSender, args[1] + " is no longer pvp mode");
                    }
                } else {
                    getMessage().send(consoleCommandSender, args[1] + " does not exist");
                }
            }
        }
    }
}