package de.frinshhd.anturniaquests.requirements.reachlocation;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The ReachLocationModel class extends the BasicRequirementModel class.
 * It represents a requirement for a quest where a player must reach a certain location.
 */
public class ReachLocationModel extends BasicRequirementModel {

    @SerializedName("friendlyName")
    private String friendlyName = null;

    /**
     * The first boundary location of the area to be reached.
     */
    @SerializedName("location1")
    private ArrayList<Integer> location1 = new ArrayList<>();

    /**
     * The second boundary location of the area to be reached.
     */
    @SerializedName("location2")
    private ArrayList<Integer> location2 = new ArrayList<>();

    /**
     * The world in which the locations are located.
     */
    @SerializedName("world")
    private String world = "world";

    /**
     * Constructor for the ReachLocationModel class.
     * It initializes the class with a map of properties.
     *
     * @param map A map of properties for initializing the class
     */
    public ReachLocationModel(LinkedHashMap<String, Object> map) {
        super(map, "reachLocation");

        // Initialize the locations and world if they are present in the map
        if (map.containsKey("location1")) {
            Object locationObj = map.get("location1");
            this.location1 = (ArrayList<Integer>) locationObj;
        }

        if (map.containsKey("location2")) {
            Object locationObj = map.get("location2");
            this.location2 = (ArrayList<Integer>) locationObj;
        }

        if (map.containsKey("world")) {
            world = (String) map.get("world");
        }

        if (map.containsKey("friendlyName")) {
            friendlyName = (String) map.get("friendlyName");
        }
    }

    /**
     * Getter for the first location.
     *
     * @return The first location to reach
     */
    public Location getLocation1() {
        Location location;
        if (this.location1.size() < 3) {
            location = new Location(getWorld(), 0, 0, 0);
        } else {
            location = new Location(getWorld(), this.location1.get(0), this.location1.get(1), this.location1.get(2));
        }

        return location;
    }

    public void setLocation1(ArrayList<Integer> location1) {
        this.location1 = location1;
    }

    /**
     * Getter for the second location.
     *
     * @return The second location to reach
     */
    public Location getLocation2() {
        Location location;
        if (this.location2.size() < 3) {
            location = new Location(getWorld(), 0, 0, 0);
        } else {
            location = new Location(getWorld(), this.location2.get(0), this.location2.get(1), this.location2.get(2));
        }

        return location;
    }

    public void setLocation2(ArrayList<Integer> location2) {
        this.location2 = location2;
    }

    /**
     * Getter for the world property.
     *
     * @return The world in which the locations are located
     */
    public World getWorld() {
        return Main.getInstance().getServer().getWorld(this.world);
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * This method generates a list of all possible locations within the boundaries defined by two locations.
     * The two locations are retrieved from the location1 and location2 properties of the class.
     * The method first clones the two locations to avoid modifying the original ones.
     * Then, it determines the minimum and maximum x, y, and z coordinates between the two locations.
     * It then iterates over all possible x, y, and z coordinates within these boundaries and creates a new Location object for each.
     * These Location objects are added to a list, which is then returned.
     *
     * @return A list of all locations within the boundaries defined by location1 and location2
     */
    public List<Location> getAllLocationsBetween() {
        // Clone the first and second locations to avoid modifying the original ones
        Location loc1 = getLocation1().clone();
        Location loc2 = getLocation2().clone();

        // Initialize the list to store the locations
        List<Location> locations = new ArrayList<>();

        // Determine the minimum and maximum x, y, and z coordinates between the two locations
        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());

        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());

        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        // Iterate over all possible x, y, and z coordinates within these boundaries
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    // Create a new Location object for each coordinate and add it to the list
                    locations.add(new Location(loc1.getWorld(), x, y, z));
                }
            }
        }

        // Return the list of locations
        return locations;
    }

    public boolean isLocationIncluded(Location location) {
        return getAllLocationsBetween().contains(location);
    }

    public String getLocationFormated(Location location) {
        if (getFriendlyName() != null) {
            return getFriendlyName();
        }

        String builder = "[X: " +
                location.getX() +
                " Y: " +
                location.getY() +
                " Z: " +
                location.getZ() +
                "]";

        return builder;
    }

    /*public String getLocationFormated() {
        if (getFriendlyName() != null) {
            return getFriendlyName();
        }

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        builder.append(getLocation1().getX());
        builder.append(" ");
        builder.append(getLocation1().getY());
        builder.append(" ");
        builder.append(getLocation1().getZ());
        builder.append("]");
        builder.append(" - ");
        builder.append("[");
        builder.append(getLocation2().getX());
        builder.append(" ");
        builder.append(getLocation2().getY());
        builder.append(" ");
        builder.append(getLocation2().getZ());
        builder.append("]");

        return builder.toString();
    }  */
}