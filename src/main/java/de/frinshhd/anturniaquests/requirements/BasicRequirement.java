package de.frinshhd.anturniaquests.requirements;

import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class BasicRequirement {

    private final String id;

    public BasicRequirement(String id, boolean indexing) {
        this.id = id;
    }


    public String getId() {
        return this.id;
    }

    public abstract ArrayList<String> getLore(Player player, ArrayList<Object> objects);

    public abstract Class getModellClass();

    public abstract void sendPlayerMissing(Player player, BasicRequirementModel requirementModel);

    public abstract boolean check(Player player, String questID);
}
