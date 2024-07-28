package org.achymake.essentialsa.commands.villager;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.commands.villager.sub.*;
import org.achymake.essentialsa.data.Entities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.List;

public class VillagerCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private final ArrayList<VillagerSubCommand> villagerSubCommands = new ArrayList<>();

    public VillagerCommand(EssentialsA plugin) {
        villagerSubCommands.add(new Adult(plugin));
        villagerSubCommands.add(new Create(plugin));
        villagerSubCommands.add(new Profession(plugin));
        villagerSubCommands.add(new Remove(plugin));
        villagerSubCommands.add(new Rename(plugin));
        villagerSubCommands.add(new Silent(plugin));
        villagerSubCommands.add(new SubCommand(plugin));
        villagerSubCommands.add(new Type(plugin));
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                for (VillagerSubCommand commands : villagerSubCommands) {
                    if (args[0].equalsIgnoreCase(commands.getName())) {
                        commands.perform((Player) sender, args);
                    }
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                for (VillagerSubCommand commandTypes : villagerSubCommands) {
                    commands.add(commandTypes.getName());
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("profession")) {
                    for (Villager.Profession profession : Villager.Profession.values()) {
                        commands.add(profession.name());
                    }
                }
                if (args[0].equalsIgnoreCase("type")) {
                    for (Villager.Type type : Villager.Type.values()) {
                        commands.add(type.name());
                    }
                }
                if (args[0].equalsIgnoreCase("command")) {
                    commands.add("console");
                    commands.add("player");
                }
                if (args[0].equalsIgnoreCase("adult")) {
                    if (getEntities().hasSelected(player)) {
                        commands.add(String.valueOf(getEntities().getSelected(player).isAdult()));
                    }
                }
                if (args[0].equalsIgnoreCase("silent")) {
                    if (getEntities().hasSelected((Player) sender)) {
                        commands.add(String.valueOf(getEntities().getSelected(player).isSilent()));
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("command")) {
                    if (args[1].equalsIgnoreCase("console")) {
                        commands.add("%player%");
                    }
                }
            }
        }
        return commands;
    }
}