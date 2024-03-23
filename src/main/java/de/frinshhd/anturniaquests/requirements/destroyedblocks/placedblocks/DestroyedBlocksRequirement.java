package de.frinshhd.anturniaquests.requirements.destroyedblocks.placedblocks;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class DestroyedBlocksRequirement extends BasicRequirement implements Listener {
    public DestroyedBlocksRequirement(boolean notGenerated) {
        super("destroyedBlocks", false);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<DestroyedBlocksModel> placedBlocks = new ArrayList<>();
        for (Object object : objects) {
            placedBlocks.add((DestroyedBlocksModel) object);
        }

        placedBlocks.forEach(placedBlock -> {

            if (hasPlayerDestroyedBlocks(player.getUniqueId(), placedBlock.getWorlds(), placedBlock.getMaterial(), placedBlock.getAmount())) {
                lore.add(Translator.build("lore.requirements.destroyedBlocks.fulfilled", new TranslatorPlaceholder("material", placedBlock.getDisplayName()), new TranslatorPlaceholder("amountPlaced", String.valueOf(getPlayerDestroyedBlocks(player.getUniqueId(), placedBlock.getWorlds(), placedBlock.getMaterial()))), new TranslatorPlaceholder("amount", String.valueOf(placedBlock.getAmount())), new TranslatorPlaceholder("worlds", placedBlock.getWorldFormated())));
            } else {
                lore.add(Translator.build("lore.requirements.destroyedBlocks.notFulfilled", new TranslatorPlaceholder("material", placedBlock.getDisplayName()), new TranslatorPlaceholder("amountPlaced", String.valueOf(getPlayerDestroyedBlocks(player.getUniqueId(), placedBlock.getWorlds(), placedBlock.getMaterial()))), new TranslatorPlaceholder("amount", String.valueOf(placedBlock.getAmount())), new TranslatorPlaceholder("worlds", placedBlock.getWorldFormated())));
            }
        });

        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return DestroyedBlocksModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        DestroyedBlocksModel destroyedBlocksModel = (DestroyedBlocksModel) requirementModel;

        if (!hasPlayerDestroyedBlocks(player.getUniqueId(), destroyedBlocksModel.getWorlds(), destroyedBlocksModel.getMaterial(), destroyedBlocksModel.getAmount())) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.destroyedBlocks", new TranslatorPlaceholder("material", destroyedBlocksModel.getDisplayName()), new TranslatorPlaceholder("amountPlaced", String.valueOf(getPlayerDestroyedBlocks(player.getUniqueId(), destroyedBlocksModel.getWorlds(), destroyedBlocksModel.getMaterial()))), new TranslatorPlaceholder("amount", String.valueOf(destroyedBlocksModel.getAmount())), new TranslatorPlaceholder("worlds", destroyedBlocksModel.getWorldFormated())));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModel : quest.getRequirement(getId())) {
            DestroyedBlocksModel destroyedBlocksModel = (DestroyedBlocksModel) rawRequirementModel;

            if (!hasPlayerDestroyedBlocks(player.getUniqueId(), destroyedBlocksModel.getWorlds(), destroyedBlocksModel.getMaterial(), destroyedBlocksModel.getAmount())) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        addPlayerDestroyedBlock(playerUUID, event.getBlock().getType(), Objects.requireNonNull(event.getBlock().getLocation().getWorld()).getName());
    }

    public void addPlayerDestroyedBlock(UUID playerUUID, Material material, String world) {
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

    public boolean hasPlayerDestroyedBlocks(UUID playerUUID, ArrayList<String> worldsToLookAt, Material material, int amount) {
        return getPlayerDestroyedBlocks(playerUUID, worldsToLookAt, material) >= amount;
    }

    public int getPlayerDestroyedBlocks(UUID playerUUID, ArrayList<String> worldsToLookAt, Material material) {
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
}
