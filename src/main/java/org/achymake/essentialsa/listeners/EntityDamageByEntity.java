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
    private Villagers getVillagers() {
        return plugin.getVillagers();
    }
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
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
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getChunk();
        switch (damager) {
            case Arrow arrow -> {
                if (arrow.getShooter() instanceof Player player) {
                    if (getDatabase().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getDatabase().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getDatabase().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk))return;
                            if (getEntities().isHostile(entity))return;
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    }
                }
            }
            case Player player -> {
                if (getDatabase().isDisabled(player)) {
                    event.setCancelled(true);
                } else {
                    if (entity instanceof Player target) {
                        if (player == target)return;
                        if (getWorlds().isPVP(target.getWorld())) {
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        } else {
                            getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                            event.setCancelled(true);
                        }
                    } else if (getChunkdata().isClaimed(chunk)) {
                        if (getChunkdata().hasAccess(player, chunk))return;
                        event.setCancelled(true);
                        getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                    }
                }
            }
            case Snowball snowball -> {
                if (snowball.getShooter() instanceof Player player) {
                    if (getDatabase().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getDatabase().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getDatabase().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk))return;
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    }
                }
            }
            case SpectralArrow spectralArrow -> {
                if (spectralArrow.getShooter() instanceof Player player) {
                    if (getDatabase().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getDatabase().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getDatabase().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk))return;
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    }
                }
            }
            case ThrownPotion thrownPotion -> {
                if (thrownPotion.getShooter() instanceof Player player) {
                    if (getDatabase().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getDatabase().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getDatabase().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk))return;
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    }
                }
            }
            case Trident trident -> {
                if (trident.getShooter() instanceof Player player) {
                    if (getDatabase().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getDatabase().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getDatabase().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk))return;
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                        }
                    }
                }
            }
            case WindCharge windCharge -> {
                if (windCharge.getShooter() instanceof Player player) {
                    if (getDatabase().isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target)return;
                            if (getWorlds().isPVP(target.getWorld())) {
                                if (!getDatabase().isPVP(player)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but your PVP is Disabled");
                                } else if (!getDatabase().isPVP(target)) {
                                    event.setCancelled(true);
                                    getMessage().sendActionBar(player, "&c&lHey!&7 Sorry but&f " + target.getName() + "&7's PVP is Disabled");
                                }
                            } else {
                                getMessage().send(player, "&cHey!&7 Sorry but pvp is disabled in this world");
                                event.setCancelled(true);
                            }
                        } else if (getChunkdata().isClaimed(chunk)) {
                            if (getChunkdata().hasAccess(player, chunk))return;
                            event.setCancelled(true);
                            getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
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
