package de.frinshhd.anturniaquests.requirements.blockinteractions;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BlockInteractionsRequirement extends BasicRequirement implements Listener {

    public HashMap<Location, List<String>> registeredLocations = new HashMap<>();

    public BlockInteractionsRequirement(boolean notGenerated) {
        super("blockInteractions", true);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);

        getLoadedRequirementModels().forEach(rawRequirementModel -> {
            BlockInteractionsModel blockInteractionsModel = (BlockInteractionsModel) rawRequirementModel;
            registeredLocations.put(blockInteractionsModel.getLocation(), blockInteractionsModel.getInteractActions());
        });
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<BlockInteractionsModel> interactions = new ArrayList<>();
        for (Object object : objects) {
            interactions.add((BlockInteractionsModel) object);
        }

        interactions.forEach(interaction -> {
            if (hasPlayerInteracted(player.getUniqueId(), interaction.getLocation())) {
                lore.add(Translator.build("lore.requirements.blockInteraction.fulfilled", new TranslatorPlaceholder("location", interaction.location.toString().substring(1, interaction.location.toString().length() - 1))));
            } else {
                lore.add(Translator.build("lore.requirements.blockInteraction.notFulfilled", new TranslatorPlaceholder("location", interaction.location.toString().substring(1, interaction.location.toString().length() - 1))));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return BlockInteractionsModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        BlockInteractionsModel interaction = (BlockInteractionsModel) requirementModel;

        if (!hasPlayerInteracted(player.getUniqueId(), interaction.getLocation())) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.blockInteraction", new TranslatorPlaceholder("location", interaction.location.toString().substring(1, interaction.location.toString().length() - 1))));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModell : quest.getRequirement(getId())) {
            BlockInteractionsModel interactionsModel = (BlockInteractionsModel) rawRequirementModell;

            if (!hasPlayerInteracted(player.getUniqueId(), interactionsModel.getLocation())) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null) {
            return;
        }

        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!registeredLocations.containsKey(event.getClickedBlock().getLocation())) {
            return;
        }

        String action = event.getAction().toString();
        int lastPart = action.lastIndexOf('_');

        if (lastPart == -1) {
            return;
        }

        action = action.substring(0, lastPart);

        if (!registeredLocations.get(event.getClickedBlock().getLocation()).contains(action)) {
            return;
        }

        savePlayerInteraction(player.getUniqueId(), event.getClickedBlock().getLocation());
    }

    public void savePlayerInteraction(UUID playerUUID, Location location) {
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

        return list;
    }

    public boolean hasPlayerInteracted(UUID playerUUID, Location location) {
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

        if (locationsList.isEmpty()) {
            return false;
        }


        for (String list : locationsList) {
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            ArrayList<String> locationList2 = gson.fromJson(list, listType);

            if (locationList2.equals(getLocationUnformated(location))) {
                return true;
            }
        }

        return false;
    }
}
