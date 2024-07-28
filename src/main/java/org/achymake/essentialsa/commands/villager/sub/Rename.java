package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class Rename extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Rename(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "rename";
    }
    public String getDescription() {
        return "changes villager name";
    }
    public String getSyntax() {
        return "/villagers rename name";
    }
    public void perform(Player player, String[] args) {
        if (args.length >= 2) {
            if (getEntities().hasSelected(player)) {
                Villager villager = getEntities().getSelected(player);
                StringBuilder name = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    name.append(args[i]);
                    name.append(" ");
                }
                getMessage().send(player, "&6You renamed&f "+ villager.getName() + "&6 to&f " + getMessage().addColor(name.toString().strip()));
                villager.setCustomName(getMessage().addColor(name.toString().strip()));
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}
