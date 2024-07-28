package org.achymake.essentialsa.listeners;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Chunkdata;
import org.achymake.essentialsa.data.Database;
import org.achymake.essentialsa.data.Entities;
import org.achymake.essentialsa.data.Message;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public record EntityDamageByEntity(EssentialsA plugin) implements Listener {
    private Entities getEntities() {
        return plugin.getEntities();
    }
    private Database getDatabase() {
        return plugin.getDatabase();
    }
    private Chunkdata getChunkdata() {
        return plugin.getChunkdata();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getChunk();
        if (getChunkdata().isClaimed(chunk)) {
            if (entity.getType().equals(EntityType.PLAYER))return;
            if (getEntities().isHostile(entity))return;
            switch (damager) {
                case Arrow arrow -> {
                    if (!(arrow.getShooter() instanceof Player player)) return;
                    if (getChunkdata().hasAccess(player, chunk)) return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
                case Player player -> {
                    if (getChunkdata().hasAccess(player, chunk)) return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
                case Snowball snowball -> {
                    if (!(snowball.getShooter() instanceof Player player)) return;
                    if (getChunkdata().hasAccess(player, chunk)) return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
                case SpectralArrow spectralArrow -> {
                    if (!(spectralArrow.getShooter() instanceof Player player)) return;
                    if (getChunkdata().hasAccess(player, chunk)) return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
                case ThrownPotion thrownPotion -> {
                    if (!(thrownPotion.getShooter() instanceof Player player)) return;
                    if (getChunkdata().hasAccess(player, chunk)) return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
                case Trident trident -> {
                    if (!(trident.getShooter() instanceof Player player)) return;
                    if (getChunkdata().hasAccess(player, chunk)) return;
                    event.setCancelled(true);
                    getMessage().sendActionBar(player, "&cChunk is owned by&f " + getChunkdata().getOwner(chunk).getName());
                }
                default -> {
                    if (getEntities().isNPC(entity)) {
                        event.setCancelled(true);
                    } else {
                        if (event.getDamager() instanceof Player)return;
                        if (!getEntities().disableDamage(event.getDamager(), event.getEntity()))return;
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            switch (damager) {
                case Arrow arrow -> {
                    if (!(arrow.getShooter() instanceof Player player)) return;
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                case Player player -> {
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                case Snowball snowball -> {
                    if (!(snowball.getShooter() instanceof Player player)) return;
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                case SpectralArrow spectralArrow -> {
                    if (!(spectralArrow.getShooter() instanceof Player player)) return;
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                case ThrownPotion thrownPotion -> {
                    if (!(thrownPotion.getShooter() instanceof Player player)) return;
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                case Trident trident -> {
                    if (!(trident.getShooter() instanceof Player player)) return;
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                case WindCharge windCharge -> {
                    if (!(windCharge.getShooter() instanceof Player player)) return;
                    if (isDisabled(player)) {
                        event.setCancelled(true);
                    } else {
                        if (entity instanceof Player target) {
                            if (player == target) return;
                            if (!getDatabase().isPVP(player)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but you're PVP is Disabled");
                            } else if (!getDatabase().isPVP(target)) {
                                event.setCancelled(true);
                                getMessage().sendActionBar(player, "&c&lHey!&7 Sorry, but&f " + target.getName() + "&7's PVP is Disabled");
                            }
                        }
                    }
                }
                default -> {
                    if (getEntities().isNPC(entity)) {
                        event.setCancelled(true);
                    } else {
                        if (event.getDamager() instanceof Player)return;
                        if (!getEntities().disableDamage(event.getDamager(), event.getEntity()))return;
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    private boolean isDisabled(Player player) {
        return getDatabase().isFrozen(player) || getDatabase().isJailed(player);
    }
}
