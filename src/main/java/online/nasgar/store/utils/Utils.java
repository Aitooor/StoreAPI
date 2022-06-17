package online.nasgar.store.utils;

import online.nasgar.store.StorePlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils {
    public static void async(Runnable run) {
        new BukkitRunnable() {
            @Override
            public void run() {
                run.run();
            }
        }.runTaskAsynchronously(StorePlugin.getInstance());
    }

    public static void sync(Runnable run) {
        new BukkitRunnable() {
            @Override
            public void run() {
                run.run();
            }
        }.runTask(StorePlugin.getInstance());
    }
}
