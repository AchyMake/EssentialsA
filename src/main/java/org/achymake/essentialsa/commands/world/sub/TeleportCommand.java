package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends WorldSubCommand {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    public TeleportCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "teleport";
    }
    public String getDescription() {
        return "teleport to different world";
    }
    public String getSyntax() {
        return "/worlds teleport name";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                getMessage().send(player, "&cUsage:&f /world teleport worldName");
            }
            if (args.length == 2) {
                if (getWorlds().worldExist(args[1])) {
                    player.teleport(getWorlds().getSpawn(getServer().getWorld(args[1])));
                    getMessage().send(player, "&6Teleporting to&f " + args[1]);
                } else {
                    getMessage().send(player, args[1] + "&c does not exist");
                }
            }
        }
    }
}