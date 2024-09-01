package de.frinshhd.anturniaquests.requirements.placedblocks;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlacedBlocksRequirement extends BasicRequirement implements Listener {
    public PlacedBlocksRequirement(boolean notGenerated) {
        super("placedBlocks", false);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<PlacedBlocksModel> placedBlocks = new ArrayList<>();
        for (Object object : objects) {
            placedBlocks.add((PlacedBlocksModel) object);
        }

        placedBlocks.forEach(placedBlock -> {

            if (hasPlayerPlacedBlocks(player.getUniqueId(), placedBlock.getWorlds(), placedBlock.getMaterial(), placedBlock.getAmount())) {
                lore.add(Translator.build("lore.requirements.placedBlocks.fulfilled", new TranslatorPlaceholder("material", placedBlock.getDisplayName()), new TranslatorPlaceholder("amountPlaced", String.valueOf(getPlayerPlacedBlocks(player.getUniqueId(), placedBlock.getWorlds(), placedBlock.getMaterial()))), new TranslatorPlaceholder("amount", String.valueOf(placedBlock.getAmount())), new TranslatorPlaceholder("worlds", placedBlock.getWorldFormated())));
            } else {
                lore.add(Translator.build("lore.requirements.placedBlocks.notFulfilled", new TranslatorPlaceholder("material", placedBlock.getDisplayName()), new TranslatorPlaceholder("amountPlaced", String.valueOf(getPlayerPlacedBlocks(player.getUniqueId(), placedBlock.getWorlds(), placedBlock.getMaterial()))), new TranslatorPlaceholder("amount", String.valueOf(placedBlock.getAmount())), new TranslatorPlaceholder("worlds", placedBlock.getWorldFormated())));
            }
        });

        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return PlacedBlocksModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        PlacedBlocksModel placedBlocksModel = (PlacedBlocksModel) requirementModel;

        if (!hasPlayerPlacedBlocks(player.getUniqueId(), placedBlocksModel.getWorlds(), placedBlocksModel.getMaterial(), placedBlocksModel.getAmount())) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.placedBlocks", new TranslatorPlaceholder("material", placedBlocksModel.getDisplayName()), new TranslatorPlaceholder("amountPlaced", String.valueOf(getPlayerPlacedBlocks(player.getUniqueId(), placedBlocksModel.getWorlds(), placedBlocksModel.getMaterial()))), new TranslatorPlaceholder("amount", String.valueOf(placedBlocksModel.getAmount())), new TranslatorPlaceholder("worlds", placedBlocksModel.getWorldFormated())));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModel : quest.getRequirement(getId())) {
            PlacedBlocksModel placedBlocksModel = (PlacedBlocksModel) rawRequirementModel;

            if (!hasPlayerPlacedBlocks(player.getUniqueId(), placedBlocksModel.getWorlds(), placedBlocksModel.getMaterial(), placedBlocksModel.getAmount())) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        addPlayerPlacedBlock(playerUUID, event.getBlockPlaced().getType(), Objects.requireNonNull(event.getBlockPlaced().getLocation().getWorld()).getName());
    }

    public void addPlayerPlacedBlock(UUID playerUUID, Material material, String world) {
        Gson gson = new Gson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        HashMap<String, HashMap<String, Integer>> worlds;

        Type mapType = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
        }.getType();

        if (requirementsData.isEmpty()) {
            worlds = new HashMap<>();
        } else {
            worlds = gson.fromJson(requirementsData.toString(), mapType);
        }

        if (!worlds.containsKey(world)) {
            HashMap<String, Integer> blocks = new HashMap<>();
            blocks.put(material.toString(), 1);

            worlds.put(world, blocks);
            Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), new JSONObject(worlds));
            return;
        }

        HashMap<String, Integer> blocks = worlds.get(world);

        if (!blocks.containsKey(material.toString())) {
            blocks.put(material.toString(), 1);
        } else {
            blocks.put(material.toString(), blocks.get(material.toString()) + 1);
        }

        worlds.put(world, blocks);

        Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), new JSONObject(worlds));
    }

    public boolean hasPlayerPlacedBlocks(UUID playerUUID, ArrayList<String> worldsToLookAt, Material material, int amount) {
        return getPlayerPlacedBlocks(playerUUID, worldsToLookAt, material) >= amount;
    }

    public int getPlayerPlacedBlocks(UUID playerUUID, ArrayList<String> worldsToLookAt, Material material) {
        Gson gson = new Gson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        HashMap<String, HashMap<String, Integer>> worlds;

        Type mapType = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
        }.getType();

        if (requirementsData.isEmpty()) {
            worlds = new HashMap<>();
        } else {
            worlds = gson.fromJson(requirementsData.toString(), mapType);
        }

        int index = 0;

        if (worldsToLookAt.isEmpty()) {
            worldsToLookAt.addAll(worlds.keySet().stream().toList());
        }

        for (String worldName : worldsToLookAt) {

            if (!worlds.containsKey(worldName)) {
                continue;
            }

            if (!worlds.get(worldName).containsKey(material.toString())) {
                continue;
            }

            index += worlds.get(worldName).get(material.toString());
        }

        return index;
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
        PlacedBlocksModel placedBlocksModel = (PlacedBlocksModel) requirementModel;
        UUID playerUUID = player.getUniqueId();

        switch (placedBlocksModel.getResetType()) {
            case NONE -> {
                break;
            }
            case ONLY_AMOUNT -> {
                Gson gson = new Gson();
                JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

                HashMap<String, HashMap<String, Integer>> worlds;
                Type mapType = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                }.getType();

                if (requirementsData.isEmpty()) {
                    return;
                } else {
                    worlds = gson.fromJson(requirementsData.toString(), mapType);
                }

                for (String world : placedBlocksModel.getWorlds()) {
                    if (worlds.containsKey(world)) {
                        HashMap<String, Integer> blocks = worlds.get(world);
                        String materialKey = placedBlocksModel.getMaterial().toString();

                        if (blocks.containsKey(materialKey)) {
                            int currentCount = blocks.get(materialKey);
                            int newCount = currentCount - placedBlocksModel.getAmount(); // Decrease by 1

                            if (newCount > 0) {
                                blocks.put(materialKey, newCount);
                            } else {
                                blocks.remove(materialKey);
                            }

                            worlds.put(world, blocks);
                        }
                    }
                }

                requirementsData = new JSONObject(gson.toJson(worlds, mapType));
                Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), requirementsData);
            }
            case COMPLETE -> {
                Main.getRequirementManager().putPlayerRequirement(player.getUniqueId(), getId(), new JSONObject());
            }
        }
    }

}
