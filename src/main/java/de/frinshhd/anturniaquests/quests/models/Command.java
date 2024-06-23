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
        if (this.name == null) {
            return getCommand();
        }

        return this.name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getCommand() {
        return this.command;
    }

    @JsonProperty
    public void setCommand(String command) {
        this.command = command;
    }
}
