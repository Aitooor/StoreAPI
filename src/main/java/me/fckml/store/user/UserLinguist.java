package me.fckml.store.user;

import me.yushust.message.bukkit.SpigotLinguist;
import me.yushust.message.language.Linguist;
import org.bukkit.entity.Player;

public class UserLinguist implements Linguist<Player> {

    private final SpigotLinguist spigotLinguist;

    public UserLinguist(){
        this.spigotLinguist = new SpigotLinguist();
    }

    @Override
    public String getLanguage(Player player) {
        return spigotLinguist.getLanguage(player);
    }
}
