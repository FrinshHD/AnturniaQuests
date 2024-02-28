package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {

    @JsonProperty
    public Database database;

    @JsonProperty
    public boolean storylinesEnabled = true;
}
