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
    private static MongoCollection<Document> mongoCol;
    private final String AUTH_URL = "mongodb" + srv() + "://%s:%s@%s:%s/" + params();
    private final String NO_AUTH_URL = "mongodb" + srv() + "://%s:%s/" + params();

    public MongoDB() {
        connect();
    }

    public static UpdateResult saveDocuments() {
        return mongoCol.updateMany(
                Filters.and(
                        Filters.eq("used", false),
                        Filters.eq("paid", true),
                        Filters.eq("server", SERVER_NAME)
                ),
                Updates.set("used", true)
        );
    }

    public static FindIterable<Document> getProducts() {
        return mongoCol.find(Filters.and(
                Filters.eq("used", false),
                Filters.eq("paid", true),
                Filters.eq("server", SERVER_NAME)
        ));
    }

    public void connect() {
        Utils.async(() -> {
            ConfigFile config = new ConfigFile(StorePlugin.getInstance(), "config");
            String user = config.getString("mongo.user"), password = config.getString("mongo.password"),
                    address = config.getString("mongo.address"), port = config.getString("mongo.port"),
                    database = config.getString("mongo.database"), collection = config.getString("mongo.collection");

            try {
                MongoClient mongoClient = MongoClients.create(user == null || password == null ?
                        String.format(NO_AUTH_URL, address, port) : String.format(AUTH_URL, user, password, address, port));
                mongoDb = mongoClient.getDatabase(database);
                mongoCol = mongoDb.getCollection(collection);

                Bukkit.getLogger().info("[StoreAPI] " + ChatColor.GREEN + "Connected to MongoDB!");
            } catch (Exception e) {
                Bukkit.getLogger().info("[StoreAPI] " + ChatColor.RED + "The plugin can't reach the MongoDB connection");
            }
        });
    }

    private String srv() {
        return new ConfigFile(StorePlugin.getInstance(), "config").getBoolean("mongo.srv") ? "" : "srv";
    }

    private String params() {
        String params = new ConfigFile(StorePlugin.getInstance(), "config").getString("mongo.params");
        return params == null || params.equals("") || params.equals(" ") ? "" : "?" + params;
    }

    public static class mongoRunnable implements Runnable {

        @Override
        public void run() {
            Utils.async(() -> {
                MongoDB.getProducts().forEach(doc -> {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(doc.getString("name"));
                    List<String> commands = doc.getList("commands", String.class);
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
