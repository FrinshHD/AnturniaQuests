package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class Rewards {

    @JsonProperty
    private List<ItemModel> items = new ArrayList<>();
    @JsonProperty
    private double money = 0.0;
    @JsonProperty
    private List<Command> commands = new ArrayList<>();

    @JsonIgnore
    public void addItem(ItemModel itemModel) {
        items.add(itemModel);
    }

    @JsonIgnore
    public List<ItemModel> getItems() {
        return this.items;
    }

    @JsonIgnore
    public void setMoney(double money) {
        this.money = money;
    }

    @JsonIgnore
    public double getMoney() {
        return this.money;
    }

    @JsonIgnore
    public void addCommand(Command command) {
        commands.add(command);
    }

    @JsonIgnore
    public List<Command> getCommands() {
        return this.commands;
    }

}
