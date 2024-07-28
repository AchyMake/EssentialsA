package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class Silent extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Silent(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "silent";
    }
    public String getDescription() {
        return "changes villager types";
    }
    public String getSyntax() {
        return "/villagers silent true/false";
    }
    public void perform(Player player, String[] args) {
        if (args.length == 2) {
            if (getEntities().hasSelected(player)) {
                Villager villager = getEntities().getSelected(player);
                boolean value = Boolean.valueOf(args[1]);
                if (value) {
                    villager.setSilent(true);
                    getMessage().send(player, "&6You set&f " + villager.getName() + "&6 silent to&f true");
                } else {
                    villager.setSilent(false);
                    getMessage().send(player, "&6You set&f " + villager.getName() + "&6 silent to&f false");
                }
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}