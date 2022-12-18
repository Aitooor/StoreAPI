package online.nasgar.store.gson;

import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unused")
public class Product {
    private Player player;
    private String name;
    private String description;
    private List<String> categories;
    private String image;
    private List<String> commands;
    private String serverName;

    public Product(Player player, String name, String description, List<String> categories, String image, List<String> commands, String serverName) {
        this.player = player;
        this.name = name;
        this.description = description;
        this.categories = categories;
        this.image = image;
        this.commands = commands;
        this.serverName = serverName;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getImage() {
        return image;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getServerName() {
        return serverName;
    }
}
