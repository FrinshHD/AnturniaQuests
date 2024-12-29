package de.frinshhd.anturniaquests.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.logger.Logger;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.config.models.Config;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import org.bukkit.ChatColor;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;

public class ConfigManager {

    Config config;

    public ConfigManager() {
        boolean loaded = load();

        if (!loaded) {
            return;
        }

        if (getConfig().debug) {
            Main.getInstance().getLogger().setLevel(Level.ALL);
            Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.FATAL);
            java.util.logging.Logger.getLogger("org.reflections").setLevel(Level.OFF);
        } else {
            Main.getInstance().getLogger().setLevel(Level.SEVERE);
            Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.FATAL);
            java.util.logging.Logger.getLogger("org.reflections").setLevel(Level.OFF);
        }
    }

    public void connectToDB() {
        switch (this.config.database.getType()) {
            case MYSQL:
                MysqlManager.connect("jdbc:mysql://" + this.config.database.ip + ":" + this.config.database.port + "/" + this.config.database.database, this.config.database.username, this.config.database.password);
                break;
            case SQLITE:
                MysqlManager.connect("jdbc:sqlite:plugins/AnturniaQuests/sqlite.db");
                break;
            case MONGODB:
                break;
            default:
        }
    }

    public boolean load() {
        Yaml yaml = Main.getYaml();
        Gson gson = Main.getGson();

        try (FileInputStream inputStream = new FileInputStream("plugins/AnturniaQuests/config.yml")) {
            LinkedHashMap<String, Object> yamlData = yaml.load(inputStream);
            String jsonString = gson.toJson(yamlData);
            this.config = gson.fromJson(jsonString, Config.class);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading config.yml. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return false;
        }

        return true;
    }

    public Config getConfig() {
        return this.config;
    }
}