package de.frinshhd.anturniaquests.config.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Config {

    @SerializedName("database")
    public Database database = new Database();

    @SerializedName("questMenuEnabled")
    public Boolean questMenuEnabled = null;

    @SerializedName("storylinesEnabled")
    public boolean storylinesEnabled = true;

    @SerializedName("debug")
    public boolean debug = false;

    @SerializedName("commands")
    private List<Command> commands = new ArrayList<>();

    @SerializedName("questMenu")
    @Expose(serialize = false, deserialize = false)
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

    public QuestMenu getQuestMenu() {
        return questMenu;
    }
}