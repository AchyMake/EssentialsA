package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class Adult extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Adult(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "adult";
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
                    villager.setAdult();
                    getMessage().send(player, "&6You set&f " + villager.getName() + "&6 to&f adult");
                } else {
                    villager.setBaby();
                    getMessage().send(player, "&6You set&f " + villager.getName() + "&6 to&f baby");
                }
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}