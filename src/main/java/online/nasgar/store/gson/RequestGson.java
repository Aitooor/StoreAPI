package online.nasgar.store.gson;

import com.google.gson.Gson;
import me.clip.placeholderapi.PlaceholderAPI;
import online.nasgar.store.StorePlugin;
import online.nasgar.store.config.ConfigFile;
import online.nasgar.store.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@SuppressWarnings({"unused"})
public class RequestGson {

    private static final String SERVER_NAME = new ConfigFile(StorePlugin.getInstance(), "config").getString("server_name");

    public static String products = "[]";
    public static BukkitRunnable redisRunnable = new BukkitRunnable() {

        @Override
        public void run() {
            Gson gson = new Gson();
            Product[] objects = gson.fromJson(products, Product[].class);
            for(Product object : objects) {
                List<String> commands = object.getCommands();
                if(!commands.isEmpty()) {
                    if(!object.getPlayer().isOnline())
                        Storage.save(object.getPlayer().getName(), commands);
                    else
                        Bukkit.getScheduler().runTask(StorePlugin.getInstance(), () -> commands.forEach(command -> {
                            command = PlaceholderAPI.setPlaceholders(object.getPlayer(), command);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", object.getPlayer().getName()));
                        }));
                }
            }
            Bukkit.getLogger().info("[StoreAPI] Modified document count: " + objects.length);
            this.cancel();
        }
    };

}
