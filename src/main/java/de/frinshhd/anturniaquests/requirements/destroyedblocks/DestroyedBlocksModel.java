package de.frinshhd.anturniaquests.requirements.destroyedblocks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DestroyedBlocksModel extends BasicRequirementModel {

    @JsonProperty
    private Material material = null;

    @JsonProperty
    private int amount = 1;

    @JsonProperty
    private ArrayList<String> worlds = new ArrayList<>();

    @JsonIgnore
    public DestroyedBlocksModel(LinkedHashMap<String, Object> map) {
        super(map, "destroyedBlocks");

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

    @JsonIgnore
    public void setMaterial(Material material) {
        this.material = material;
    }

    @JsonIgnore
    public Material getMaterial() {
        return material;
    }

    @JsonIgnore
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public int getAmount() {
        return amount;
    }

    @JsonIgnore
    public String getDisplayName() {
        return new TranslatableComponent(getMaterial().getTranslationKey()).toPlainText();
    }

    @JsonIgnore
    public void setWorlds(ArrayList<String> worlds) {
        this.worlds = worlds;
    }

    @JsonIgnore
    public ArrayList<String> getWorlds() {
        return this.worlds;
    }

    @JsonIgnore
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
