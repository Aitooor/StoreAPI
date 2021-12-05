package me.fckml.store.redis;

import lombok.Data;
import lombok.Getter;
import me.fckml.store.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

@Data
public class StoreRedisDatabase {

    @Getter
    private static StoreRedisDatabase instance;

    private JedisPool redisPool;
    private JedisPubSub jedisPubSub;


    public StoreRedisDatabase() {
        instance = this;

        JedisPoolConfig config = new JedisPoolConfig();

        config.setTestWhileIdle(true);
        config.setNumTestsPerEvictionRun(-1);
        config.setMinEvictableIdleTimeMillis(60000L);
        config.setTimeBetweenEvictionRunsMillis(30000L);

        this.redisPool = new JedisPool(config, "127.0.0.1", 6379, 30000);

        this.setupPubSub();
    }

    private void setupPubSub() {
        this.jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase("Store")) {
                    try {
                        String[] args = message.split(";");

                        if (args[0].equalsIgnoreCase("RECEIVED")) {
                            boolean offline = Boolean.parseBoolean(args[1]);
                            String player = args[2];
                            String[] commands = args[3].split(",");

                            if (!offline) {
                                Player online = Bukkit.getPlayer(player);

                                if (online == null) {
                                    Storage.save(player, Arrays.asList(commands));
                                    return;
                                }

                                Arrays.asList(commands).forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player)));
                                return;
                            }

                            Arrays.asList(commands).forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ForkJoinPool.commonPool().execute(() -> {
            try {
                Jedis jedis = this.redisPool.getResource();

                jedis.subscribe(this.jedisPubSub, "Store");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}