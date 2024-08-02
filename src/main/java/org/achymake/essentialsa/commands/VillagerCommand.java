package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Villagers;
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
    private Villagers getVillagers() {
        return plugin.getVillagers();
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
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        getMessage().send(player, "&6You removed&f " + villager.getName());
                        getVillagers().getFile(villager).delete();
                        villager.remove();
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("type")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        villager.setVillagerType(Villager.Type.valueOf(args[1]));
                        getVillagers().setString(villager, "type", villager.getVillagerType().name());
                        getMessage().send(player, "&6You set&f " + villager.getName() + "&6 type to&f " + villager.getVillagerType().name());
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("silent")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        boolean value = Boolean.valueOf(args[1]);
                        if (value) {
                            villager.setSilent(true);
                            getVillagers().setBoolean(villager, "silent", true);
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 silent to&f true");
                        } else {
                            villager.setSilent(false);
                            getVillagers().setBoolean(villager, "silent", false);
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 silent to&f false");
                        }
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("profession")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        villager.setProfession(Villager.Profession.valueOf(args[1].toUpperCase()));
                        getVillagers().setString(villager, "profession", args[1].toUpperCase());
                        getMessage().send(player, "&6You set&f "+ villager.getName() + "&6 profession to&f " + villager.getProfession().name());
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("adult")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        boolean value = Boolean.parseBoolean(args[1]);
                        if (value) {
                            villager.setAdult();
                            getVillagers().setBoolean(villager, "adult", true);
                            getMessage().send(player, "&6You set&f " + villager.getName() + "&6 to&f adult");
                        } else {
                            villager.setBaby();
                            getVillagers().setBoolean(villager, "adult", false);
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
                    getVillagers().createVillager(player, getMessage().getStringBuilder(args, 1));
                    return true;
                }
                if (args[0].equalsIgnoreCase("rename")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        String rename = getMessage().addColor(getMessage().getStringBuilder(args, 1));
                        getMessage().send(player, "&6You renamed&f "+ villager.getName() + "&6 to&f " + rename);
                        getVillagers().setString(villager, "name", rename);
                        villager.setCustomName(rename);
                    } else {
                        getMessage().send(player, "&cYou have to look at a villager");
                    }
                    return true;
                }
            }
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("command")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        String commandType = args[1];
                        String commandString = getMessage().getStringBuilder(args, 2);
                        getVillagers().setCommandType(villager, commandType);
                        getVillagers().setCommand(villager, commandString);
                        getMessage().send(player, "&6You added&f " + commandString + "&6 with&f "+ commandType + "&6 command to&f " + villager.getName());
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
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        commands.add(String.valueOf(villager.isAdult()));
                    }
                }
                if (args[0].equalsIgnoreCase("silent")) {
                    Villager villager = getVillagers().getSelected(player);
                    if (villager != null) {
                        commands.add(String.valueOf(villager.isSilent()));
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