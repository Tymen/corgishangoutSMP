package dev.tymen.corgishangoutsmp;

import dev.tymen.corgishangoutsmp.command.VoteSunCommandExecutor;
import dev.tymen.corgishangoutsmp.stats.PlayerList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Corgishangoutsmp extends JavaPlugin {
    private final PluginLogger logger = new PluginLogger(this);
    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        saveDefaultConfig();

        String BOT_TOKEN = getConfig().getString("discord_token");

        if (BOT_TOKEN != null) {
            try {
                JDA jda = JDABuilder.createLight(BOT_TOKEN, Collections.emptyList())
                        .setActivity(Activity.playing("Player stats"))
                        .addEventListeners(new MyListener())
                        .build();
                new PlayerList(this, jda, logger);
            } catch (Exception e) {
                logger.warning("Something went wrong with initializing discord bot!");
            }
        }

        logger.info("Plugin is enabled");

        this.getCommand("votesun").setExecutor(new VoteSunCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        logger.info("Plugin is disabled");
    }
}
