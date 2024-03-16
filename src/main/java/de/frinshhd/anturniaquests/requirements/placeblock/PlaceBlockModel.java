package de.frinshhd.anturniaquests.requirements.placeblock;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlaceBlockModel extends BasicRequirementModel {

    public ArrayList<Integer> location = new ArrayList<>();
    private String world = "world";

    private Material material = null;

    public PlaceBlockModel(LinkedHashMap<String, Object> map) {
        super(map);

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

    public Location getLocation() {

        Location location;
        if (this.location.size() < 3) {
            location = new Location(getWorld(), 0, 0, 0);
        } else {
            location = new Location(getWorld(), this.location.get(0), this.location.get(1), this.location.get(2));
        }

        return location;
    }

    public World getWorld() {
        return Main.getInstance().getServer().getWorld(this.world);
    }

    public Material getMaterial() {
        return material;
    }
}
