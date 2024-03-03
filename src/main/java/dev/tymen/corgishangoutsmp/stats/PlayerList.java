package dev.tymen.corgishangoutsmp.stats;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class PlayerList {
    private JavaPlugin plugin;
    private PluginLogger logger;
    private JDA jda;

    public PlayerList(JavaPlugin plugin, JDA jda, PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.jda = jda;
        schedulePlayerListUpdate();
    }

    public void getOnlinePlayer() {
        List<String> playerList = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerList.add(player.getName());
        }

        String statusMessage = "with nobody :(";

        if(playerList.size() == 1) {
            statusMessage = "with: " + playerList.size() + " person";
        }
        if (playerList.size() > 1) {
            statusMessage = "with: " + playerList.size() + " people";
        }

        jda.getPresence().setActivity(Activity.playing(statusMessage));
    }

    private void schedulePlayerListUpdate() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                getOnlinePlayer(); // This will call your method every minute
            }
        }, 0L, 1200L); // 1200L = 60 seconds * 20 ticks
    }
}
