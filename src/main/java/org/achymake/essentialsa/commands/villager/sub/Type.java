package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class Type extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Type(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "type";
    }
    public String getDescription() {
        return "changes villager types";
    }
    public String getSyntax() {
        return "/villagers biomes type";
    }
    public void perform(Player player, String[] args) {
        if (args.length == 2) {
            if (getEntities().hasSelected(player)) {
                Villager villager = getEntities().getSelected(player);
                villager.setVillagerType(Villager.Type.valueOf(args[1]));
                getMessage().send(player, "&6You set&f " + villager.getName() + "&6 type to&f " + villager.getVillagerType().name());
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}