package de.frinshhd.anturniaquests.requirements.placedblocks;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlacedBlocksModel extends BasicRequirementModel {

    @SerializedName("material")
    private Material material = null;

    @SerializedName("amount")
    private int amount = 1;

    @SerializedName("worlds")
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

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDisplayName() {
        return new TranslatableComponent(getMaterial().getTranslationKey()).toPlainText();
    }

    public ArrayList<String> getWorlds() {
        return this.worlds;
    }

    public void setWorlds(ArrayList<String> worlds) {
        this.worlds = worlds;
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