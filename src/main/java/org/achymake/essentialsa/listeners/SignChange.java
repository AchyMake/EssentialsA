package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.ChestShop;
import org.achymake.essentialsa.data.Message;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public record SignChange(EssentialsA plugin) implements Listener {
    private ChestShop getChestShop() {
        return plugin.getChestShop();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getBlock().getState();
        if (getChestShop().isShop(sign)) {
            event.setCancelled(true);
            getMessage().send(player, "&cSign is already a chest shop");
        } else {
            if (sign.getBlockData() instanceof WallSign wallSign) {
                if (wallSign.getFacing().equals(BlockFace.EAST)) {
                    if (sign.getLocation().add(-1,0,0).getBlock().getState() instanceof Chest chest) {
                        getChestShop().setupShop(player, sign, chest);
                    }
                } else if (wallSign.getFacing().equals(BlockFace.NORTH)) {
                    if (sign.getLocation().add(0,0,1).getBlock().getState() instanceof Chest chest) {
                        getChestShop().setupShop(player, sign, chest);
                    }
                } else if (wallSign.getFacing().equals(BlockFace.WEST)) {
                    if (sign.getLocation().add(1,0,0).getBlock().getState() instanceof Chest chest) {
                        getChestShop().setupShop(player, sign, chest);
                    }
                } else if (wallSign.getFacing().equals(BlockFace.SOUTH)) {
                    if (sign.getLocation().add(0,0,-1).getBlock().getState() instanceof Chest chest) {
                        getChestShop().setupShop(player, sign, chest);
                    }
                }
            }
        }
        if (player.hasPermission("essentials.event.sign.color")) {
            for (int i = 0; i < event.getLines().length; i++) {
                if (!event.getLine(i).contains("&"))return;
                event.setLine(i, getMessage().addColor(event.getLine(i)));
            }
        }
    }
}