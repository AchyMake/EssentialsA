package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.entity.Player;

public class Create extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public Create(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "create";
    }
    public String getDescription() {
        return "creates villager npc";
    }
    public String getSyntax() {
        return "/villagers create name";
    }
    public void perform(Player player, String[] args) {
        if (args.length >= 2) {
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                name.append(args[i]);
                name.append(" ");
            }
            getEntities().createVillager(player, name.toString().strip());
        }
    }
}