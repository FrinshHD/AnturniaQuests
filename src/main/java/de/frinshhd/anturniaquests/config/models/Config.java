package de.frinshhd.anturniaquests.config.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Config {


    @JsonProperty
    private String language = "en";
    @JsonProperty
    public Database database = new Database();
    @JsonProperty
    public Boolean questMenuEnabled = null;
    @JsonProperty
    public boolean storylinesEnabled = true;
    @JsonProperty
    public boolean debug = false;
    @JsonProperty
    private List<Command> commands = new ArrayList<>();
    @JsonProperty
    private QuestMenu questMenu = new QuestMenu();

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

    @JsonIgnore
    public QuestMenu getQuestMenu() {
        return questMenu;
    }

    public String getLanguage() {
        return language;
    }
}
