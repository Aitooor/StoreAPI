package me.fckml.store;

import lombok.Getter;
import me.fckml.store.listener.PlayerListener;
import me.fckml.store.redis.StoreRedisDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public class StorePlugin extends JavaPlugin {

    @Getter private static StorePlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        new StoreRedisDatabase();
        new PlayerListener();
    }
}
