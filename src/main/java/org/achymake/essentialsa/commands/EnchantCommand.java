package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private Message getMessage() {
        return plugin.getMessage();
    }
    public EnchantCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.getInventory().getItemInMainHand().getType().isAir()) {
                    getMessage().send(player, "&cYou have to hold an item");
                } else {
                    ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
                    Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());
                    if (itemMeta.hasEnchant(enchantment)) {
                        player.getInventory().getItemInMainHand().removeEnchantment(enchantment);
                        getMessage().send(player, "&6You removed&f " + enchantment.getName());
                    } else {
                        player.getInventory().getItemInMainHand().addUnsafeEnchantment(enchantment, 1);
                        getMessage().send(player, "&6You added&f " + enchantment.getName() + "&6 with lvl&f 1");
                    }
                }
                return true;
            }
            if (args.length == 2) {
                if (player.getInventory().getItemInMainHand().getType().isAir()) {
                    getMessage().send(player, "&cYou have to hold an item");
                } else {
                    Enchantment enchantment = Enchantment.getByName(args[0].toUpperCase());
                    if (Integer.valueOf(args[1]) > 0) {
                        player.getInventory().getItemInMainHand().addUnsafeEnchantment(enchantment, Integer.valueOf(args[1]));
                        getMessage().send(player, "&6You added&f " + enchantment.getName() + "&6 with lvl&f " + args[1]);
                    } else {
                        player.getInventory().getItemInMainHand().removeEnchantment(enchantment);
                        getMessage().send(player, "&6You removed&f " + enchantment.getName());
                    }
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (Enchantment enchantment : Enchantment.values()) {
                    commands.add(enchantment.getName().toLowerCase());
                }
            }
            if (args.length == 2) {
                commands.add(String.valueOf(Enchantment.getByName(args[0].toUpperCase()).getMaxLevel()));
            }
        }
        return commands;
    }
}