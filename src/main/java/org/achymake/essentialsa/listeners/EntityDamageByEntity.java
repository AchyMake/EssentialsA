package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public record EntityDamageByEntity(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Worlds getWorlds() {
        return plugin.getWorlds();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        Chunk chunk = entity.getChunk();
        switch (damager) {
            case Arrow arrow -> {
                if (arrow.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getUserdata().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getUserdata().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                } else {
                                    if (player.getPassenger() != null) {
                                        if (!player.hasPermission("essentials.carry.attack")) return;
                                        plugin.getEntities().attack(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            case Player player -> {
                if (getUserdata().isDisabled(player)) {
                    event.setCancelled(true);
                } else {
                    if (entity instanceof Player target) {
                        if (player == target)return;
                        if (getWorlds().isPVP(target.getWorld())) {
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                            } else {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().attack(player.getPassenger(), target);
                                }
                            }
                        } else {
                            getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                            event.setCancelled(true);
                        }
                    }
                }
            }
            case Snowball snowball -> {
                if (snowball.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getUserdata().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getUserdata().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                } else {
                                    if (player.getPassenger() != null) {
                                        if (!player.hasPermission("essentials.carry.attack"))return;
                                        plugin.getEntities().attack(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            case SpectralArrow spectralArrow -> {
                if (spectralArrow.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getUserdata().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getUserdata().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                } else {
                                    if (player.getPassenger() != null) {
                                        if (!player.hasPermission("essentials.carry.attack"))return;
                                        plugin.getEntities().attack(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            case ThrownPotion thrownPotion -> {
                if (thrownPotion.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getUserdata().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getUserdata().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                } else {
                                    if (player.getPassenger() != null) {
                                        if (!player.hasPermission("essentials.carry.attack"))return;
                                        plugin.getEntities().attack(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            case Trident trident -> {
                if (trident.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getUserdata().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getUserdata().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                } else {
                                    if (player.getPassenger() != null) {
                                        if (!player.hasPermission("essentials.carry.attack"))return;
                                        plugin.getEntities().attack(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            case WindCharge windCharge -> {
                if (windCharge.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getUserdata().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getUserdata().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                } else {
                                    if (player.getPassenger() != null) {
                                        if (!player.hasPermission("essentials.carry.attack"))return;
                                        plugin.getEntities().attack(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            default -> {
                if (getVillagers().isNPC(entity)) {
                    event.setCancelled(true);
                } else {
                    if (event.getDamager() instanceof Player)return;
                    if (!getEntities().disableDamage(damager, entity))return;
                    event.setCancelled(true);
                }
            }
        }
    }
}
