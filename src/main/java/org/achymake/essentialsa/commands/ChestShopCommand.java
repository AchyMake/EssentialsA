package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.ChestShop;
import org.achymake.essentialsa.data.Message;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChestShopCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private ChestShop getCestShop() {
        return plugin.getChestShop();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public ChestShopCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getCestShop().toggleChestShopEditor(player);
                if (getCestShop().isChestShopEditor(player)) {
                    getMessage().send(player, "&6You are now a chest shop editor");
                } else {
                    getMessage().send(player, "&6You are no longer a chest shop editor");
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
        }
        return commands;
    }
}