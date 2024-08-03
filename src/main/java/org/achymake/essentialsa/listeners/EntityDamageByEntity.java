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
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
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
        double damage = event.getDamage();
        Chunk chunk = entity.getChunk();
        switch (damager) {
            case Arrow arrow -> {
                if (arrow.getShooter() instanceof Player player) {
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
                                        plugin.getEntities().setTarget(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk)) {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().setTarget(player.getPassenger(), entity);
                                }
                            } else {
                                if (getEntities().isHostile(entity))return;
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                            }
                        } else {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        }
                    }
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
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
                                    plugin.getEntities().setTarget(player.getPassenger(), target);
                                }
                            }
                        } else {
                            getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                            event.setCancelled(true);
                        }
                    } else if (getChunkdata().isClaimed(chunk)) {
                        if (getChunkdata().hasAccess(player, chunk)) {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        } else {
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    } else {
                        if (player.getPassenger() != null) {
                            if (!player.hasPermission("essentials.carry.attack"))return;
                            plugin.getEntities().setTarget(player.getPassenger(), entity);
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
                                        plugin.getEntities().setTarget(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk)) {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().setTarget(player.getPassenger(), entity);
                                }
                            } else {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                            }
                        } else {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        }
                    }
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
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
                                        plugin.getEntities().setTarget(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk)) {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().setTarget(player.getPassenger(), entity);
                                }
                            } else {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                            }
                        } else {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        }
                    }
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
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
                                        plugin.getEntities().setTarget(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk)) {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().setTarget(player.getPassenger(), entity);
                                }
                            } else {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                            }
                        } else {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        }
                    }
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
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
                                        plugin.getEntities().setTarget(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk)) {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().setTarget(player.getPassenger(), entity);
                                }
                            } else {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                            }
                        } else {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        }
                    }
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
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
                                        plugin.getEntities().setTarget(player.getPassenger(), target);
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk)) {
                                if (player.getPassenger() != null) {
                                    if (!player.hasPermission("essentials.carry.attack"))return;
                                    plugin.getEntities().setTarget(player.getPassenger(), entity);
                                }
                            } else {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                            }
                        } else {
                            if (player.getPassenger() != null) {
                                if (!player.hasPermission("essentials.carry.attack"))return;
                                plugin.getEntities().setTarget(player.getPassenger(), entity);
                            }
                        }
                    }
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
                        }
                    }
                }
            }
            default -> {
                if (getVillagers().isNPC(entity)) {
                    event.setCancelled(true);
                } else {
                    if (entity.isInsideVehicle()) {
                        if (entity.getVehicle() instanceof Player player) {
                            event.setCancelled(true);
                            player.damage(damage);
                        }
                    }
                    if (event.getDamager() instanceof Player)return;
                    if (!getEntities().disableDamage(damager, entity))return;
                    event.setCancelled(true);
                }
            }
        }
    }
}
