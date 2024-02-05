package de.frinshhd.anturniaquests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.frinshhd.anturniaquests.config.models.Config;
import de.frinshhd.anturniaquests.mysql.MysqlManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ConfigManager {

    Config config;

    public ConfigManager() {
        load();

        // connect to database
        if (config.database.type.equals("sqlite")) {
            try {
                MysqlManager.connect();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
