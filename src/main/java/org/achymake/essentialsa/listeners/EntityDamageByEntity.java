package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public record EntityDamageByEntity(EssentialsA plugin) implements Listener {
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        switch (damager) {
            case Arrow arrow -> {
                if (arrow.getShooter() instanceof Player player) {
                    if (getUserdata().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
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
                        if (!getUserdata().isPVP(player)) {
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                        } else if (!getUserdata().isPVP(target)) {
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
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
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
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
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
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
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
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
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
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
                            if (!getUserdata().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getUserdata().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
            }
            default -> {
                if (event.getDamager() instanceof Player)return;
                if (!getEntities().disableDamage(damager, entity))return;
                event.setCancelled(true);
            }
        }
    }
}
