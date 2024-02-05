package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Command {
    @JsonProperty
    private String name;

    @JsonProperty
    private String command;

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public String getCommand() {
        return this.command;
    }
}
