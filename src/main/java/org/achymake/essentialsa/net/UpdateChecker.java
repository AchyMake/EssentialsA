package org.achymake.essentialsa.net;

import org.achymake.essentialsa.EssentialsA;
import org.achymake.essentialsa.data.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public record UpdateChecker(EssentialsA plugin) {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    private BukkitScheduler getScheduler() {
        return plugin.getScheduler();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    public void getUpdate(Player player) {
        if (notifyUpdate()) {
            getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        if (!plugin.version().equals(latest)) {
                            getMessage().send(player, plugin.name() + "&6 has new update:");
                            getMessage().send(player, "-&a https://www.spigotmc.org/resources/118371/");
                        }
                    });
                }
            }, 5);
        }
    }
    public void getUpdate() {
        if (notifyUpdate()) {
            getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        if (plugin.version().equals(latest)) {
                            getMessage().sendLog(Level.INFO, "You are using the latest version");
                        } else {
                            getMessage().sendLog(Level.INFO, plugin.name() + " has new update:");
                            getMessage().sendLog(Level.INFO, "- https://www.spigotmc.org/resources/118371/");
                        }
                    });
                }
            });
        }
    }
    public void getLatest(Consumer<String> consumer) {
        try {
            InputStream inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 118371)).openStream();
            Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
                scanner.close();
            }
            inputStream.close();
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
    private boolean notifyUpdate() {
        return getConfig().getBoolean("notify-update");
    }
}