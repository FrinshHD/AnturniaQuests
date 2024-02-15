package de.frinshhd.anturniaquests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.frinshhd.anturniaquests.config.models.Config;
import de.frinshhd.anturniaquests.mysql.MysqlManager;

import java.io.FileInputStream;
import java.io.IOException;

public class ConfigManager {

    Config config;

    public ConfigManager() {
        load();

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
