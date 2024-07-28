package org.achymake.essentialsa.commands.villager.sub;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.VillagerSubCommand;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public class SubCommand extends VillagerSubCommand {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public SubCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    public String getName() {
        return "command";
    }
    public String getDescription() {
        return "changes villager command";
    }
    public String getSyntax() {
        return "/villagers command console/player command";
    }
    public void perform(Player player, String[] args) {
        if (args.length >= 3) {
            if (getEntities().hasSelected(player)) {
                Villager villager = getEntities().getSelected(player);
                villager.getPersistentDataContainer().set(NamespacedKey.minecraft("command-type"), PersistentDataType.STRING, args[1]);
                StringBuilder command = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    command.append(args[i]);
                    command.append(" ");
                }
                villager.getPersistentDataContainer().set(NamespacedKey.minecraft("command"), PersistentDataType.STRING, command.toString().strip());
                getMessage().send(player, "&6You added&f " + command.toString().strip() + "&6 with&f "+ args[1] + "&6 command");
            } else {
                getMessage().send(player, "&cYou have to look at a villager");
            }
        }
    }
}