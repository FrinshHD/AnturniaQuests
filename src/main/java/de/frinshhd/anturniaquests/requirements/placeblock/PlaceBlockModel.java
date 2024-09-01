package de.frinshhd.anturniaquests.requirements.placeblock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ResetType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlaceBlockModel extends BasicRequirementModel {

    @JsonProperty
    public ArrayList<Integer> location = new ArrayList<>();

    @JsonProperty
    private String world = "world";

    @JsonProperty
    private Material material = null;

    @JsonIgnore
    public PlaceBlockModel(LinkedHashMap<String, Object> map) {
        super(map, "placeBlocks");

        if (map.containsKey("location")) {
            Object locationObj = map.get("location");

            this.location = (ArrayList<Integer>) locationObj;
        }

        if (map.containsKey("world")) {
            world = (String) map.get("world");
        }

        if (map.containsKey("material")) {
            material = Material.valueOf((String) map.get("material"));
        }
    }

    @JsonIgnore
    public Location getLocation() {

        Location location;
        if (this.location.size() < 3) {
            location = new Location(getWorld(), 0, 0, 0);
        } else {
            location = new Location(getWorld(), this.location.get(0), this.location.get(1), this.location.get(2));
        }

        return location;
    }

    @JsonIgnore
    public void setLocation(ArrayList<Integer> location) {
        this.location = location;
    }

    @JsonIgnore
    public World getWorld() {
        return Main.getInstance().getServer().getWorld(this.world);
    }

    @JsonIgnore
    public void setWorld(String world) {
        this.world = world;
    }

    @JsonIgnore
    public Material getMaterial() {
        return material;
    }

    @JsonIgnore
    public void setMaterial(Material material) {
        this.material = material;
    }
}

