package de.frinshhd.anturniaquests.quests.models;

import com.google.gson.annotations.SerializedName;

public class Command {
    @SerializedName("name")
    private String name;

    @SerializedName("command")
    private String command;

    public String getName() {
        if (this.name == null) {
            return getCommand();
        }

        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}