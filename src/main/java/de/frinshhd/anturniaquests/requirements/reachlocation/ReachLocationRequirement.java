package de.frinshhd.anturniaquests.requirements.reachlocation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReachLocationRequirement extends BasicRequirement implements Listener {

    private final PlayerHashMap<UUID, List<ReachLocationModel>> playersCompleteObjectives = new PlayerHashMap<>();
    private final List<Location> registeredLocations = new ArrayList<>();

    public ReachLocationRequirement(boolean notGenerated) {
        super("reachLocation", true);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);

        getLoadedRequirementModels().forEach(rawRequirementModel -> {
            ReachLocationModel reachLocationModel = (ReachLocationModel) rawRequirementModel;
            registeredLocations.addAll(reachLocationModel.getAllLocationsBetween());
        });
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<ReachLocationModel> reachLocations = new ArrayList<>();
        for (Object object : objects) {
            reachLocations.add((ReachLocationModel) object);
        }

        reachLocations.forEach(reachLocation -> {
            if (hasPlayerReached(player.getUniqueId(), reachLocation.getAllLocationsBetween())) {
                lore.add(Translator.build("lore.requirements.reachLocation.fulfilled", new TranslatorPlaceholder("location1", reachLocation.getLocationFormated(reachLocation.getLocation1())), new TranslatorPlaceholder("location2", reachLocation.getLocationFormated(reachLocation.getLocation2())), new TranslatorPlaceholder("world", reachLocation.getWorld().getName())));
            } else {
                lore.add(Translator.build("lore.requirements.reachLocation.notFulfilled", new TranslatorPlaceholder("location1", reachLocation.getLocationFormated(reachLocation.getLocation1())), new TranslatorPlaceholder("location2", reachLocation.getLocationFormated(reachLocation.getLocation2())), new TranslatorPlaceholder("world", reachLocation.getWorld().getName())));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return ReachLocationModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        ReachLocationModel reachLocation = (ReachLocationModel) requirementModel;

        if (!hasPlayerReached(player.getUniqueId(), reachLocation.getAllLocationsBetween())) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.reachLocation", new TranslatorPlaceholder("location1", reachLocation.getLocationFormated(reachLocation.getLocation1())), new TranslatorPlaceholder("location2", reachLocation.getLocationFormated(reachLocation.getLocation2())), new TranslatorPlaceholder("world", reachLocation.getWorld().getName())));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModell : quest.getRequirement(getId())) {
            ReachLocationModel reachLocationModel = (ReachLocationModel) rawRequirementModell;

            if (!hasPlayerReached(player.getUniqueId(), reachLocationModel.getAllLocationsBetween())) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        List<Location> traveledLocations = getPlayerAllLocations(player.getUniqueId());
        List<BasicRequirementModel> modelsToCheck = getLoadedRequirementModels();

        for (Location traveledLocation : traveledLocations) {
            if (modelsToCheck.isEmpty()) {
                break;
            }

            List<BasicRequirementModel> modelsToRemove = new ArrayList<>();
            List<BasicRequirementModel> finalModelsToRemove = modelsToRemove;
            modelsToCheck.forEach(rawRequirementModel -> {
                ReachLocationModel reachLocationModel = (ReachLocationModel) rawRequirementModel;

                if (reachLocationModel.isLocationIncluded(traveledLocation)) {
                    if (!playersCompleteObjectives.containsKey(player.getUniqueId()) &&
                            playersCompleteObjectives.get(player.getUniqueId()) == null) {
                        playersCompleteObjectives.put(player.getUniqueId(), new ArrayList<>());
                    }

                    List<ReachLocationModel> reachLocationModels = playersCompleteObjectives.get(player.getUniqueId());
                    reachLocationModels.add(reachLocationModel);

                    playersCompleteObjectives.put(player.getUniqueId(), reachLocationModels);
                    finalModelsToRemove.add(rawRequirementModel);
                }
            });

            modelsToCheck.removeAll(finalModelsToRemove);
            modelsToRemove = new ArrayList<>();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getBlock().getLocation().equals(Objects.requireNonNull(event.getTo()).getBlock().getLocation())) {
            return;
        }

        if (!registeredLocations.contains(Objects.requireNonNull(event.getTo()).getBlock().getLocation())) {
            return;
        }

        savePlayerLocation(player.getUniqueId(), Objects.requireNonNull(event.getTo()).getBlock().getLocation());
    }

    public void savePlayerLocation(UUID playerUUID, Location location) {
        Gson gson = new Gson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        List<String> locationsList;

        if (!requirementsData.has("locations")) {
            locationsList = new ArrayList<>();
        } else {

            Type listType = new TypeToken<List<String>>() {
            }.getType();

            locationsList = gson.fromJson(requirementsData.getString("locations"), listType);
        }

        ArrayList<String> locationList = getLocationUnformated(location);

        if (!locationsList.isEmpty()) {
            for (String list : locationsList) {
                Type listType = new TypeToken<List<String>>() {
                }.getType();

                ArrayList<String> locationList2 = gson.fromJson(list, listType);

                if (locationList2.equals(locationList)) {
                    return;
                }
            }
        }

        locationsList.add(gson.toJson(locationList));
        requirementsData.put("locations", gson.toJson(locationsList));

        Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), requirementsData);
    }

    public ArrayList<String> getLocationUnformated(Location location) {
        ArrayList<String> list = new ArrayList<>();

        list.add(String.valueOf(location.getBlockX()));
        list.add(String.valueOf(location.getBlockY()));
        list.add(String.valueOf(location.getBlockZ()));
        list.add(Objects.requireNonNull(location.getWorld()).getName());

        return list;
    }

    public boolean hasPlayerReached(UUID playerUUID, List<Location> locations) {
        for (Location location : getPlayerAllLocations(playerUUID)) {
            if (locations.contains(location)) {
                return true;
            }
        }

        return false;
    }

    public List<Location> getPlayerAllLocations(UUID playerUUID) {
        Gson gson = new Gson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        List<String> locationsListRaw;

        if (!requirementsData.has("locations")) {
            locationsListRaw = new ArrayList<>();
        } else {
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            locationsListRaw = gson.fromJson(requirementsData.getString("locations"), listType);
        }

        List<Location> locationsList = new ArrayList<>();

        for (String list : locationsListRaw) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            ArrayList<String> locationList2 = gson.fromJson(list, listType);

            Location location = new Location(Bukkit.getWorld(locationList2.get(3)), Double.parseDouble(locationList2.get(0)), Double.parseDouble(locationList2.get(1)), Double.parseDouble(locationList2.get(2)));
            locationsList.add(location);
        }


        return locationsList;
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
        ReachLocationModel reachLocationModel = (ReachLocationModel) requirementModel;
        UUID playerUUID = player.getUniqueId();

        switch (reachLocationModel.getResetType()) {
            case NONE -> {
                break;
            }
            case ONLY_AMOUNT -> {
                Gson gson = new Gson();
                JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

                List<String> reachedLocations;
                Type listType = new TypeToken<List<String>>() {
                }.getType();

                if (requirementsData.isEmpty()) {
                    return;
                } else {
                    reachedLocations = gson.fromJson(requirementsData.getString("locations"), listType);
                }

                String locationKey = gson.toJson(getLocationUnformated(reachLocationModel.getLocation1()));

                if (reachedLocations.contains(locationKey)) {
                    reachedLocations.remove(locationKey);

                    requirementsData.put("locations", gson.toJson(reachedLocations, listType));
                    Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), requirementsData);
                }
            }
            case COMPLETE -> {
                Main.getRequirementManager().putPlayerRequirement(player.getUniqueId(), getId(), new JSONObject());
            }
        }
    }
}
