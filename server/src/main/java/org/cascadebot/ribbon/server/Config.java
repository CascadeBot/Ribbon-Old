package org.cascadebot.ribbon.server;

import lombok.AccessLevel;
import lombok.Getter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

@Getter
public class Config {

    @Getter(AccessLevel.NONE)
    public static final Config INS = new Config();

    @Getter(AccessLevel.NONE)
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static final String file = "config.yml";

    private YamlFile config;

    private String token;

    private OnlineStatus status;
    private String activity;
    private Activity.ActivityType activityType;

    private int shardCount;
    private int shardStart;
    private int shardEnd;

    private String rabbitHost;
    private int rabbitPort;
    private String rabbitUsername;
    private String rabbitPassword;

    private Config() {
        try {
            this.config = new YamlFile(new File(file));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Couldn't load config!", e);
            System.exit(1);
        }

        this.token = this.config.getString("discord.token");
        if (this.token == null || this.token.isBlank()) {
            LOGGER.error("Invalid Discord token! Exiting.");
            System.exit(1);
        }

        String status = this.config.getString("discord.status", "ONLINE");
        this.status = OnlineStatus.fromKey(status);
        if (this.status == OnlineStatus.UNKNOWN) {
            LOGGER.warn("Unknown status '{}' supplied, expected one of: {}. Status set to '{}'", status, Arrays.toString(OnlineStatus.values()), OnlineStatus.ONLINE);
            this.status = OnlineStatus.ONLINE;
        }

        if (this.config.contains("discord.activity")) {
            this.activity = this.config.getString("discord.activity");
            String type = this.config.getString("discord.activity_type", "DEFAULT");
            try {
                this.activityType = Activity.ActivityType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Unknown activity type '{}' supplied, expected one of: {}. Type set to '{}'", type, Arrays.toString(Activity.ActivityType.values()), Activity.ActivityType.DEFAULT);
                this.activityType = Activity.ActivityType.DEFAULT;
            }
        }

        this.shardCount = this.config.getInt("shards.count", 1);
        if (this.shardCount < 1) {
            LOGGER.error("Got a shard count less than 1! Please enter a positive number larger than 1!");
            System.exit(1);
        }
        this.shardStart = this.config.getInt("shards.start", 0);
        this.shardEnd = this.config.getInt("shards.end", 0);

        if (this.shardStart < 0 || this.shardEnd < 0) {
            LOGGER.error("Shard start and shard end must be a positive number!");
            System.exit(1);
        } else if (this.shardStart > this.shardEnd) {
            LOGGER.error("Shard start must be less than or equal to shard end!");
            System.exit(1);
        }

        this.rabbitHost = this.config.getString("rabbitmq.host", "localhost");
        this.rabbitPort = this.config.getInt("rabbitmq.port", 5672);
        this.rabbitUsername = this.config.getString("rabbitmq.username", "guest");
        this.rabbitPassword = this.config.getString("rabbitmq.password", "guest");
    }

}
