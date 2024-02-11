package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.utils.DatabaseTypes;

public class Database {

    @JsonProperty
    private String type = "sqlite";

    @JsonProperty
    public String username = null;

    @JsonProperty
    public String password = null;

    @JsonProperty
    public String database = "AnturniaQuests";

    @JsonProperty
    public String ip = "127.0.0.1";

    @JsonProperty
    public int port = 3306;

    @JsonIgnore
    public DatabaseTypes getType() {
        return DatabaseTypes.valueOf(type.toUpperCase());
    }

}
