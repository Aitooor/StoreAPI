package online.nasgar.store.mongo;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import online.nasgar.store.StorePlugin;
import online.nasgar.store.config.ConfigFile;
import online.nasgar.store.storage.Storage;
import online.nasgar.store.utils.Utils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class MongoDB {

    private static final String SERVER_NAME = new ConfigFile(StorePlugin.getInstance(), "config").getString("server_name");
    private static MongoDatabase mongoDb;
    private static MongoCollection<Document> orders;

    public MongoDB() {
        connect();
    }

    public static UpdateResult saveDocuments() {
        return orders.updateMany(
                Filters.and(
                        Filters.eq("used", false),
                        Filters.eq("paid", true),
                        Filters.eq("product.serverName", SERVER_NAME)
                ),
                Updates.set("used", true)
        );
    }

    public static FindIterable<Document> getProducts() {
        return orders.find(Filters.and(
                Filters.eq("used", false),
                Filters.eq("paid", true),
                Filters.eq("product.serverName", SERVER_NAME)
        ));
    }

    public void connect() {
        Utils.async(() -> {
            ConfigFile config = new ConfigFile(StorePlugin.getInstance(), "config");
            String database = config.getString("mongo.database"), collection = config.getString("mongo.collection");

            try {
                MongoClient mongoClient = MongoClients.create(config.getString("mongo.link"));
                mongoDb = mongoClient.getDatabase(database);
                orders = mongoDb.getCollection(collection);

                Bukkit.getLogger().info("[StoreAPI] " + ChatColor.GREEN + "Connected to MongoDB!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[StoreAPI] " + ChatColor.RED + "The plugin can't reach the MongoDB connection");
            }
        });
    }

    public static class mongoRunnable implements Runnable {

        @Override
        public void run() {
            Utils.async(() -> {
                MongoDB.getProducts().forEach(doc -> {
                    Document product = doc.get("product", Document.class);
                    OfflinePlayer p = Bukkit.getOfflinePlayer(doc.getString("user"));
                    List<String> commands = product.getList("commands", String.class);
                    if (!commands.isEmpty()) {
                        if (!p.isOnline())
                            Storage.save(p.getName(), commands);
                        else
                            Utils.sync(() -> commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    command.replace("<player>", p.getName()))));
                    }

                });

                Bukkit.getLogger().info("[StoreAPI] Modified document count: " + MongoDB.saveDocuments().getModifiedCount());
            });
        }
    }
}
