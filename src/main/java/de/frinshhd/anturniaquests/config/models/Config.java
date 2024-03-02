package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {

    @JsonProperty
    public Database database = new Database();

    @JsonProperty
    public boolean storylinesEnabled = true;

    @JsonProperty
    public boolean debug = false;
}
