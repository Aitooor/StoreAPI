package online.nasgar.store;

import lombok.Getter;
import online.nasgar.store.config.ConfigFile;
import online.nasgar.store.listener.PlayerListener;
import online.nasgar.store.gson.RequestGson;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SuppressWarnings({"InstantiationOfUtilityClass", "ConstantConditions"})
public class StorePlugin extends JavaPlugin {

    @Getter
    private static StorePlugin instance;
    public static YamlConfiguration settings;
    private static JedisPool pool;

    @Override
    public void onEnable() {
        instance = this;
        ConfigFile s = new ConfigFile(this, "config");
        settings = s.getConfiguration();
        new RequestGson();
        new PlayerListener();

        pool = new JedisPool(settings.getString("redis.host"), settings.getInt("redis.port"));
        getLogger().info(settings.getString("redis.host") + ":" + settings.getInt("redis.port"));
        Jedis j = null;

        try{
            j = pool.getResource();
            j.auth(settings.getString("redis.password"));

            RequestGson.products = j.get(settings.getString("redis.key"));
            getLogger().info("Connected to Redis!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // minutes * 20 = seconds * 20 (ticks)
        RequestGson.redisRunnable.runTaskTimerAsynchronously(this, 0, 20 * 60 * 5);
        j.set(settings.getString("redis.key"), "[]");
    }

    @Override
    public void onDisable() {
        pool.close();
    }
}
