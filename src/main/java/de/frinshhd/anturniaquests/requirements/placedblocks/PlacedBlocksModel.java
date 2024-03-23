package de.frinshhd.anturniaquests.requirements.placedblocks;

import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlacedBlocksModel extends BasicRequirementModel {

    private Material material = null;

    private int amount = 1;

    private ArrayList<String> worlds = new ArrayList<>();

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

    public String getDisplayName() {
        return new TranslatableComponent(getMaterial().getTranslationKey()).toPlainText();
    }

    public ArrayList<String> getWorlds() {
        return this.worlds;
        /*ArrayList<String> worlds = new ArrayList<>();

        this.worlds.forEach(worldName -> {
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                return;
            }

            worlds.add(world);
        });

        return worlds; */
    }

    public String getWorldFormated() {
        if (getWorlds().isEmpty()) {
            return "";
        }

        ArrayList<String> worlds = (ArrayList<String>) this.getWorlds().clone();
        String worldsString = worlds.toString();
        worldsString = worldsString.substring(1, worldsString.length() - 1);

        return worldsString;
    }
}
