package me.fckml.store;

import lombok.Getter;
import me.fckml.store.listener.PlayerListener;
import me.fckml.store.redis.StoreRedisDatabase;
import me.fckml.store.user.UserLinguist;
import me.fckml.store.user.UserMessageSender;
import me.fckml.store.utils.MessageUtil;
import me.yushust.message.MessageHandler;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSource;
import me.yushust.message.source.MessageSourceDecorator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class StorePlugin extends JavaPlugin {

    @Getter private static StorePlugin instance;
    @Getter private MessageHandler messageHandler;

    @Override
    public void onEnable() {
        instance = this;
        loadLanguages();
        new StoreRedisDatabase();
        new PlayerListener();
    }

    private void loadLanguages(){
        MessageSourceDecorator messageSourceDecorator =
                MessageSourceDecorator.decorate(
                        BukkitMessageAdapt.newYamlSource(
                                this,
                                new File(
                                        getDataFolder(),
                                        "languages"
                                )
                        )
                );
        MessageSource messageSource = messageSourceDecorator
                .addFallbackLanguage("en")
                .addFallbackLanguage("es")
                .get();
        this.loadFiles("languages/lang_en.yml", "languages/lang_es.yml");
        this.messageHandler = MessageHandler.of(
                messageSource,
                config -> {
                    config.addInterceptor(MessageUtil::translate);

                    config.specify(Player.class)
                            .setMessageSender(new UserMessageSender())
                            .setLinguist(new UserLinguist());
                }
        );
    }

    private void loadFiles(String... files){
        for(String name : files) {
            if (this.getResource(name) != null) {
                this.saveResource(name, false);
            }
        }
    }
}
