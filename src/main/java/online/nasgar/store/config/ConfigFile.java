package online.nasgar.store.config;

import lombok.Getter;
import online.nasgar.store.StorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
public class ConfigFile extends AbstractConfigFile {

    private final File file;
    private final YamlConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String name, boolean overwrite) {
        super(plugin, name);
        this.file = new File(plugin.getDataFolder(), name + ".yml");

        try {
            plugin.saveResource(name + ".yml", overwrite);
        } catch (Exception ignored) {
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);

    }

    public ConfigFile(JavaPlugin plugin, String name) {
        this(plugin, name, false);
    }

    @Override
    public String getString(String path) {
        return this.configuration.contains(path) ?
                ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : null;
    }

    @Override
    public String getStringOrDefault(String path, String or) {
        String toReturn = this.getString(path);
        return (toReturn == null) ? or : toReturn;
    }

    @Override
    public int getInteger(String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    @Override
    public double getDouble(String path) {
        return this.configuration.contains(path) ? this.configuration.getDouble(path) : 0.0;
    }

    @Override
    public Object get(String path) {
        return this.configuration.contains(path) ? this.configuration.get(path) : null;
    }

    @Override
    public List<String> getStringList(String path) {
        return this.configuration.contains(path) ? this.configuration.getStringList(path) : null;
    }

    public void reload() {
        File file = new File(StorePlugin.getInstance().getDataFolder(), this.getName() + ".yml");

        try {
            this.getConfiguration().load(file);
            this.getConfiguration().save(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File folder = StorePlugin.getInstance().getDataFolder();
        try {
            this.getConfiguration().save(new File(folder, this.getName() + ".yml"));
        } catch (Exception ignored) {
        }
    }
}