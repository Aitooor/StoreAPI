package online.nasgar.store.redis;

import lombok.Data;
import lombok.Getter;
import online.nasgar.store.StorePlugin;
import online.nasgar.store.storage.Storage;
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

        this.redisPool = new JedisPool(config, StorePlugin.getInstance().getConfig().getString("REDIS.ADDRESS"), Integer.parseInt(StorePlugin.getInstance().getConfig().getString("REDIS.PORT")), 30000);
        this.redisPool.getResource().auth(StorePlugin.getInstance().getConfig().getString("REDIS.PASSWORD"));

        this.setupPubSub();
    }

    private void setupPubSub() {
        this.jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase("Store")) {
                    try {
                        /* Message: RECEIVED;TEST_SERVER;true;Vicen621;/lorem[SEP]/ipsum[SEP]/dolor[SEP]/sit[SEP]/amet
                         *
                         * received: RECEIVED
                         * server: TEST_SERVER
                         * online: true
                         * player: Vicen621
                         * commands: /lorem[SEP]/ipsum[SEP]/dolor[SEP]/sit[SEP]/amet | {"/lorem", "/ipsum", "/dolor", "/sit", "/amet"}
                         */
                        String[] args = message.split(";");

                        if (args[0].equalsIgnoreCase("RECEIVED")) {
                            String server = args[1];

                            if (!server.equalsIgnoreCase(StorePlugin.getInstance().getConfig().getString("SERVER_NAME")))
                                return;

                            boolean online = Boolean.parseBoolean(args[2]); //true if online, false if offline
                            String player = args[3];
                            String[] commands = args[4].split("[SEP]");

                            if (!online) {
                                Player onlineP = Bukkit.getPlayer(player);

                                if (onlineP == null) {
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

                jedis.auth(StorePlugin.getInstance().getConfig().getString("REDIS.PASSWORD"));
                jedis.subscribe(this.jedisPubSub, "Store");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}