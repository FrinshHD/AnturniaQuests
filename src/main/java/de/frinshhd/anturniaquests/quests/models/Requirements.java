package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class Requirements {

    @JsonProperty
    private ArrayList<Item> items = new ArrayList<>();

    @JsonProperty
    private ArrayList<KilledEntity> killedEntities = new ArrayList<>();


    public ArrayList<Item> getItems() {
        return this.items;
    }

    @JsonIgnore
    public ArrayList<KilledEntity> getKilledEntities() {
        return this.killedEntities;
    }

    public boolean checkItems(Player player) {
        for (Item item : getItems()) {
            if (item.getAmountInInventory(player) < item.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public boolean checkKilledEntities(Player player) {
        for (KilledEntity killedEntity : killedEntities) {
            if (Main.getQuestsManager().getKilledEntityAmount(player, killedEntity.getEntity()) < killedEntity.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public boolean check(Player player) {
        if (!checkItems(player)) {
            return false;
        }

        if (!checkKilledEntities(player)) {
            return false;
        }

        return true;
    }

    public void removeItems(Player player) {
        for (Item item : getItems()) {
            item.removeItemFromInventory(player);
        }
    }
}
