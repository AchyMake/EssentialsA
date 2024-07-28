package org.achymake.essentialsa.commands.villager;

import org.bukkit.entity.Player;

public abstract class VillagerSubCommand {
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract void perform(Player player, String[] args);
}