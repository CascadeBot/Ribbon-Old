package org.cascadebot.ribbon.server;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.EnumSet;

public class RibbonServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(RibbonServer.class);

    @Getter
    private static RibbonServer instance;

    private ShardManager shardManager;

    public static void main(String[] args) {
        LOGGER.info("Starting Ribbon!");

        instance = new RibbonServer();
        instance.init();
    }

    private void init() {
        DefaultShardManagerBuilder shardManagerBuilder = new DefaultShardManagerBuilder();
        try {
            shardManagerBuilder.setToken(Config.INS.getToken())
                    .setStatusProvider(shard -> Config.INS.getStatus())
                    .setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE))
                    .setShardsTotal(Config.INS.getShardCount())
                    .setShards(Config.INS.getShardStart(), Config.INS.getShardEnd());

            if (Config.INS.getActivity() != null && Config.INS.getActivityType() != null) {
                shardManagerBuilder.setActivityProvider(shard -> Activity.of(Config.INS.getActivityType(), Config.INS.getActivity()));
            }

            this.shardManager = shardManagerBuilder.build();

        } catch (LoginException e) {
            LOGGER.error("Failed to login!", e);
            System.exit(1);
        }
    }

}
