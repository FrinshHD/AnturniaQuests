package de.frinshhd.anturniaquests.requirements.placedblocks;

import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlacedBlocksModel extends BasicRequirementModel {

    private Material material = null;

    private int amount = 1;

    private ArrayList<String> worlds = null;

    public PlacedBlocksModel(LinkedHashMap<String, Object> map) {
        super(map, "placedBlocks");

        if (map.containsKey("material")) {
            this.material = Material.valueOf((String) map.get("material"));
        }

        if (map.containsKey("amount")) {
            this.amount = (int) map.get("amount");
        }

        if (map.containsKey("worlds")) {
            this.worlds = (ArrayList<String>) map.get("worlds");
        }
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public ArrayList<String> getWorlds() {
        return worlds;
    }
}
