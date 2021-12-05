package me.fckml.store.storage;

import me.fckml.store.StorePlugin;
import me.fckml.store.config.ConfigFile;

import java.util.List;

public class Storage {

    public static void save(String name, List<String> commands) {
        ConfigFile configFile = new ConfigFile(StorePlugin.getInstance(), "players/" + name);

        configFile.getConfiguration().set("commands", commands);
        configFile.getConfiguration().set("name", name);

        configFile.save();
    }

    public static ConfigFile load(String name) {
        return new ConfigFile(StorePlugin.getInstance(), "players/" + name);
    }
}
