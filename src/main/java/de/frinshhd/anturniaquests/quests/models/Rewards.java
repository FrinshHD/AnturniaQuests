package de.frinshhd.anturniaquests.quests.models;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class Rewards {

    @SerializedName("items")
    private List<ItemModel> items = new ArrayList<>();

    @SerializedName("money")
    private double money = 0.0;

    @SerializedName("commands")
    private List<Command> commands = new ArrayList<>();

    public void addItem(ItemModel itemModel) {
        items.add(itemModel);
    }

    public List<ItemModel> getItems() {
        return this.items;
    }

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public List<Command> getCommands() {
        return this.commands;
    }
}