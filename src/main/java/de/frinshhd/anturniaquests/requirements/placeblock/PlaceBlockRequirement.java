package de.frinshhd.anturniaquests.requirements.placeblock;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

public class PlaceBlockRequirement extends BasicRequirement implements Listener {

    public List<Location> registeredLocations = new ArrayList<>();

    public PlaceBlockRequirement(boolean notGenerated) {
        super("placeBlocks", true);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);

        getLoadedRequirementModels().forEach(rawRequirementModel -> {
            PlaceBlockModel placeBlockModel = (PlaceBlockModel) rawRequirementModel;

            registeredLocations.add(placeBlockModel.getLocation());
        });
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<PlaceBlockModel> placeBlockModels = new ArrayList<>();
        for (Object object : objects) {
            placeBlockModels.add((PlaceBlockModel) object);
        }

        placeBlockModels.forEach(placeBlockModel -> {
            if (hasPlayerInteracted(player.getUniqueId(), placeBlockModel.getLocation(), placeBlockModel.getMaterial())) {
                lore.add(TranslationManager.getInstance().build("lore.requirements.placeBlock.fulfilled", new Translatable("location", placeBlockModel.location.toString().substring(1, placeBlockModel.location.toString().length() - 1))));
            } else {
                lore.add(TranslationManager.getInstance().build("lore.requirements.placeBlock.notFulfilled", new Translatable("location", placeBlockModel.location.toString().substring(1, placeBlockModel.location.toString().length() - 1))));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return PlaceBlockModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        PlaceBlockModel placeBlockModel = (PlaceBlockModel) requirementModel;

        if (!hasPlayerInteracted(player.getUniqueId(), placeBlockModel.getLocation(), placeBlockModel.getMaterial())) {
            ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.missingRequirements.placeBlock", new Translatable("location", placeBlockModel.location.toString().substring(1, placeBlockModel.location.toString().length() - 1))));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModel : quest.getRequirement(getId())) {
            PlaceBlockModel placeBlockModel = (PlaceBlockModel) rawRequirementModel;

            if (!hasPlayerInteracted(player.getUniqueId(), placeBlockModel.getLocation(), placeBlockModel.getMaterial())) {
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelled()) {
            return;
        }

        if (!registeredLocations.contains(event.getBlockPlaced().getLocation())) {
            return;
        }

        savePlayerPlaceBlock(player.getUniqueId(), event.getBlockPlaced().getLocation(), event.getBlockPlaced().getType());
    }

    public void savePlayerPlaceBlock(UUID playerUUID, Location location, Material material) {
        Gson gson = new Gson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        //Location, Material
        HashMap<String, String> locationsList;

        if (!requirementsData.has("placeBlocks")) {
            locationsList = new HashMap<>();
        } else {
            Type mapType = new TypeToken<HashMap<String, String>>() {
            }.getType();

            locationsList = gson.fromJson(requirementsData.getString("placeBlocks"), mapType);
        }

        ArrayList<String> locationList = getLocationUnformated(location);

        if (!locationsList.isEmpty()) {
            for (Map.Entry<String, String> map : locationsList.entrySet()) {
                Type listType = new TypeToken<List<String>>() {
                }.getType();

                ArrayList<String> locationList2 = gson.fromJson(map.getKey(), listType);

                if (locationList2.equals(locationList)) {
                    if (material == null) {
                        return;
                    }

                    if (Material.valueOf(map.getValue()).equals(material)) {
                        return;
                    }
                }
            }
        }

        Type mapType = new TypeToken<HashMap<String, String>>() {
        }.getType();

        locationsList.put(gson.toJson(locationList), gson.toJson(material));

        requirementsData.put("placeBlocks", gson.toJson(locationsList, mapType));

        Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), requirementsData);
    }

    public ArrayList<String> getLocationUnformated(Location location) {
        ArrayList<String> list = new ArrayList<>();

        list.add(String.valueOf(location.getBlockX()));
        list.add(String.valueOf(location.getBlockY()));
        list.add(String.valueOf(location.getBlockZ()));

        return list;
    }

    public boolean hasPlayerInteracted(UUID playerUUID, Location location, Material material) {
        Gson gson = new Gson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        //Location, Material
        HashMap<String, String> locationsList;

        if (!requirementsData.has("placeBlocks")) {
            locationsList = new HashMap<>();
        } else {
            Type mapType = new TypeToken<HashMap<String, String>>() {
            }.getType();

            locationsList = gson.fromJson(requirementsData.getString("placeBlocks"), mapType);
        }

        if (locationsList.isEmpty()) {
            return false;
        }


        for (Map.Entry<String, String> map : locationsList.entrySet()) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            ArrayList<String> locationList2 = gson.fromJson(map.getKey(), listType);

            if (locationList2.equals(getLocationUnformated(location))) {
                if (material == null) {
                    return true;
                }

                if (Material.valueOf(map.getValue()).equals(material)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
        PlaceBlockModel placeBlockModel = (PlaceBlockModel) requirementModel;
        UUID playerUUID = player.getUniqueId();

        switch (placeBlockModel.getResetType()) {
            case NONE -> {
                break;
            }
            case ONLY_AMOUNT -> {
                Gson gson = new Gson();
                JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

                HashMap<String, String> placedBlocks;
                Type mapType = new TypeToken<HashMap<String, String>>() {
                }.getType();

                if (requirementsData.isEmpty()) {
                    return;
                } else {
                    placedBlocks = gson.fromJson(requirementsData.getString("placeBlocks"), mapType);
                }

                String locationKey = gson.toJson(getLocationUnformated(placeBlockModel.getLocation()));

                if (placedBlocks.containsKey(locationKey)) {
                    int currentCount = Integer.parseInt(placedBlocks.get(locationKey));
                    int newCount = currentCount - 1; // Decrease by 1 since only one block can be placed at a location

                    if (newCount > 0) {
                        placedBlocks.put(locationKey, String.valueOf(newCount));
                    } else {
                        placedBlocks.remove(locationKey);
                    }

                    requirementsData.put("placeBlocks", gson.toJson(placedBlocks, mapType));
                    Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), requirementsData);
                }
            }
            case COMPLETE -> {
                Main.getRequirementManager().putPlayerRequirement(player.getUniqueId(), getId(), new JSONObject());
            }
        }
    }
}
