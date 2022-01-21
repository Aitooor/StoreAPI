package me.fckml.store;

import lombok.Getter;
import me.fckml.store.listener.PlayerListener;
import me.fckml.store.redis.StoreRedisDatabase;
import me.yushust.message.MessageHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class StorePlugin extends JavaPlugin {

    @Getter private static StorePlugin instance;
    @Getter private MessageHandler messageHandler;

    @Override
    public void onEnable() {
        instance = this;
        new StoreRedisDatabase();
        new PlayerListener();
    }
}
