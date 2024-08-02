package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Economy;
import org.achymake.essentialsa.data.Message;
import org.achymake.essentialsa.data.Worth;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SellCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Worth getWorth() {
        return plugin.getWorth();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public SellCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (isEmpty(player)) {
                    getMessage().send(player, "&cYou have to hold an item");
                } else {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (getWorth().isSellable(itemStack.getType())) {
                        double value = getWorth().getWorth(itemStack.getType());
                        getEconomy().depositPlayer(player, value);
                        getMessage().send(player, "&6You sold&f 1 " + itemStack.getType() + "&6 for&a " + getEconomy().currencyNamePlural() + getEconomy().format(getWorth().getWorth(itemStack.getType())));
                        itemStack.setAmount(itemStack.getAmount() - 1);
                    } else {
                        getMessage().send(player, itemStack.getType() + "&c is not able to sell");
                    }
                }
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    ItemStack[] itemStacks = player.getInventory().getStorageContents();
                    for (ItemStack itemStack : itemStacks) {
                        if (itemStack != null) {
                            if (getWorth().isSellable(itemStack.getType())) {
                                double value = getWorth().getWorth(itemStack.getType()) * itemStack.getAmount();
                                getEconomy().depositPlayer(player, value);
                                getMessage().send(player, "&6You sold&f " + itemStack.getAmount() + " " + itemStack.getType() + "&6 for&a " + getEconomy().currencyNamePlural() + getEconomy().format(getWorth().getWorth(itemStack.getType()) * itemStack.getAmount()));
                                itemStack.setAmount(0);
                            } else {
                                getMessage().send(player, itemStack.getType() + "&c is not able to sell");
                            }
                            return true;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("hand")) {
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (isEmpty(player)) {
                        getMessage().send(player, "&cYou have to hold an item");
                    } else {
                        if (getWorth().isSellable(itemStack.getType())) {
                            double value = getWorth().getWorth(itemStack.getType()) * itemStack.getAmount();
                            getEconomy().depositPlayer(player, value);
                            getMessage().send(player, "&6You sold&f " + itemStack.getAmount() + " " + itemStack.getType() + "&6 for&a " + getEconomy().currencyNamePlural() + getEconomy().format(getWorth().getWorth(itemStack.getType()) * itemStack.getAmount()));
                            itemStack.setAmount(0);
                        } else {
                            getMessage().send(player, itemStack.getType() + "&c is not able to sell");
                        }
                    }
                    return true;
                } else {
                    int amount = Integer.parseInt(args[0]);
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if (isEmpty(player)) {
                        getMessage().send(player, "&cYou have to hold an item");
                    } else {
                        if (getWorth().isSellable(itemStack.getType())) {
                            if (itemStack.getAmount() >= amount) {
                                double value = getWorth().getWorth(itemStack.getType()) * amount;
                                int newAmount = itemStack.getAmount() - amount;
                                getEconomy().depositPlayer(player, value);
                                getMessage().send(player, "&6You sold&f " + amount + " " + itemStack.getType() + "&6 for&a " + getEconomy().currencyNamePlural() + getEconomy().format(getWorth().getWorth(itemStack.getType()) * amount));
                                itemStack.setAmount(newAmount);
                            } else {
                                getMessage().send(player, "&cYou don't have enough&f " + itemStack.getType());
                            }
                        } else {
                            getMessage().send(player, itemStack.getType() + "&c is not able to sell");
                        }
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
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.add("8");
                commands.add("16");
                commands.add("32");
                commands.add("64");
                commands.add("all");
                commands.add("hand");
            }
        }
        return commands;
    }
    private boolean isEmpty(Player player) {
        return player.getInventory().getItemInMainHand().getType().equals(Material.AIR);
    }
}