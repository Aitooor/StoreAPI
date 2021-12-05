package me.fckml.store.listener;

import me.fckml.store.StorePlugin;
import me.fckml.store.config.ConfigFile;
import me.fckml.store.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    public PlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, StorePlugin.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ConfigFile file = Storage.load(player.getName());

        if (file.getConfiguration().contains("commands")) {
            file.getConfiguration().getStringList("commands").forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName())));
            file.getConfiguration().set("commands", null);
            file.save();
            return;
        }
    }
}
