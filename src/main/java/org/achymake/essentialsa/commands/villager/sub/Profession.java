package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class Profession extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Profession(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "profession";
    }
    public String getDescription() {
        return "changes villager profession";
    }
    public String getSyntax() {
        return "/villagers profession type";
    }
    public void perform(Player player, String[] args) {
        if (args.length == 2) {
            if (getEntities().hasSelected(player)) {
                Villager villager = getEntities().getSelected(player);
                villager.setProfession(Villager.Profession.valueOf(args[1]));
                getMessage().send(player, "&6You set&f "+ villager.getName() + "&6 profession to&f " + villager.getProfession().name());
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}
