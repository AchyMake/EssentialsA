package org.achymake.essentialsa.commands.world.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.world.WorldSubCommand;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worlds;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends WorldSubCommand {
    private final EssentialsA plugin;
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public SetSpawnCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "setspawn";
    }
    public String getDescription() {
        return "set world spawn";
    }
    public String getSyntax() {
        return "/worlds setspawn";
    }
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                getWorlds().setSpawn(player.getWorld(), player.getLocation());
                getMessage().send(player, player.getWorld().getName() + "&6 changed spawn point");
            }
        }
    }
}