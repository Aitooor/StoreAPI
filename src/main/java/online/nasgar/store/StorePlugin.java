package online.nasgar.store;

import lombok.Getter;
import online.nasgar.store.config.ConfigFile;
import online.nasgar.store.listener.PlayerListener;
import online.nasgar.store.mongo.MongoDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StorePlugin extends JavaPlugin {

    @Getter
    private static StorePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        new MongoDB();
        new PlayerListener();
        double seconds = new ConfigFile(this, "config").getDouble("runnable.seconds");
        // minutes * 20 = seconds * 20 (ticks)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MongoDB.mongoRunnable(), (long) seconds * 20, (long) seconds * 20);
    }
}
