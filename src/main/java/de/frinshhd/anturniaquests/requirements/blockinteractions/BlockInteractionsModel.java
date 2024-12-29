package de.frinshhd.anturniaquests.requirements.blockinteractions;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BlockInteractionsModel extends BasicRequirementModel {

    @SerializedName("interactActions")
    public List<String> interactActions = new ArrayList<>(List.of("LEFT_CLICK", "RIGHT_CLICK"));

    @SerializedName("location")
    public ArrayList<Integer> location = new ArrayList<>();

    @SerializedName("world")
    private String world = "world";

    public BlockInteractionsModel(LinkedHashMap<String, Object> map) {
        super(map, "blockInteractions");

        if (map.containsKey("interactActions")) {
            this.interactActions = (List<String>) map.get("interactActions");
        }

        if (map.containsKey("location")) {
            Object locationObj = map.get("location");

            this.location = (ArrayList<Integer>) locationObj;
        }

        if (map.containsKey("world")) {
            world = (String) map.get("world");
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

    public void setLocation(ArrayList<Integer> location) {
        this.location = location;
    }

    public World getWorld() {
        return Main.getInstance().getServer().getWorld(this.world);
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public List<String> getInteractActions() {
        return interactActions;
    }

    public void setInteractActions(List<String> interactActions) {
        this.interactActions = interactActions;
    }
}