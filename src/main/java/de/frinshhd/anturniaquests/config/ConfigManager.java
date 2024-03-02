package de.frinshhd.anturniaquests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.j256.ormlite.logger.Logger;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.config.models.Config;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {

    Config config;

    public ConfigManager() {
        load();

        if (getConfig().debug) {
            Main.getInstance().getLogger().setLevel(Level.ALL);
            Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.DEBUG);
            java.util.logging.Logger.getLogger("org.reflections").setLevel(Level.OFF);
        } else {
            Main.getInstance().getLogger().setLevel(Level.SEVERE);
            Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.FATAL);
            java.util.logging.Logger.getLogger("org.reflections").setLevel(Level.OFF);
        }

        // connect to database
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

    public void load() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            this.config = mapper.readValue(new FileInputStream("plugins/AnturniaQuests/config.yml"), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Config getConfig() {
        return this.config;
    }
}
