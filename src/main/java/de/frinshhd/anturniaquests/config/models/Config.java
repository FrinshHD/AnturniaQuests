package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Config {

    @JsonProperty
    public Database database = new Database();

    @JsonProperty
    private List<Command> commands = new ArrayList<>();

    @JsonProperty
    public boolean questMenuEnabled = true;

    @JsonProperty
    public boolean storylinesEnabled = true;

    @JsonProperty
    public boolean debug = false;

    public List<Command> getCommands() {
        return commands;
    }

    public Command getCommand(String name) {
        for (Command command : getCommands()) {
            if (command.getName().equals(name)) {
                return command;
            }
        }

        return null;
    }
}
