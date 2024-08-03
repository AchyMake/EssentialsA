package org.achymake.essentialsa.commands;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkCommand implements CommandExecutor, TabCompleter {
    private final EssentialsA plugin;
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private Userdata getUserdata() {
        return plugin.getUserdata();
    }
    private Chunks getChunks() {
        return plugin.getChunks();
    }
    private Server getServer() {
        return plugin.getServer();
    }
    private Economy getEconomy() {
        return plugin.getEconomy();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public ChunkCommand(EssentialsA plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (getChunks().isEnable()) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("claim")) {
                        if (player.hasPermission("essentials.command.chunk.claim")) {
                            Chunk chunk = player.getChunk();
                            if (getChunks().isAllowedClaim(chunk) ) {
                                if (getChunks().isAllowedWorld(chunk.getWorld())) {
                                    if (getChunks().isClaimed(chunk)) {
                                        if (getChunks().isOwner(player ,chunk)) {
                                            getMessage().send(player, "&cYou already own it");
                                        } else {
                                            getMessage().send(player, "&cChunk is already owned by " + getChunks().getOwner(chunk).getName());
                                        }
                                    } else {
                                        if (getChunks().getClaimCount(player) >= getConfig().getInt("chunks.claim.max-claims")) {
                                            getMessage().send(player, "&cYou have reach you're limit of&f " + getChunks().getClaimCount(player) + "&c claimed chunks");
                                        } else {
                                            double cost = getConfig().getDouble("chunks.economy.cost");
                                            int claimed = getChunks().getClaimCount(player);
                                            if (claimed > 0) {
                                                int multiply = getConfig().getInt("chunks.economy.multiply");
                                                double calculator = multiply * cost / 100 * claimed;
                                                double result = cost + calculator;
                                                if (getEconomy().has(player, result)) {
                                                    getChunks().setup(player, chunk);
                                                    getChunks().claimEffect(player, chunk);
                                                    getChunks().claimSound(player);
                                                    getEconomy().withdrawPlayer(player, result);
                                                    getMessage().send(player, "&6You claimed a chunk for&a " + getEconomy().currencyNamePlural() + getEconomy().format(result));
                                                } else {
                                                    getMessage().send(player, "&cYou do not have&a " +  getEconomy().currencyNamePlural() + result + "&c to claim it");
                                                }
                                            } else {
                                                if (getEconomy().has(player, cost)) {
                                                    getChunks().setup(player, chunk);
                                                    getChunks().claimEffect(player, chunk);
                                                    getChunks().claimSound(player);
                                                    getEconomy().withdrawPlayer(player, cost);
                                                    getMessage().send(player, "&6You claimed a chunk for&a " + getEconomy().currencyNamePlural() + getEconomy().format(cost));
                                                } else {
                                                    getMessage().send(player, "&cYou do not have&a " +  getEconomy().currencyNamePlural() + getEconomy().format(cost) + "&c to claim it");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    getMessage().send(player, "&c&lHey!&7 Sorry but you are not allowed to claim here");
                                }
                            } else {
                                getMessage().send(player, "&c&lHey!&7 Sorry but you are not allowed to claim here");
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("banned")) {
                        if (player.hasPermission("essentials.command.chunk.banned")) {
                            if (getUserdata().getConfig(player).getStringList("chunks.banned").isEmpty()) {
                                getMessage().send(player, "&cYou do not have any banned members");
                            } else {
                                getMessage().send(player, "&6Banned:");
                                for (String uuidListed : getUserdata().getConfig(player).getStringList("chunks.banned")) {
                                    OfflinePlayer offlinePlayer = player.getServer().getOfflinePlayer(UUID.fromString(uuidListed));
                                    getMessage().send(player, "- " + offlinePlayer.getName());
                                }
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("unclaim")) {
                        if (player.hasPermission("essentials.command.chunk.unclaim")) {
                            Chunk chunk = player.getLocation().getChunk();
                            if (getChunks().isClaimed(chunk)) {
                                if (getChunks().isOwner(player, chunk)) {
                                    getMessage().send(player, "&6You unclaimed current chunk and got refunded&a " + getEconomy().currencyNamePlural() + getEconomy().format(getConfig().getDouble("chunks.economy.refund")));
                                    getChunks().remove(player, chunk);
                                    getChunks().unclaimEffect(player, chunk);
                                    getChunks().unclaimSound(player);
                                } else {
                                    getMessage().send(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
                                }
                            } else {
                                getMessage().send(player, "&cChunk is already unclaimed");
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("view")) {
                        if (player.hasPermission("essentials.command.chunk.view")) {
                            getChunks().chunkView(player, player);
                            getChunks().claimSound(player);
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("tnt")) {
                        if (player.hasPermission("essentials.command.chunk.tnt")) {
                            Chunk chunk = player.getLocation().getChunk();
                            if (getChunks().isClaimed(chunk)) {
                                if (getChunks().isOwner(player, chunk)) {
                                    if (getChunks().isTNTAllowed(chunk)) {
                                        getChunks().toggleTNT(chunk, false);
                                        getMessage().send(player, "&6Disabled&f TNT&6 for current chunk");
                                    } else {
                                        getChunks().toggleTNT(chunk, true);
                                        getMessage().send(player, "&6Enabled&f TNT&6 for current chunk");
                                    }
                                } else {
                                    if (player.hasPermission("essentialschunks.command.chunks.edit")) {
                                        if (getChunks().isTNTAllowed(chunk)) {
                                            getChunks().toggleTNT(chunk, false);
                                            getMessage().send(player, "&6Disabled&f TNT&6 for current chunk");
                                        } else {
                                            getChunks().toggleTNT(chunk, true);
                                            getMessage().send(player, "&6Enabled&f TNT&6 for current chunk");
                                        }
                                    } else {
                                        getMessage().send(player, "&cChunk is owned by&f " + getChunks().getOwner(chunk).getName());
                                    }
                                }
                            } else {
                                getMessage().send(player, "&cChunk is unclaimed");
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("members")) {
                        if (player.hasPermission("essentials.command.chunk.members")) {
                            if (getUserdata().getConfig(player).getStringList("chunks.members").isEmpty()) {
                                getMessage().send(player, "&cYou do not have any members");
                            } else {
                                getMessage().send(player, "&6Chunk Members:");
                                for (OfflinePlayer offlinePlayer : getChunks().getMembers(player)) {
                                    getMessage().send(player, "- " + offlinePlayer.getName());
                                }
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("help")) {
                        if (player.hasPermission("essentials.command.chunk.help")) {
                            getMessage().send(player, "&6Chunk Help:");
                            if (player.hasPermission("essentials.command.chunk.ban")) {
                                getMessage().send(player, "&f/chunk ban player&7 - bans target from chunk");
                            }
                            if (player.hasPermission("essentials.command.chunk.banned")) {
                                getMessage().send(player, "&f/chunk banned&7 - check list of banned");
                            }
                            if (player.hasPermission("essentials.command.chunk.claim")) {
                                getMessage().send(player, "&f/chunk claim&7 - claim current chunk");
                            }
                            getMessage().send(player, "&f/chunk help&7 - show this list");
                            if (player.hasPermission("essentials.command.chunk.members")) {
                                getMessage().send(player, "&f/chunk members add player&7 - adds chunk member");
                                getMessage().send(player, "&f/chunk members remove player&7 - removes chunk member");
                            }
                            if (player.hasPermission("essentials.command.chunk.tnt")) {
                                getMessage().send(player, "&f/chunk tnt&7 - toggle tnt");
                            }
                            if (player.hasPermission("essentials.command.chunk.unban")) {
                                getMessage().send(player, "&f/chunk unban player&7 - unbans target");
                            }
                            if (player.hasPermission("essentials.command.chunk.unclaim")) {
                                getMessage().send(player, "&f/chunk unclaim&7 - unclaim current chunk");
                            }
                            if (player.hasPermission("essentials.command.chunk.view")) {
                                getMessage().send(player, "&f/chunk view&7 - view claimed chunks");
                            }
                            if (player.hasPermission("essentials.command.chunk.view.others")) {
                                getMessage().send(player, "&f/chunk view target&7 - view target claimed chunks");
                            }
                            return true;
                        }
                    }
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("ban")) {
                        if (player.hasPermission("essentials.command.chunk.ban")) {
                            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                            if (getChunks().getBanned(player).contains(target)) {
                                getMessage().send(player, target.getName() + "&c is already banned");
                            } else {
                                if (getChunks().getMembers(player).contains(target)) {
                                    List<String> members = getChunks().getMembersUUIDString(player);
                                    List<String> banned = getChunks().getBannedUUIDString(player);
                                    members.remove(target.getUniqueId().toString());
                                    banned.add(target.getUniqueId().toString());
                                    getUserdata().setStringList(player, "chunks.members", members);
                                    getUserdata().setStringList(player, "chunks.banned", banned);
                                    getMessage().send(player, "&6You banned&f " + target.getName());
                                } else {
                                    List<String> banned = getChunks().getBannedUUIDString(player);
                                    banned.add(target.getUniqueId().toString());
                                    getUserdata().setStringList(player, "chunks.banned", banned);
                                    getMessage().send(player, "&6You banned&f " + target.getName());
                                }
                            }
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("unclaim")) {
                        if (player.hasPermission("essentials.command.chunk.unclaim")) {
                            if (args[1].equalsIgnoreCase("all")) {
                                getChunks().removeAll(player);
                                getMessage().send(player, "&6You unclaimed all chunks and got refunded each for&a " + getEconomy().currencyNamePlural() + getEconomy().format(getConfig().getDouble("chunks.economy.refund")));
                                return true;
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("view")) {
                        if (player.hasPermission("essentials.command.chunk.view")) {
                            if (player.hasPermission("essentials.command.chunk.view.others")) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                                if (getUserdata().exist(offlinePlayer)) {
                                    getChunks().chunkView(player, offlinePlayer);
                                    getChunks().claimSound(player);
                                    return true;
                                }
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("unban")) {
                        if (player.hasPermission("essentials.command.chunk.unban")) {
                            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                            if (getChunks().getBanned(player).contains(target.getUniqueId().toString())) {
                                List<String> banned = getChunks().getBannedUUIDString(player);
                                banned.remove(target.getUniqueId().toString());
                                getUserdata().setStringList(player, "chunks.banned", banned);
                                getMessage().send(player, "&6You banned&f " + target.getName() + "&6 from you're chunks");
                            } else {
                                getMessage().send(player, target.getName() + "&c is already banned");
                            }
                            return true;
                        }
                    }
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("members")) {
                        if (player.hasPermission("essentials.command.chunk.members")) {
                            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                            if (args[1].equalsIgnoreCase("add")) {
                                if (getChunks().getMembers(player).contains(target)) {
                                    getMessage().send(player, target.getName() + "&c is already a member");
                                } else {
                                    List<String> members = getUserdata().getConfig(player).getStringList("chunks.members");
                                    members.add(target.getUniqueId().toString());
                                    getUserdata().setStringList(player, "chunks.members", members);
                                    getMessage().send(player, target.getName() + "&6 is now a member");
                                }
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("remove")) {
                                if (getUserdata().getConfig(player).getStringList("chunks.members").contains(target.getUniqueId().toString())) {
                                    List<String> members = getUserdata().getConfig(player).getStringList("chunks.members");
                                    members.remove(target.getUniqueId().toString());
                                    getUserdata().setStringList(player, "chunks.members", members);
                                    getMessage().send(player, target.getName() + "&6 is now removed from members");
                                } else {
                                    getMessage().send(player, target.getName() + "&c is not a member");
                                }
                                return true;
                            }
                        }
                    }
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
                if (player.hasPermission("essentials.command.chunk.ban")) {
                    commands.add("ban");
                }
                if (player.hasPermission("essentials.command.chunk.banned")) {
                    commands.add("banned");
                }
                if (player.hasPermission("essentials.command.chunk.claim")) {
                    commands.add("claim");
                }
                if (player.hasPermission("essentials.command.chunk.help")) {
                    commands.add("help");
                }
                if (player.hasPermission("essentials.command.chunk.members")) {
                    commands.add("members");
                }
                if (player.hasPermission("essentials.command.chunk.tnt")) {
                    commands.add("tnt");
                }
                if (player.hasPermission("essentials.command.chunk.unban")) {
                    commands.add("unban");
                }
                if (player.hasPermission("essentials.command.chunk.unclaim")) {
                    commands.add("unclaim");
                }
                if (player.hasPermission("essentials.command.chunk.view")) {
                    commands.add("view");
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("unclaim")) {
                    if (player.hasPermission("essentials.command.chunk.unclaim")) {
                        commands.add("all");
                    }
                }
                if (player.hasPermission("essentials.command.chunk.view.others")) {
                    if (args[0].equalsIgnoreCase("view")) {
                        for (OfflinePlayer offlinePlayer : player.getServer().getOfflinePlayers()) {
                            if (getUserdata().exist(offlinePlayer)) {
                                commands.add(offlinePlayer.getName());
                            }
                        }
                    }
                }
                if (player.hasPermission("essentials.command.chunk.members")) {
                    if (args[0].equalsIgnoreCase("members")) {
                        commands.add("add");
                        commands.add("remove");
                    }
                }
                if (player.hasPermission("essentials.command.chunk.ban")) {
                    if (args[0].equalsIgnoreCase("ban")) {
                        for (OfflinePlayer players : getServer().getOfflinePlayers()) {
                            commands.add(players.getName());
                        }
                    }
                }
                if (player.hasPermission("essentials.command.chunk.unban")) {
                    if (args[0].equalsIgnoreCase("unban")) {
                        for (OfflinePlayer players : getServer().getOfflinePlayers()) {
                            commands.add(players.getName());
                        }
                    }
                }
            }
            if (args.length == 3) {
                if (player.hasPermission("essentials.command.chunk.members")) {
                    if (args[0].equalsIgnoreCase("members")) {
                        if (args[1].equalsIgnoreCase("add")) {
                            for (OfflinePlayer players : getServer().getOfflinePlayers()) {
                                commands.add(players.getName());
                            }
                        }
                        if (args[1].equalsIgnoreCase("remove")) {
                            for (OfflinePlayer offlinePlayers : getChunks().getMembers(player)) {
                                commands.add(offlinePlayers.getName());
                            }
                        }
                    }
                }
            }
        }
        return commands;
    }
}