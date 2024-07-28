package org.achymake.essentialsa.commands.world;

import org.bukkit.command.CommandSender;

public abstract class WorldSubCommand {
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract void perform(CommandSender sender, String[] args);
}
