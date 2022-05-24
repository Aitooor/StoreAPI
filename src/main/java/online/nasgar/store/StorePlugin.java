package online.nasgar.store;

import lombok.Getter;
import online.nasgar.store.listener.PlayerListener;
import online.nasgar.store.redis.StoreRedisDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public class StorePlugin extends JavaPlugin {

    @Getter
    private static StorePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        new StoreRedisDatabase();
        new PlayerListener();
    }
}
