package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class Remove extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Remove(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "remove";
    }
    public String getDescription() {
        return "removes villager npc";
    }
    public String getSyntax() {
        return "/villagers remove";
    }
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            if (getEntities().hasSelected(player)) {
                Villager villager = getEntities().getSelected(player);
                getMessage().send(player, "&6You removed&f " + villager.getName());
                villager.remove();
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}
