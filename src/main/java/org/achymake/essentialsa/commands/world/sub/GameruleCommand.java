package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GameruleCommand extends WorldSubCommand {
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
    public GameruleCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "gamerule";
    }
    public String getDescription() {
        return "change gamerule";
    }
    public String getSyntax() {
        return "/worlds gamerule world gamerule value";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 4) {
                if (getWorlds().worldExist(args[1])) {
                    getServer().getWorld(args[1]).setGameRuleValue(args[2], args[3]);
                    getMessage().send(player, args[1] + "&6 changed&f " + args[2] + "&6 to&f " + args[3]);
                } else {
                    getMessage().send(player, args[1] + "&c does not exist");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 4) {
                if (getWorlds().worldExist(args[1])) {
                    getServer().getWorld(args[1]).setGameRuleValue(args[2], args[3]);
                    getMessage().send(consoleCommandSender, args[1] + " changed " + args[2] + " to " + args[3]);
                } else {
                    getMessage().send(consoleCommandSender, args[1] + " does not exist");
                }
            }
        }
    }
}