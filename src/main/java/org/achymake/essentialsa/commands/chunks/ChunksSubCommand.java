package org.achymake.essentialsa.commands.chunks;

import org.bukkit.command.CommandSender;

public abstract class ChunksSubCommand {
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getSyntax();
    public abstract void perform(CommandSender commandSender, String[] args);
}