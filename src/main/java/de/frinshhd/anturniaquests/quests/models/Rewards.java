package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Rewards {

    @JsonProperty
    private List<Item> items = new ArrayList<>();
    @JsonProperty
    private double money = 0.0;
    @JsonProperty
    private List<Command> commands = new ArrayList<>();

    @JsonIgnore
    public List<Item> getItems() {
        return this.items;
    }

    @JsonIgnore
    public double getMoney() {
        return this.money;
    }

    @JsonIgnore
    public List<Command> getCommands() {
        return this.commands;
    }

}
