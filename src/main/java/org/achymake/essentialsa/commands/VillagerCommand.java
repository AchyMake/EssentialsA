package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class VillagerCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }

    public VillagerCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        getMessage().send(player, "&6You removed&f " + villager.getName());
                        villager.remove();
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("type")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        villager.setVillagerType(Villager.Type.valueOf(args[1]));
                        getMessage().send(player, "&6You set&f " + villager.getName() + "&6 type to&f " + villager.getVillagerType().name());
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("silent")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        boolean value = Boolean.valueOf(args[1]);
                        if (value) {
                            villager.setSilent(true);
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 silent to&f true");
                        } else {
                            villager.setSilent(false);
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 silent to&f false");
                        }
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("profession")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        villager.setProfession(Villager.Profession.valueOf(args[1]));
                        getMessage().send(player, "&6You set&f "+ villager.getName() + "&6 profession to&f " + villager.getProfession().name());
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("adult")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        boolean value = Boolean.valueOf(args[1]);
                        if (value) {
                            villager.setAdult();
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 to&f adult");
                        } else {
                            villager.setBaby();
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 to&f baby");
                        }
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
            }
            if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    getEntities().createVillager(player, getMessage().getStringBuilder(args, 1));
                    return true;
                }
                if (args[0].equalsIgnoreCase("rename")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        StringBuilder name = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            name.append(args[i]);
                            name.append(" ");
                        }
                        getMessage().send(player, "&6You renamed&f "+ villager.getName() + "&6 to&f " + getMessage().addColor(getMessage().getStringBuilder(args, 2)));
                        villager.setCustomName(getMessage().addColor(getMessage().getStringBuilder(args, 2)));
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
            }
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("command")) {
                    if (getEntities().hasSelected(player)) {
                        Villager villager = getEntities().getSelected(player);
                        villager.getPersistentDataContainer().set(NamespacedKey.minecraft("command-type"), PersistentDataType.STRING, args[2]);
                        villager.getPersistentDataContainer().set(NamespacedKey.minecraft("command"), PersistentDataType.STRING, getMessage().getStringBuilder(args, 3));
                        getMessage().send(player, "&6You added&f " + command.toString().strip() + "&6 with&f "+ args[1] + "&6 command");
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                commands.add("adult");
                commands.add("create");
                commands.add("profession");
                commands.add("remove");
                commands.add("rename");
                commands.add("silent");
                commands.add("command");
                commands.add("type");
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