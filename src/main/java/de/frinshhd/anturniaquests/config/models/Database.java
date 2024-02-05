package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Database {

    @JsonProperty
    public String type = "sqlite";

    @JsonProperty
    public String username = null;

    @JsonProperty
    public String password = null;

}
